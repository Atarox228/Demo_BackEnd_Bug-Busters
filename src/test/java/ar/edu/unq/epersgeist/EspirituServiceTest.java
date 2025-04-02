package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EspirituServiceTest {

    private EspirituService espirituService = new EspirituServiceImpl(new HibernateEspirituDAO(), new HibernateMediumDAO());
    private MediumServiceImpl mediumService = new MediumServiceImpl(new HibernateMediumDAO());
    private Espiritu Casper;
    private Espiritu Jinn;
    private Espiritu Oni;
    private Espiritu Jorge;
    private Medium medium;
    private Medium medium2;
    private Ubicacion Bernal;

    @BeforeEach
    void setUp(){
        Casper = new Espiritu("Angelical", 0, "Casper", new Ubicacion("Quilmes"));
        Oni = new Espiritu("Demoniaco", 95, "Otakemaru");
        Jinn = new Espiritu("Demoniaco", 100, "Marids");
        Jorge = new Espiritu("Humano", 20, "Jorge");
        medium = new Medium("lala", 100, 50);
        medium2 = new Medium("lolo", 100, 60);
    }


    @Test
    void guardarEspiritu(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        assertNotNull(Casper.getId());
        assertNotNull(Oni.getId());
        assertNotNull(Jinn.getId());
        System.out.println("El id es: " + Casper.getId());
        System.out.println("El id es: " + Oni.getId());
        System.out.println("El id es: " + Jinn.getId());
    }


    @Test
    void actualizarEspiritu(){
        espirituService.crear(Casper);
        Espiritu sinActualizar = espirituService.recuperar(Casper.getId());
        Casper.setNombre("Lala");
        System.out.println("El nombre ahora es: " + Casper.getNombre());
        espirituService.actualizar(Casper);
        Espiritu actualizado = espirituService.recuperar(Casper.getId());
        assertEquals(sinActualizar.getId(), Casper.getId());
        assertEquals(sinActualizar.getNombre(), "Casper");
        assertEquals(actualizado.getNombre(), "Lala");
    }


    @Test
    void recuperarEspiritu(){
        espirituService.crear(Casper);
        Espiritu espirituRecuperado = espirituService.recuperar(Casper.getId());
        assertEquals(espirituRecuperado.getNombre(), "Casper");
        assertEquals(espirituRecuperado.getTipo(), "Angelical");
        assertEquals(espirituRecuperado.getNivelDeConexion(), 0);
    }

    @Test
    void recuperarTodosLosEspiritus(){
        //Se deben obtener alfabeticamente
        espirituService.crear(Oni);
        espirituService.crear(Casper);
        espirituService.crear(Jinn);
        List<Espiritu> espiritus = espirituService.recuperarTodos();
        assertEquals(espiritus.size(), 3);
        assertEquals(espiritus.get(0).getNombre(), "Casper");
        assertEquals(espiritus.get(1).getNombre(), "Marids");
        assertEquals(espiritus.get(2).getNombre(), "Otakemaru");
    }

    @Test
    void eliminarEspiritu(){
        espirituService.crear(Casper);
        Long espirituId = Casper.getId();
        assertNotNull(espirituService.recuperar(espirituId));
        espirituService.eliminar(Casper);
        assertNull(espirituService.recuperar(espirituId));
    }

    @Test
    void obtenerEspiritusDemoniacos(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos();
        assertEquals(demonios.size(), 2);
    }

    @Test
    void verificarOrdenDeEspiritusDemoniacos(){
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos();
        assertEquals(demonios.get(0).getNombre(), "Marids");
        assertEquals(demonios.get(1).getNombre(), "Otakemaru");

    }

//    @Test
//    void ConectarConMedium(){
//        mediumService.guardar(medium);
//        espirituService.crear(Casper);
//        Medium mediumConectado = espirituService.conectar(Casper.getId(), medium.getId());
//        assertEquals(mediumConectado.getId(), medium.getId());
//    }

    @AfterEach
    void cleanup() {
        espirituService.eliminarTodo();
    }

    //    @Test
//    void guardarEspirituYaExistenteEnDB(){
//        espirituService.guardar(Casper);
//        Espiritu espirituRecuperado = espirituService.recuperar(Casper.getId());
//        Casper.setNombre("Lala");
//        espirituService.guardar(Casper);
//        assertEquals(espirituRecuperado.getId(), Casper.getId());
//        assertEquals(espirituRecuperado.getNombre(), "Casper");
//        assertEquals(espirituService.recuperar(Casper.getId()).getNombre(), "Lala");
//    }


//
//    @Test
//    void recuperarEspirituEliminado(){
//        Espiritu espirituNuevo = espirituService.crear(Oni);
//        long idEspN = espirituNuevo.getId();
//        espirituService.eliminar(idEspN);
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            espirituService.recuperar(idEspN);
//        });
//        assertTrue(exception.getMessage().contains("No se encontro el espiritu"));
//    }
//
//
//    @Test
//    void eliminarEspiritu() {
//        Espiritu espirituActualizado = espirituService.crear(Casper);
//        long idEsp1 = espirituActualizado.getId();
//        espirituService.eliminar(espirituActualizado.getId());
//        Espiritu espiritu2 = espirituService.crear(Casper);
//        long idEsp2 = espiritu2.getId();
//        //Si existiera casper en la base de datos, no podria crear otro
//        assertNotNull(idEsp2);
//    }
//
//    @Test
//    void eliminarEspirituEliminado() {
//        Espiritu espirituActualizado = espirituService.crear(Jinn);
//        espirituService.eliminar(espirituActualizado.getId());
//        espirituService.eliminar(espirituActualizado.getId());
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            espirituService.recuperar(espirituActualizado.getId());
//        });
//        assertTrue(exception.getMessage().contains("No se encontro el espiritu"));
//    }
//
//    @Test
//    void recuperarTodosLosEspiritus(){
//
//        Espiritu esp1 = espirituService.crear(Jinn);
//        Espiritu esp2 = espirituService.crear(Oni);
//        Espiritu esp3 = espirituService.crear(Casper);
//
//        List<Espiritu>espiritus = espirituService.recuperarTodos();
//        List<String> nombresObtenidos = new ArrayList<>();
//        for (Espiritu p : espiritus){
//            nombresObtenidos.add(p.getNombre());
//        }
//
//        List<String> nombresEsperados = new ArrayList<>();
//        nombresEsperados.add("Casper");
//        nombresEsperados.add("Marids");
//        nombresEsperados.add("Otakemaru");
//        assertEquals(nombresEsperados, nombresObtenidos);
//    }
//
//    @Test
//    void recuperarTodosSinEspiritus(){
//        List<Espiritu>espiritus = espirituService.recuperarTodos();
//        assertArrayEquals(espiritus.toArray(),new ArrayList<>().toArray());
//    }
//
//    @Test
//    void actualizarEspiritu(){
//        Medium marta = new Medium("marta",100,10);
//        Espiritu esp1 = espirituService.crear(Casper);
//        espirituService.conectar(esp1.getId(),marta);
//        Espiritu espirituActualizado = espirituService.recuperar(esp1.getId());
//        assertEquals(10,espirituActualizado.getNivelDeConexion());
//    }
//
//    @Test
//    void actualizarEspirituNoExistente(){
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            espirituService.actualizar(Oni);
//        });
//        assertTrue(exception.getMessage().contains("Espiritu sin id asignada"));
//    }
//
//    @Test
//    void actualizarEspirituEliminado(){
//        Medium tita = new Medium("tita",100,10);
//        Espiritu esp1 = espirituService.crear(Casper);
//        espirituService.conectar(esp1.getId(), tita);
//        espirituService.eliminar(esp1.getId());
//        espirituService.actualizar(esp1);
//        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//            espirituService.recuperar(esp1.getId());
//        });
//        assertTrue(exception.getMessage().contains("No se encontro el espiritu"));
//    }
//
//    @Test
//    void conexionEspirituMedium(){
//        Medium lizzie = new Medium("Lizzie",100,10);
//        Espiritu espiritu = espirituService.crear(Casper);
//        espirituService.conectar(espiritu.getId(), lizzie);
//        Espiritu espirituActualizado = espirituService.recuperar(espiritu.getId());
//        assertEquals(espirituActualizado.getNivelDeConexion(), espiritu.getNivelDeConexion()+10);
//    }
//
//    @Test
//    void conexionEspirituMediumYaHecha(){
//        Medium sosa = new Medium("sosa",100,10);
//        Espiritu espiritu = espirituService.crear(Casper);
//        int elcoso = espiritu.getNivelDeConexion();
//        espirituService.conectar(espiritu.getId(), sosa);
//        espirituService.conectar(espiritu.getId(), sosa);
//        Espiritu espirituActualizado = espirituService.recuperar(espiritu.getId());
//        assertEquals(espirituActualizado.getNivelDeConexion(), elcoso+10);
//    }
//
//    @Test
//    void conexionEspirituMediumLvlMax(){
//        Medium micho = new Medium("micho",100,10);
//        Espiritu espiritu = espirituService.crear(Oni);
//        espirituService.conectar(espiritu.getId(), micho);
//        Espiritu espirituActualizado = espirituService.recuperar(espiritu.getId());
//        assertEquals(espirituActualizado.getNivelDeConexion(), 100);
//    }
//
//    @AfterEach
//    void teardown(){
//        List<Espiritu> espiritus = espirituService.recuperarTodos();
//        for(Espiritu espiritu : espiritus){
//            espirituService.eliminar(espiritu.getId());
//        }
//    }

}
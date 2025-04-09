package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.exception.NoSePuedenConectarException;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateUbicacionDao;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class EspirituServiceTest {

    private EspirituService espirituService = new EspirituServiceImpl(new HibernateEspirituDAO(), new HibernateMediumDAO(),new HibernateUbicacionDao());
    private MediumServiceImpl mediumService = new MediumServiceImpl(new HibernateMediumDAO(), new HibernateEspirituDAO(), new HibernateUbicacionDao());
    private UbicacionServiceImpl ubicacionService = new UbicacionServiceImpl( new HibernateUbicacionDao(),new HibernateMediumDAO(), new HibernateEspirituDAO());
    private Espiritu Casper;
    private Espiritu Jinn;
    private Espiritu Oni;
    private Espiritu Anabelle;
    private Espiritu Volac;
    private Medium medium;
    private Ubicacion Bernal;
    private Ubicacion Quilmes;

    @BeforeEach
    void setUp(){
        Bernal = new Ubicacion("Bernal");
        Quilmes = new Ubicacion("Quilmes");
        ubicacionService.crear(Bernal);
        ubicacionService.crear(Quilmes);
        Casper = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Casper");
        Oni = new Espiritu(TipoEspiritu.DEMONIACO, 95, "Otakemaru");
        Jinn = new Espiritu(TipoEspiritu.DEMONIACO, 100, "Marids");
        Anabelle = new Espiritu(TipoEspiritu.DEMONIACO, 48, "Anabelle");
        Volac = new Espiritu(TipoEspiritu.DEMONIACO, 55, "Volac");
        medium = new Medium("lala", 100, 50,Bernal);
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
        espirituService.actualizar(Casper);
        Espiritu actualizado = espirituService.recuperar(Casper.getId());
        assertEquals(sinActualizar.getId(), Casper.getId());
        assertEquals(sinActualizar.getNombre(), "Casper");
        assertEquals(actualizado.getNombre(), "Lala");
    }

    @Test
    void actualizarEspirituNoRegistrado(){
        Casper.setNombre("Lala");
        assertThrows(IdNoValidoException.class, () -> {
            espirituService.actualizar(Casper);
        });
    }

    @Test
    void actualizarEspirituEliminado(){
        espirituService.crear(Casper);
        Casper.setNombre("Lala");
        espirituService.eliminar(Casper);
        assertThrows(OptimisticLockException.class, () -> {
            espirituService.actualizar(Casper);
        });
    }


    @Test
    void recuperarEspiritu(){
        espirituService.crear(Casper);
        Espiritu espirituRecuperado = espirituService.recuperar(Casper.getId());
        assertEquals(espirituRecuperado.getNombre(), "Casper");
        assertEquals(espirituRecuperado.getTipo(), TipoEspiritu.ANGELICAL);
        assertEquals(espirituRecuperado.getNivelDeConexion(), 0);
    }

    @Test
    void RecuperarEspirituNoRegistrado() {
        assertThrows(IdNoValidoException.class, () -> {
            espirituService.recuperar(Casper.getId());
        });
    }

    @Test
    void RecuperarEspirituConIdNoPersistido(){
        assertThrows(IdNoValidoException.class, () -> {
            espirituService.recuperar(10L);
        });
    }

    @Test
    void RecuperarEspirituEliminado() {
        espirituService.crear(Casper);
        espirituService.eliminar(Casper);
        assertThrows(IdNoValidoException.class, () -> {
            espirituService.recuperar(Casper.getId());
        });
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
    void EliminarEspiritu(){
        espirituService.crear(Casper);
        Long espirituId = Casper.getId();
        assertNotNull(espirituService.recuperar(espirituId));
        espirituService.eliminar(Casper);
        assertThrows(IdNoValidoException.class, () -> {
            espirituService.recuperar(espirituId);
        });
    }

    @Test
    void EliminarEspirituDosVeces() {
        espirituService.crear(Casper);
        espirituService.eliminar(Casper);
        assertThrows(OptimisticLockException.class, () -> {
            espirituService.eliminar(Casper);
        });
    }

    @Test
    void obtenerLos3EspiritusDemoniacosConMayorNivel(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.DESCENDENTE, 1, 3);
        assertEquals(demonios.size(), 3);
        assertEquals(demonios.get(0).getNombre(), "Marids");
        assertEquals(demonios.get(1).getNombre(), "Otakemaru");
        assertEquals(demonios.get(2).getNombre(), "Volac");
    }

    @Test
    void obtenerLos3EspiritusDemoniacosConMenorNivel(){
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        espirituService.crear(Casper);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 1, 3);
        assertEquals(demonios.size(), 3);
        assertEquals(demonios.get(0).getNombre(), "Anabelle");
        assertEquals(demonios.get(1).getNombre(), "Volac");
        assertEquals(demonios.get(2).getNombre(), "Otakemaru");
    }

    @Test
    void obtener2EspiritusDemoniacosDeLaSegundPaginaOrdenAscendente(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 2, 2);
        assertEquals(demonios.size(), 2);
        assertEquals(demonios.get(0).getNombre(), "Otakemaru");
        assertEquals(demonios.get(1).getNombre(), "Marids");
    }

    @Test
    void obtenerEspiritusDemoniacosDePaginaVacia(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 4, 2);
        assertEquals(demonios.size(), 0);
    }

    @Test
    void obtenerEspiritusDemoniacosConMenosDeLaCantidadSolicitada(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 1, 10);
        assertEquals(demonios.size(), 4);
    }

    @Test
    void conectarConMediumExitoso(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Bernal);
        mediumService.guardar(medium);
        espirituService.crear(Casper);
        assertEquals(medium.getEspiritus().size(),0);
        Medium mediumConectado = espirituService.conectar(Casper.getId(), medium.getId());
        Espiritu espirituConectado = espirituService.recuperar(Casper.getId());
        assertEquals(mediumConectado.getId(), medium.getId());
        assertEquals(mediumConectado.getEspiritus().size(),1);
        assertFalse(espirituConectado.estaLibre());
        assertEquals(10, espirituConectado.getNivelDeConexion());
    }

    @Test
    void conexionFallidaPorUbicacion(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Quilmes);
        mediumService.guardar(medium);
        espirituService.crear(Casper);
        assertEquals(0, medium.getEspiritus().size());
        assertThrows(NoSePuedenConectarException.class, () -> {
            espirituService.conectar(Casper.getId(), medium.getId());
        });
    }

    @Test
    void conexionFallidaPorLibertadDeEspiritu(){
        Medium medium2 = new Medium("lala",100,100, Bernal);
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Bernal);
        mediumService.guardar(medium);
        espirituService.crear(Casper);
        mediumService.guardar(medium2);
        espirituService.conectar(Casper.getId(), medium2.getId());
        assertEquals(0, medium.getEspiritus().size());
        assertThrows(NoSePuedenConectarException.class, () -> {
            espirituService.conectar(Casper.getId(), medium.getId());
        });
    }





    @AfterEach
    void cleanUp() {
        espirituService.eliminarTodo();
        mediumService.eliminarTodo();
        ubicacionService.eliminarTodo();
    }

}
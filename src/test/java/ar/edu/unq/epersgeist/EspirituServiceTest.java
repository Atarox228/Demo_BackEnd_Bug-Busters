package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateUbicacionDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import ar.edu.unq.epersgeist.servicios.impl.*;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.modelo.exception.NoSePuedenConectarException;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class EspirituServiceTest {

    private EspirituService espirituService;
    private MediumServiceImpl mediumService;
    private UbicacionServiceImpl ubicacionService;
    private Espiritu Casper;
    private Espiritu Jinn;
    private Espiritu Oni;
    private Espiritu Anabelle;
    private Espiritu Volac;
    private Medium medium;
    private Medium  medium2;
    private Ubicacion Bernal;
    private Ubicacion Quilmes;

    @BeforeEach
    void setUp(){
        espirituService = new EspirituServiceImpl(new HibernateEspirituDAO(), new HibernateMediumDAO(),new HibernateUbicacionDAO());
        mediumService = new MediumServiceImpl(new HibernateMediumDAO(), new HibernateEspirituDAO(), new HibernateUbicacionDAO());
        ubicacionService = new UbicacionServiceImpl( new HibernateUbicacionDAO(),new HibernateMediumDAO(), new HibernateEspirituDAO());
        Bernal = new Ubicacion("Bernal");
        Quilmes = new Ubicacion("Quilmes");
        ubicacionService.crear(Bernal);
        ubicacionService.crear(Quilmes);
        Casper = new Angel( 0, "Casper");
        Oni = new Demonio(95, "Otakemaru");
        Jinn = new Demonio(100, "Marids");
        Anabelle = new Demonio(48, "Anabelle");
        Volac = new Demonio(55, "Volac");
        medium = new Medium("Lala", 100, 50);
        medium2 = new Medium("Lalo",100,100);
    }


    @Test
    void guardarEspiritu(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        assertNotNull(Casper.getId());
        assertNotNull(Oni.getId());
        assertNotNull(Jinn.getId());
    }


    @Test
    void actualizarEspiritu(){
        espirituService.crear(Casper);
        Espiritu sinActualizar = espirituService.recuperar(Casper.getId());
        Casper.setNombre("Lala");
        espirituService.actualizar(Casper);
        Espiritu actualizado = espirituService.recuperar(Casper.getId());
        assertEquals(sinActualizar.getId(), Casper.getId());
        assertEquals("Casper", sinActualizar.getNombre());
        assertEquals("Lala", actualizado.getNombre());
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
        assertEquals("Casper", espirituRecuperado.getNombre());
        assertEquals(TipoEspiritu.ANGELICAL, espirituRecuperado.getTipo());
        assertEquals(0, espirituRecuperado.getNivelConexion());
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
        assertEquals(3, espiritus.size());
        assertEquals("Casper", espiritus.get(0).getNombre());
        assertEquals("Marids", espiritus.get(1).getNombre());
        assertEquals("Otakemaru", espiritus.get(2).getNombre());
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
    void obtenerEspiritusDemoniacos() {
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.DESCENDENTE, 1, 5);
        assertEquals(demonios.size(), 4);
    }

    @Test
    void obtenerLos3EspiritusDemoniacosConMayorNivel(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.DESCENDENTE, 1, 3);
        assertEquals(3, demonios.size());
        assertEquals("Marids", demonios.get(0).getNombre());
        assertEquals("Otakemaru", demonios.get(1).getNombre());
        assertEquals("Volac", demonios.get(2).getNombre());
    }

    @Test
    void obtenerLos3EspiritusDemoniacosConMenorNivel(){
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        espirituService.crear(Casper);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 1, 3);
        assertEquals(3, demonios.size());
        assertEquals("Anabelle", demonios.get(0).getNombre());
        assertEquals("Volac", demonios.get(1).getNombre());
        assertEquals("Otakemaru", demonios.get(2).getNombre());
    }

    @Test
    void obtener2EspiritusDemoniacosDeLaSegundPaginaOrdenAscendente(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 2, 2);
        assertEquals(2, demonios.size());
        assertEquals("Otakemaru", demonios.get(0).getNombre());
        assertEquals("Marids", demonios.get(1).getNombre());
    }

    @Test
    void obtenerEspiritusDemoniacosDePaginaVacia(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 4, 2);
        assertEquals(0, demonios.size());
    }

    @Test
    void obtenerEspiritusDemoniacosConMenosDeLaCantidadSolicitada(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 1, 10);
        assertEquals(4, demonios.size());
    }

    @Test
    void conectarConMediumExitoso(){
        mediumService.crear(medium);
        espirituService.crear(Casper);

        mediumService.ubicarseEn(medium.getId(),Bernal.getId());
        espirituService.ubicarseEn(Casper.getId(),Bernal.getId());

        assertEquals(0, medium.getEspiritus().size());
        Medium mediumConectado = espirituService.conectar(Casper.getId(), medium.getId());
        Espiritu espirituConectado = espirituService.recuperar(Casper.getId());
        assertEquals(mediumConectado.getId(), medium.getId());
        assertEquals(mediumConectado.getEspiritus().size(),1);
        assertFalse(espirituConectado.estaLibre());
        assertEquals(10, espirituConectado.getNivelConexion());
    }

    @Test
    void conexionFallidaPorUbicacion(){
        mediumService.crear(medium);
        espirituService.crear(Casper);

        mediumService.ubicarseEn(medium.getId(),Quilmes.getId());
        espirituService.ubicarseEn(Casper.getId(),Bernal.getId());

        assertEquals(0, medium.getEspiritus().size());
        assertThrows(NoSePuedenConectarException.class, () -> {
            espirituService.conectar(Casper.getId(), medium.getId());
        });
    }

    @Test
    void conexionFallidaPorLibertadDeEspiritu(){

        mediumService.crear(medium);
        espirituService.crear(Casper);
        mediumService.crear(medium2);
        mediumService.ubicarseEn(medium.getId(),Bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),Bernal.getId());
        espirituService.ubicarseEn(Casper.getId(),Bernal.getId());

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
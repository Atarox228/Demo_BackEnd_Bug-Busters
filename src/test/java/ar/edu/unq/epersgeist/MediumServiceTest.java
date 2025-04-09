package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.TipoEspiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.exception.EspirituNoLibreException;
import ar.edu.unq.epersgeist.persistencia.dao.exception.NoSePuedenConectarException;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateUbicacionDao;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class MediumServiceTest {

    private MediumService mediumService;
    private Medium medium;
    private Medium medium2;
    private EspirituService espirituService = new EspirituServiceImpl(new HibernateEspirituDAO(), new HibernateMediumDAO());
    private Espiritu espiritu;
    private Ubicacion bernal;
    private UbicacionService ubicacionService = new UbicacionServiceImpl(new HibernateUbicacionDao());
    private Medium medium3;
    private Medium mediumSinMana;
    private Ubicacion quilmes;
    private Espiritu espiritu2;
    private Espiritu espiritu3;

    @BeforeEach
    void setUp() {
        this.mediumService = new MediumServiceImpl(new HibernateMediumDAO(), new HibernateEspirituDAO(), new HibernateUbicacionDao());
        bernal = new Ubicacion("Bernal");
        quilmes = new Ubicacion(("Quilmes"));
        ubicacionService.crear(bernal);
        ubicacionService.crear(quilmes);
        this.medium = new Medium("Lizzie",150,100);
        this.medium2 = new Medium("Lorraine", 200, 50);
        espiritu = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Casper", bernal);
        espiritu2 = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Ghosty", quilmes);
        espiritu3 = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Bernalero", bernal);
        medium3 = new Medium("Lala", 100, 50, bernal);
        mediumSinMana = new Medium("Nomana", 100, 0, bernal);
    }

    @Test
    void testGuardarMedium() {
        mediumService.guardar(medium);
        assertNotNull(medium.getId());
    }

    @Test
    void testRecuperarMedium() {
        mediumService.guardar(medium);
        Medium mediumRecuperado = mediumService.recuperar(medium.getId());
        assertNotNull(mediumRecuperado);
        assertEquals(medium.getId(), mediumRecuperado.getId());
    }

    @Test
    void testRecuperarTodos() {
        mediumService.guardar(medium);
        mediumService.guardar(medium2);
        Collection<Medium> mediums = mediumService.recuperarTodos();
        assertEquals(2, mediums.size());
        List<String> nombres = mediums.stream().map(Medium::getNombre).toList();
        assertEquals(List.of("Lizzie", "Lorraine"), nombres);
    }

    @Test
    void testEliminarMedium() {
        mediumService.guardar(medium);
        Long mediumId = medium.getId();
        assertNotNull(mediumService.recuperar(mediumId));
        mediumService.eliminar(medium);
        assertNull(mediumService.recuperar(mediumId));
    }

    @Test
    void testEliminarTodosLosMediums() {
        mediumService.guardar(medium);
        mediumService.guardar(medium2);
        Long mediumId = medium.getId();
        Long mediumId2 = medium2.getId();
        assertNotNull(mediumService.recuperar(mediumId));
        assertNotNull(mediumService.recuperar(mediumId2));
        mediumService.eliminarTodo();
        assertNull(mediumService.recuperar(mediumId));
        assertNull(mediumService.recuperar(mediumId2));
    }

    @Test
    void testActualizarMedium(){
        mediumService.guardar(medium);
        Medium sinActualizar = mediumService.recuperar(medium.getId());
        medium.setNombre("Juan");
        System.out.println("El nombre ahora es: " + medium.getNombre());
        mediumService.actualizar(medium);
        Medium actualizado = mediumService.recuperar(medium.getId());
        assertEquals(sinActualizar.getId(), medium.getId());
        assertEquals(sinActualizar.getNombre(), "Lizzie");
        assertEquals(actualizado.getNombre(), "Juan");
    }

    @Test
    void testDescansarMedium(){
        mediumService.guardar(medium);
        Medium sinDescansar = mediumService.recuperar(medium.getId());
        mediumService.descansar(medium.getId());
        Medium descansado = mediumService.recuperar(medium.getId());
        assertEquals(sinDescansar.getId(), descansado.getId());
        assertNotEquals(sinDescansar.getMana(), descansado.getMana());
    }


    @Test
    void testInvocarEspirituLibreConManaSuficiente() {
        espirituService.crear(espiritu2);
        Espiritu espirituAntes = espirituService.recuperar(espiritu2.getId());
        mediumService.guardar(medium3);
        Espiritu espirituInvocado = mediumService.invocar(medium3.getId(), espiritu2.getId());
        assertNotEquals(espirituInvocado.getMedium(), espirituAntes.getMedium());
        assertNotEquals(espirituInvocado.getUbicacion(), espirituAntes.getUbicacion());
    }

    @Test
    void testInvocarEspirituLibreConManaSuficienteMismaUbicacion() {
        espirituService.crear(espiritu);
        Espiritu espirituAntes = espirituService.recuperar(espiritu.getId());
        mediumService.guardar(medium3);
        Espiritu espirituInvocado = mediumService.invocar(medium3.getId(), espiritu.getId());
        assertNotEquals(espirituInvocado.getMedium(), espirituAntes.getMedium());
        assertEquals(espirituInvocado.getUbicacion(), espirituAntes.getUbicacion());
    }


    @Test
    void testInvocarEspirituSinMana() {
        espirituService.crear(espiritu);
        Espiritu espirituAntes = espirituService.recuperar(espiritu.getId());
        mediumService.guardar(mediumSinMana);
        Espiritu espirituNoInvocado = mediumService.invocar(mediumSinMana.getId(), espiritu.getId());
        assertEquals(espirituNoInvocado.getMedium(), espirituAntes.getMedium());

    }

    @Test
    void espiritusDeMedium(){
        espirituService.crear(espiritu);
        mediumService.guardar(medium3);
        // Utilizo conectar pero solo para agregar espiritus al medium.
        espirituService.conectar(espiritu.getId(), medium3.getId());
        assertEquals((mediumService.espiritus(medium3.getId())).size(), 1);
    }

    @Test
    void espiritusDeMediumSinEspiritus(){
        mediumService.guardar(medium3);
        assertEquals((mediumService.espiritus(medium3.getId())).size(), 0);
    }

    @Test
    void espiritusDeMediumConVariosEspiritus(){
        espirituService.crear(espiritu);
        espirituService.crear(espiritu3);
        mediumService.guardar(medium3);
        espirituService.conectar(espiritu.getId(), medium3.getId());
        espirituService.conectar(espiritu3.getId(), medium3.getId());
        assertEquals((mediumService.espiritus(medium3.getId())).size(), 2);
    }

    @AfterEach
    void tearDown() {
        espirituService.eliminarTodo();
        mediumService.eliminarTodo();
        ubicacionService.eliminarTodo();
    }
}


    package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateUbicacionDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.impl.*;
import jakarta.persistence.OptimisticLockException;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.persistencia.dao.exception.EspirituNoLibreException;
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
    private UbicacionService ubicacionService;
    private EspirituService espirituService;
    private Medium medium;
    private Medium medium2;
    private Medium mediumSinMana;
    private Espiritu espiritu;
    private Espiritu espiritu2;
    private Ubicacion bernal;
    private Ubicacion quilmes;


    @BeforeEach
    void setUp() {
        espirituService = new EspirituServiceImpl(new HibernateEspirituDAO(), new HibernateMediumDAO(),new HibernateUbicacionDAO());
        ubicacionService  = new UbicacionServiceImpl(new HibernateUbicacionDAO(),new HibernateMediumDAO(), new HibernateEspirituDAO());
        mediumService = new MediumServiceImpl(new HibernateMediumDAO(), new HibernateEspirituDAO(), new HibernateUbicacionDAO());
        bernal = new Ubicacion("Bernal");
        quilmes = new Ubicacion(("Quilmes"));
        ubicacionService.crear(bernal);
        ubicacionService.crear(quilmes);
        medium = new Medium("Lizzie",150,100);
        medium2 = new Medium("Lala", 100, 50);
        espiritu = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Casper");
        espiritu2 = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Ghosty");
        mediumSinMana = new Medium("Nomana", 100, 0);
    }

    @Test
    void guardarMedium() {
        mediumService.crear(medium);
        assertNotNull(medium.getId());
    }

    @Test
    void recuperarMedium() {
        mediumService.crear(medium);
        Medium mediumRecuperado = mediumService.recuperar(medium.getId());
        assertNotNull(mediumRecuperado);
        assertEquals(medium.getId(), mediumRecuperado.getId());
    }

    @Test
    void RecuperarMediumConIdInvalido(){
        Medium medium = mediumService.recuperar(12548L);
        assertNull(medium);
    }

    @Test
    void recuperarMediumConIdNulo() {
        assertThrows(IdNoValidoException.class,()->{
            mediumService.recuperar(null);
        });
    }

    @Test
    void recuperarTodos() {
        mediumService.crear(medium);
        mediumService.crear(medium2);
        Collection<Medium> mediums = mediumService.recuperarTodos();
        assertEquals(2, mediums.size());
        List<String> nombres = mediums.stream().map(Medium::getNombre).toList();
        assertEquals(List.of( "Lala","Lizzie"), nombres);
    }

    @Test
    void recuperarTodosSinMediums(){
        Collection<Medium> mediums = mediumService.recuperarTodos();
        assertEquals(0, mediums.size());
    }

    @Test
    void eliminarMedium() {
        mediumService.crear(medium);
        Long mediumId = medium.getId();
        assertNotNull(mediumService.recuperar(mediumId));
        mediumService.eliminar(medium);
        assertNull(mediumService.recuperar(mediumId));
    }

    @Test
    void eliminarMediumYaEliminado(){
        mediumService.crear(medium);
        mediumService.eliminar(medium);
        assertThrows(OptimisticLockException.class, () -> {
            mediumService.eliminar(medium);
        });
    }

    @Test
    void eliminarTodosLosMediums() {
        mediumService.crear(medium);
        mediumService.crear(medium2);
        Long mediumId = medium.getId();
        Long mediumId2 = medium2.getId();
        assertNotNull(mediumService.recuperar(mediumId));
        assertNotNull(mediumService.recuperar(mediumId2));
        mediumService.eliminarTodo();
        assertNull(mediumService.recuperar(mediumId));
        assertNull(mediumService.recuperar(mediumId2));
    }

    @Test
    void actualizarMedium(){
        mediumService.crear(medium);
        Medium sinActualizar = mediumService.recuperar(medium.getId());
        medium.setNombre("Juan");
        System.out.println("El nombre ahora es: " + medium.getNombre());
        mediumService.actualizar(medium);
        Medium actualizado = mediumService.recuperar(medium.getId());
        assertEquals(sinActualizar.getId(), medium.getId());
        assertEquals("Lizzie", sinActualizar.getNombre());
        assertEquals("Juan", actualizado.getNombre());
    }

    @Test
    void actualizarMediumEliminado(){
        mediumService.crear(medium);
        mediumService.eliminar(medium);
        assertThrows(OptimisticLockException.class, () -> {
            mediumService.actualizar(medium);
        });
    }

    @Test
    void descansarMedium(){
        mediumService.crear(medium);
        Medium sinDescansar = mediumService.recuperar(medium.getId());
        mediumService.descansar(medium.getId());
        Medium descansado = mediumService.recuperar(medium.getId());
        assertEquals(sinDescansar.getId(), descansado.getId());
        assertNotEquals(sinDescansar.getMana(), descansado.getMana());
    }

    @Test
    void descansarMediumConIdNulo(){
        assertThrows(IdNoValidoException.class,()->{
            mediumService.descansar(null);
        });
    }

    @Test
    void descansarMediumConIdInexistente(){
        assertThrows(IdNoValidoException.class,()->{
            mediumService.descansar(1258L);
        });
    }

    @Test
    void invocarEspirituLibreConManaSuficiente() {
        espirituService.crear(espiritu2);
        espirituService.ubicarseEn(espiritu2.getId(),quilmes.getId());
        Espiritu espirituAntes = espirituService.recuperar(espiritu2.getId());
        mediumService.crear(medium2);
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());
        Espiritu espirituInvocado = mediumService.invocar(medium2.getId(), espiritu2.getId());
        assertNotEquals(espirituInvocado.getMedium(), espirituAntes.getMedium());
        assertNotEquals(espirituInvocado.getUbicacion(), espirituAntes.getUbicacion());
    }

    // Testear que pasa si invoco en la misma ubicacion

    @Test
    void invocarEspirituNoLibre() {
        espirituService.crear(espiritu);
        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        mediumService.crear(medium2);
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());
        mediumService.crear(medium);
        mediumService.invocar(medium2.getId(), espiritu.getId());
        assertThrows(EspirituNoLibreException.class, () -> mediumService.invocar(medium.getId(), espiritu.getId()));
    }

    @Test
    void invocarEspirituSinMana() {
        espirituService.crear(espiritu);
        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        Espiritu espirituAntes = espirituService.recuperar(espiritu.getId());
        mediumService.crear(mediumSinMana);
        mediumService.ubicarseEn(mediumSinMana.getId(),bernal.getId());
        Espiritu espirituNoInvocado = mediumService.invocar(mediumSinMana.getId(), espiritu.getId());
        assertEquals(espirituNoInvocado.getMedium(), espirituAntes.getMedium());
        assertEquals(espirituNoInvocado.getUbicacion(), espirituAntes.getUbicacion());

    }

    @Test
    void espiritusDeMedium(){
        espirituService.crear(espiritu);
        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        mediumService.crear(medium2);
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());
        // Utilizo conectar pero solo para agregar espiritus al medium.
        espirituService.conectar(espiritu.getId(), medium2.getId());
        assertEquals(1, (mediumService.espiritus(medium2.getId())).size());
    }

    @Test
    void espiritusDeMediumSinEspiritus(){
        mediumService.crear(medium2);
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());
        assertEquals(0, (mediumService.espiritus(medium2.getId())).size());
    }

    @Test
    void espiritusDeMediumConVariosEspiritus(){
        espirituService.crear(espiritu);
        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        espirituService.crear(espiritu2);
        espirituService.ubicarseEn(espiritu2.getId(),bernal.getId());
        mediumService.crear(medium2);
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());
        espirituService.conectar(espiritu.getId(), medium2.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());
        assertEquals(2, (mediumService.espiritus(medium2.getId())).size());
    }

    @AfterEach
    void cleanUp() {
        espirituService.eliminarTodo();
        mediumService.eliminarTodo();
        ubicacionService.eliminarTodo();
    }
}


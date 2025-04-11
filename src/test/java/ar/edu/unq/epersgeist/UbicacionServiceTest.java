package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateUbicacionDAO;
import ar.edu.unq.epersgeist.servicios.impl.*;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class UbicacionServiceTest {
    private UbicacionService ubicacionService;
    private MediumService mediumService;
    private EspirituService espirituService;
    private Ubicacion fellwood;
    private Ubicacion ashenvale;
    private Espiritu espiritu1;
    private Espiritu espiritu2;
    private Medium medium1;
    private Medium medium2;


    @BeforeEach
    void prepare() {
        ubicacionService = new UbicacionServiceImpl(new HibernateUbicacionDAO(),new HibernateMediumDAO(), new HibernateEspirituDAO());
        espirituService = new EspirituServiceImpl(new HibernateEspirituDAO(), new HibernateMediumDAO(),new HibernateUbicacionDAO());
        mediumService = new MediumServiceImpl(new HibernateMediumDAO(), new HibernateEspirituDAO(), new HibernateUbicacionDAO());

        fellwood = new Ubicacion("Fellwood");
        ubicacionService.crear(fellwood);
        ashenvale = new Ubicacion("Ashenvale");
        ubicacionService.crear(ashenvale);

        espiritu1 = new Demonio(95, "Casper");
        espirituService.crear(espiritu1);
        espiritu2 = new Angel(100, "Marids");
        espirituService.crear(espiritu2);

        medium1 = new Medium("lala", 100, 50);
        mediumService.crear(medium1);
        medium2 = new Medium("lolo", 100, 60);
        mediumService.crear(medium2);
    }

    @Test
    void crearUbicacion(){
        assertNotNull(ashenvale.getId());
    }

    @Test
    void crearMismaUbicacionDosVeces(){
        assertThrows(RuntimeException.class, () -> {
            ubicacionService.crear(fellwood);
        });
    }

    @Test
    void recuperarUbicacion(){
        Ubicacion ubicacion2 = ubicacionService.recuperar(fellwood.getId());
        assertEquals(fellwood.getNombre(), ubicacion2.getNombre());
    }

    @Test
    void recuperarUbicacionNoPersistida(){
        Ubicacion ubicacion2 = ubicacionService.recuperar(1L);
        assertNull(ubicacion2);

    }

    @Test
    void recuperarUbicacionNula(){
        assertThrows(IdNoValidoException.class,()->{
            ubicacionService.recuperar(null);
        });

    }

    @Test
    void eliminarUbicacion(){
        Long idEliminado = fellwood.getId();
        ubicacionService.eliminar(fellwood);
        assertNull(ubicacionService.recuperar(idEliminado));

    }

    @Test
    void eliminarMismaUbicacionDosVeces(){
        ubicacionService.eliminar(fellwood);
        assertThrows(OptimisticLockException.class, () -> {
            ubicacionService.eliminar(fellwood);
        });

    }

    @Test
    void eliminarUbicacionConEspiritus(){
        espirituService.ubicarseEn(espiritu1.getId(),fellwood.getId());
        Ubicacion ubicacion = ubicacionService.recuperar(fellwood.getId());
        assertThrows(ConstraintViolationException.class, () -> {
          ubicacionService.eliminar(ubicacion);
        });

    }

    @Test
    void recuperarTodasLasUbicaciones(){
        Ubicacion ardenweald = new Ubicacion("Ardenweald");
        ubicacionService.crear(ardenweald);
        Integer cantidadList = ubicacionService.recuperarTodos().size();
        assertEquals(3, cantidadList);
    }

    @Test
    void recuperarTodosSinUbicaciones(){
        ubicacionService.eliminar(ashenvale);
        ubicacionService.eliminar(fellwood);
        Integer cantidadList = ubicacionService.recuperarTodos().size();
        assertEquals(0, cantidadList);
    }


    @Test
    void actualizarUbicacion(){
        String nombrePre = fellwood.getNombre();
        fellwood.setNombre("Bosque Vil");
        ubicacionService.actualizar(fellwood);
        Ubicacion ubiCambiada = ubicacionService.recuperar(fellwood.getId());
        assertNotEquals(nombrePre , ubiCambiada.getNombre());
    }

    @Test
    void actualizarUbicacionNoRegistrada(){
        Ubicacion ardenweald = new Ubicacion("Ardenweald");
        assertThrows(IdNoValidoException.class, () -> {
            ubicacionService.actualizar(ardenweald);
        });
    }

    @Test
    void actualizarUbicacionNula(){
        assertThrows(NullPointerException.class, () -> {
            ubicacionService.actualizar(null);
        });
    }

    @Test
    void actualizarUbicacionEliminada(){
        ubicacionService.eliminar(fellwood);
        assertThrows(OptimisticLockException.class, () -> {
            ubicacionService.actualizar(fellwood);
        });
    }

    @Test
    void actualizarVariasUbicaciones(){
        String nombrePre1 = fellwood.getNombre();
        String nombrePre2 = ashenvale.getNombre();
        fellwood.setNombre("Bosque Vil");
        ashenvale.setNombre("Ardenweald");
        ubicacionService.actualizar(fellwood);
        ubicacionService.actualizar(ashenvale);
        Ubicacion ubiCambiada1 = ubicacionService.recuperar(fellwood.getId());
        Ubicacion ubiCambiada2 = ubicacionService.recuperar(ashenvale.getId());

        assertNotEquals(nombrePre1 , ubiCambiada1.getNombre());
        assertNotEquals(nombrePre2 , ubiCambiada2.getNombre());
    }

    @Test
    void actualizarUbicacionConValoresInvalidos(){
        fellwood.setNombre("");
        assertThrows(ConstraintViolationException.class, () -> {
            ubicacionService.actualizar(fellwood);
        });
    }

    @Test
    void eliminarTodasLasUbicaciones(){
        Long ubi1Id = fellwood.getId();
        Long ubi2Id = ashenvale.getId();

        assertNotNull(ubicacionService.recuperar(ubi1Id));
        assertNotNull(ubicacionService.recuperar(ubi2Id));
        ubicacionService.eliminarTodo();
        assertNull(ubicacionService.recuperar(ubi1Id));
        assertNull(ubicacionService.recuperar(ubi2Id));
    }

    @Test
    void espiritusEnUbicacion(){
        espirituService.ubicarseEn(espiritu1.getId(),fellwood.getId());
        espirituService.ubicarseEn(espiritu2.getId(),fellwood.getId());

        List<Espiritu> espiritusUbi = ubicacionService.espiritusEn(fellwood.getId());

        assertEquals(2, espiritusUbi.size());
        assertEquals(espiritu1.getId(), espiritusUbi.get(0).getId());
        assertEquals(espiritu2.getId(), espiritusUbi.get(1).getId());
    }

    @Test
    void ubicacionSinEspiritusRegistrados(){
        List<Espiritu> espiritusUbi = ubicacionService.espiritusEn(fellwood.getId());
        assertEquals(0, espiritusUbi.size());
    }

    @Test
    void espiritusEnUbicacionNoRegistrada(){
        List<Espiritu> espiritusUbi = ubicacionService.espiritusEn(1L);
        assertEquals(0, espiritusUbi.size());
    }

    @Test
    void espiritusEnUbicacionNula(){
        List<Espiritu> espiritusUbi = ubicacionService.espiritusEn(null);

        assertEquals(0, espiritusUbi.size());
    }

    @Test
    void mediumsSinEspirtusEnUbicacion(){
        mediumService.ubicarseEn(medium1.getId(),ashenvale.getId());
        mediumService.ubicarseEn(medium2.getId(),ashenvale.getId());

        List<Medium> mediumsSinEsps = ubicacionService.mediumsSinEspiritusEn(ashenvale.getId());
        assertEquals(2, mediumsSinEsps.size());
    }

    @Test
    void todosLosMediumsConEspiritusEnUbicacion(){
        espirituService.ubicarseEn(espiritu1.getId(),ashenvale.getId());
        espirituService.ubicarseEn(espiritu2.getId(),ashenvale.getId());
        mediumService.ubicarseEn(medium1.getId(),ashenvale.getId());
        mediumService.ubicarseEn(medium2.getId(),ashenvale.getId());

        espirituService.conectar(espiritu1.getId(),medium1.getId());
        espirituService.conectar(espiritu2.getId(),medium2.getId());

        List<Medium> mediumsSinEsps = ubicacionService.mediumsSinEspiritusEn(ashenvale.getId());
        assertEquals(0, mediumsSinEsps.size());
    }

    @Test
    void algunMediumConEspiritusEnUbicacion(){
        espirituService.ubicarseEn(espiritu1.getId(),ashenvale.getId());
        espirituService.ubicarseEn(espiritu2.getId(),ashenvale.getId());
        mediumService.ubicarseEn(medium1.getId(),ashenvale.getId());
        mediumService.ubicarseEn(medium2.getId(),ashenvale.getId());

        espirituService.conectar(espiritu1.getId(),medium1.getId());
        espirituService.conectar(espiritu2.getId(),medium1.getId());

        List<Medium> mediumsSinEsps = ubicacionService.mediumsSinEspiritusEn(ashenvale.getId());
        assertEquals(1, mediumsSinEsps.size());
    }

    @Test
    void MediumsEnUbicacionNoRegistrada(){
        List<Medium> mediumsSinEsps = ubicacionService.mediumsSinEspiritusEn(1L);
        assertEquals(0, mediumsSinEsps.size());
    }

    @Test
    void MediumsEnUbicacionNula(){
        List<Medium> mediumsSinEsps = ubicacionService.mediumsSinEspiritusEn(null);
        assertEquals(0, mediumsSinEsps.size());
    }

    @Test
    void ubicacionSinMediumsRegistrados(){
        List<Medium> mediumsSinEsps = ubicacionService.mediumsSinEspiritusEn(ashenvale.getId());
        assertEquals(0, mediumsSinEsps.size());
    }

    @AfterEach
    void cleanUp() {
        espirituService.eliminarTodo();
        mediumService.eliminarTodo();
        ubicacionService.eliminarTodo();
    }

}

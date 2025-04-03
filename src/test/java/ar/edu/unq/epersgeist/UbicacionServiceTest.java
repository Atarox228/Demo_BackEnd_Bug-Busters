package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.exception.EntidadYaEliminadaException;
import ar.edu.unq.epersgeist.persistencia.dao.exception.EntidadYaRegistradaException;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateUbicacionDao;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.exception.UbicacionEnlazadaConEntidadesException;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
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
    private UbicacionServiceImpl ubicacionService;
    private MediumServiceImpl mediumService;
    private EspirituServiceImpl espirituService;
    private Ubicacion fellwood;
    private Ubicacion ashenvale;
    private Ubicacion ardenweald;
    private Espiritu espiritu1;
    private Espiritu espiritu2;
    private Medium medium1;
    private Medium medium2;


    @BeforeEach
    void prepare() {
        ubicacionService = new UbicacionServiceImpl( new HibernateUbicacionDao(),new HibernateMediumDAO(), new HibernateEspirituDAO());
        espirituService = new EspirituServiceImpl(new HibernateEspirituDAO(), new HibernateMediumDAO());
        mediumService = new MediumServiceImpl(new HibernateMediumDAO(), new HibernateEspirituDAO(), new HibernateUbicacionDao());


        fellwood = new Ubicacion("Fellwood");
        ashenvale = new Ubicacion("Ashenvale");
        ardenweald = new Ubicacion("Ardenweald");

        espiritu1 = new Espiritu("Demoniaco", 95, "Casper");
        espiritu2 = new Espiritu("Angelical", 100, "Marids");

        medium1 = new Medium("lala", 100, 50);
        medium2 = new Medium("lolo", 100, 60);
    }

    @Test
    void crearUbicacion(){

        ubicacionService.crear(ashenvale);
        assertNotNull(ashenvale.getId());
    }

    @Test
    void crearMismaUbicacionDosVeces(){
        ubicacionService.crear(fellwood);
        assertThrows(RuntimeException.class, () -> {
            ubicacionService.crear(fellwood);
        });
    }

    @Test
    void RecuperarUbicacion(){
        ubicacionService.crear(fellwood);
        Ubicacion ubicacion2 = ubicacionService.recuperar(fellwood.getId());

        assertEquals(fellwood.getNombre(), ubicacion2.getNombre());
    }

    @Test
    void RecuperarUbicacionNoPersistida(){

        Ubicacion ubicacion2 = ubicacionService.recuperar(1L);

        assertNull(ubicacion2);

    }

    @Test
    void RecuperarUbicacionNula(){

        assertThrows(IdNoValidoException.class,()->{
            ubicacionService.recuperar(null);
        });

    }

    @Test
    void EliminarUbicacion(){
        ubicacionService.crear(fellwood);
        Long idEliminado = fellwood.getId();
        ubicacionService.eliminar(fellwood);
        assertNull(ubicacionService.recuperar(idEliminado));

    }

    @Test
    void EliminarMismaUbicacionDosVeces(){
        ubicacionService.crear(fellwood);
        ubicacionService.eliminar(fellwood);
        assertThrows(OptimisticLockException.class, () -> {
            ubicacionService.eliminar(fellwood);
        });

    }

    @Test
    void EliminarUbicacionConEspiritus(){
        ubicacionService.crear(fellwood);
        espirituService.crear(espiritu1);
        ubicacionService.agregarEspiritu(fellwood.getId(),espiritu1.getId());
        Ubicacion ubicacion = ubicacionService.recuperar(fellwood.getId());
        assertThrows(UbicacionEnlazadaConEntidadesException.class, () -> {
            ubicacionService.eliminar(ubicacion);
        });

    }

    @Test
    void RecuperarTodasLasUbicaciones(){
        ubicacionService.crear(fellwood);
        ubicacionService.crear(ashenvale);
        ubicacionService.crear(ardenweald);
        Integer cantidadList = ubicacionService.recuperarTodos().size();
        assertEquals(3, cantidadList);
    }

    @Test
    void RecuperarTodosSinUbicaciones(){

        Integer cantidadList = ubicacionService.recuperarTodos().size();
        assertEquals(0, cantidadList);
    }


    @Test
    void ActualizarUbicacion(){
        ubicacionService.crear(fellwood);
        String nombrePre = fellwood.getNombre();
        fellwood.setNombre("Bosque Vil");
        ubicacionService.actualizar(fellwood);
        Ubicacion ubiCambiada = ubicacionService.recuperar(fellwood.getId());
        assertNotEquals(nombrePre , ubiCambiada.getNombre());
    }

    @Test
    void actualizarUbicacionEliminada(){
        ubicacionService.crear(fellwood);
        ubicacionService.eliminar(fellwood);
        assertThrows(OptimisticLockException.class, () -> {
            ubicacionService.actualizar(fellwood);
        });
    }
    
    @Test
    void ActualizarVariasUbicaciones(){
        ubicacionService.crear(fellwood);
        ubicacionService.crear(ashenvale);
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
    void ActualizarUbicacionConValoresInvalidos(){
        ubicacionService.crear(fellwood);
        fellwood.setNombre("");
        assertThrows(ConstraintViolationException.class, () -> {
            ubicacionService.actualizar(fellwood);
        });
    }

    @Test
    void EliminarTodasLasUbicaciones(){
        ubicacionService.crear(fellwood);
        ubicacionService.crear(ashenvale);
        Long ubi1Id = fellwood.getId();
        Long ubi2Id = ashenvale.getId();
        assertNotNull(ubicacionService.recuperar(ubi1Id));
        assertNotNull(ubicacionService.recuperar(ubi2Id));
        ubicacionService.eliminarTodo();
        assertNull(ubicacionService.recuperar(ubi1Id));
        assertNull(ubicacionService.recuperar(ubi2Id));
    }

    @Test
    void EspiritusEnUbicacion(){
        ubicacionService.crear(fellwood);
        espirituService.crear(espiritu1);
        espirituService.crear(espiritu2);

        ubicacionService.agregarEspiritu(fellwood.getId(),espiritu1.getId());
        ubicacionService.agregarEspiritu(fellwood.getId(),espiritu2.getId());

        List<Espiritu> espiritusUbi = ubicacionService.espiritusEn(fellwood.getId());

        assertEquals(2, espiritusUbi.size());
        assertEquals(espiritu1.getId(), espiritusUbi.get(0).getId());
        assertEquals(espiritu2.getId(), espiritusUbi.get(1).getId());
    }

    @Test
    void UbicacionSinEspiritusRegistrados(){
        ubicacionService.crear(ashenvale);

        List<Espiritu> espiritusUbi = ubicacionService.espiritusEn(fellwood.getId());

        assertEquals(0, espiritusUbi.size());
    }

    @Test
    void EspiritusEnUbicacionNoRegistrada(){
        List<Espiritu> espiritusUbi = ubicacionService.espiritusEn(1L);

        assertEquals(0, espiritusUbi.size());
    }

    @Test
    void EspiritusEnUbicacionNula(){
        List<Espiritu> espiritusUbi = ubicacionService.espiritusEn(null);

        assertEquals(0, espiritusUbi.size());
    }

    @AfterEach
    void cleanUp() {
        espirituService.eliminarTodo();
        mediumService.eliminarTodo();
        ubicacionService.eliminarTodo();
    }

    // Test de modelo
//    @Test
//    void crearUbicacionNula(){
//        Ubicacion ubi = new Ubicacion(null);
//        assertThrows(NullPointerException.class, () -> {
//            ubicacionService.crear(ubi);
//        });
//    }
}

package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.exception.EntidadYaEliminadaException;
import ar.edu.unq.epersgeist.persistencia.dao.exception.EntidadYaRegistradaException;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateUbicacionDao;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
public class UbicacionServiceTest {
    private UbicacionServiceImpl service;
    private Ubicacion fellwood;
    private Ubicacion ashenvale;
    private Ubicacion ardenweald;


    @BeforeEach
    void prepare() {
        this.service = new UbicacionServiceImpl(
                new HibernateUbicacionDao()
        );

        fellwood = new Ubicacion("Fellwood");
        ashenvale = new Ubicacion("Ashenvale");
        ardenweald = new Ubicacion("Ardenweald");
    }

    @Test
    void crearUbicacion(){

        service.crear(ashenvale);
        assertNotNull(ashenvale.getId());
    }

    @Test
    void crearMismaUbicacionDosVeces(){
        service.crear(fellwood);
        assertThrows(RuntimeException.class, () -> {
            service.crear(fellwood);
        });
    }

    @Test
    void RecuperarUbicacion(){
        service.crear(fellwood);
        Ubicacion ubicacion2 = service.recuperar(fellwood.getId());

        assertEquals(fellwood.getNombre(), ubicacion2.getNombre());
    }

    @Test
    void EliminarUbicacion(){
        service.crear(fellwood);
        Long idEliminado = fellwood.getId();
        service.eliminar(fellwood);
        assertNull(service.recuperar(idEliminado));

    }

    @Test
    void EliminarMismaUbicacionDosVeces(){
        service.crear(fellwood);
        service.eliminar(fellwood);
        assertThrows(RuntimeException.class, () -> {
            service.eliminar(fellwood);
        });

    }

    @Test
    void RecuperarTodasLasUbicaciones(){
        service.crear(fellwood);
        service.crear(ashenvale);
        service.crear(ardenweald);
        Integer cantidadList = service.recuperarTodos().size();
        assertEquals(3, cantidadList);
    }


    @Test
    void ActualizarUbicacion(){
        service.crear(fellwood);
        String nombrePre = fellwood.getNombre();
        fellwood.setNombre("Bosque Vil");
        service.actualizar(fellwood);
        Ubicacion ubiCambiada = service.recuperar(fellwood.getId());
        assertNotEquals(nombrePre , ubiCambiada.getNombre());
    }

    @Test
    void actualizarUbicacionEliminada(){
        service.crear(fellwood);
        service.eliminar(fellwood);
//        assertThrows(.class, () -> {
//            service.actualizar(fellwood);
//        });
    }
    
    @Test
    void ActualizarVariasUbicaciones(){
        service.crear(fellwood);
        service.crear(ashenvale);
        String nombrePre1 = fellwood.getNombre();
        String nombrePre2 = ashenvale.getNombre();
        fellwood.setNombre("Bosque Vil");
        ashenvale.setNombre("Ardenweald");
        service.actualizar(fellwood);
        service.actualizar(ashenvale);
        Ubicacion ubiCambiada1 = service.recuperar(fellwood.getId());
        Ubicacion ubiCambiada2 = service.recuperar(ashenvale.getId());
        assertNotEquals(nombrePre1 , ubiCambiada1.getNombre());
        assertNotEquals(nombrePre2 , ubiCambiada2.getNombre());
        
    }

    @AfterEach
    void cleanUp() {
        service.eliminarTodo();
    }
}

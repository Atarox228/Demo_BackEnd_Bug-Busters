package ar.edu.unq.epersgeist.service;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.repositorys.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.service.dataService.DataService;
import ar.edu.unq.epersgeist.servicios.exception.*;
import ar.edu.unq.epersgeist.servicios.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)

@SpringBootTest
public class UbicacionServiceTest {

    @Autowired
    private DataService dataService;
    @Autowired
    private UbicacionService ubicacionService;
    @Autowired
    private MediumService mediumService;
    @Autowired
    private EspirituService espirituService;

    private Ubicacion fellwood;
    private Ubicacion ashenvale;
    private Espiritu espiritu1;
    private Espiritu espiritu2;
    private Medium medium1;
    private Medium medium2;
    private UbicacionRepository repository;

    @BeforeEach
    void prepare() {

        fellwood = new Cementerio("Fellwood", 50);
        ubicacionService.crear(fellwood);
        ashenvale = new Santuario("Ashenvale",100);
        ubicacionService.crear(ashenvale);

        espiritu1 = new Demonio( "Casper");
        espirituService.crear(espiritu1);
        espiritu2 = new Angel("Marids");
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
        assertThrows(UbicacionYaCreadaException.class, () -> {
            ubicacionService.crear(fellwood);
        });
    }

    @Test
    void recuperarUbicacion(){
        Ubicacion ubicacion2 = ubicacionService.recuperar(fellwood.getId()).get();
        assertEquals(fellwood.getNombre(), ubicacion2.getNombre());
    }

    @Test
    void recuperarUbicacionNoPersistida(){
        assertThrows(RecursoNoEncontradoException.class, () -> {
            ubicacionService.recuperar(1L);
        });
    }

    @Test
    void recuperarUbicacionNula(){
        assertThrows(IdNoValidoException.class, () -> {
            ubicacionService.recuperar(null);
        });

    }

    @Test
    void eliminarUbicacion(){
        Long idEliminado = fellwood.getId();
        ubicacionService.eliminar(fellwood);
        assertThrows(EntidadEliminadaException.class, () -> {
            ubicacionService.recuperar(idEliminado);
        });
    }

    @Test
    void eliminarMismaUbicacionDosVeces() {
        ubicacionService.eliminar(fellwood);
        assertThrows(EntidadEliminadaException.class, () -> {
            ubicacionService.eliminar(fellwood);
        });
    }

    @Test
    void eliminarUbicacionConEspiritus(){
        espiritu1.setUbicacion(fellwood);
        espirituService.actualizar(espiritu1);
        Ubicacion ubicacion = ubicacionService.recuperar(fellwood.getId()).get();
        assertThrows(EntidadConEntidadesConectadasException.class, () -> {
          ubicacionService.eliminar(ubicacion);
        });

    }

    @Test
    void recuperarTodasLasUbicaciones(){
        Ubicacion ardenweald = new Cementerio("Ardenweald",100);
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
        Ubicacion ubiCambiada = ubicacionService.recuperar(fellwood.getId()).get();
        assertNotEquals(nombrePre , ubiCambiada.getNombre());
    }

    @Test
    void actualizarUbicacionNoRegistrada(){
        Ubicacion ardenweald = new Cementerio("Ardenweald", 100);
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
        assertThrows(EntidadEliminadaException.class, () -> {
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
        Ubicacion ubiCambiada1 = ubicacionService.recuperar(fellwood.getId()).get();
        Ubicacion ubiCambiada2 = ubicacionService.recuperar(ashenvale.getId()).get();

        assertNotEquals(nombrePre1 , ubiCambiada1.getNombre());
        assertNotEquals(nombrePre2 , ubiCambiada2.getNombre());
    }

    @Test
    void actualizarUbicacionConValoresInvalidos(){
        fellwood.setNombre("");
        assertThrows(DataIntegrityViolationException.class, () -> {
            ubicacionService.actualizar(fellwood);
        });
    }

    @Test
    void eliminarTodasLasUbicaciones() {
        Long ubi1Id = fellwood.getId();
        Long ubi2Id = ashenvale.getId();

        assertNotNull(ubicacionService.recuperar(ubi1Id));
        assertNotNull(ubicacionService.recuperar(ubi2Id));
        ubicacionService.clearAll();
        assertThrows(RecursoNoEncontradoException.class, () -> {
            ubicacionService.recuperar(ubi1Id);
        });
        assertThrows(RecursoNoEncontradoException.class, () -> {
            ubicacionService.recuperar(ubi2Id);
        });
    }

    @Test
    void espiritusEnUbicacion(){
        espiritu1.setUbicacion(fellwood);
        espirituService.actualizar(espiritu1);
        espiritu2.setUbicacion(fellwood);
        espirituService.actualizar(espiritu2);

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
        assertThrows(IdNoValidoException.class, () -> {
            ubicacionService.espiritusEn(null);
        });
    }

    @Test
    void mediumsSinEspirtusEnUbicacion(){
          medium1.setUbicacion(ashenvale);
          mediumService.actualizar(medium1);
          medium2.setUbicacion(ashenvale);
          mediumService.actualizar(medium2);

        List<Medium> mediumsSinEsps = ubicacionService.mediumsSinEspiritusEn(ashenvale.getId());
        assertEquals(2, mediumsSinEsps.size());
    }

    @Test
    void todosLosMediumsConEspiritusEnUbicacion(){
        espiritu1.setUbicacion(ashenvale);
        espiritu2.setUbicacion(ashenvale);
        espirituService.actualizar(espiritu1);
        espirituService.actualizar(espiritu2);
        medium1.setUbicacion(ashenvale);
        mediumService.actualizar(medium1);
        medium2.setUbicacion(ashenvale);
        mediumService.actualizar(medium2);

        espirituService.conectar(espiritu1.getId(),medium1.getId());
        espirituService.conectar(espiritu2.getId(),medium2.getId());

        List<Medium> mediumsSinEsps = ubicacionService.mediumsSinEspiritusEn(ashenvale.getId());
        assertEquals(0, mediumsSinEsps.size());
    }

    @Test
    void algunMediumConEspiritusEnUbicacion(){
        espiritu1.setUbicacion(ashenvale);
        espiritu2.setUbicacion(ashenvale);
        espirituService.actualizar(espiritu1);
        espirituService.actualizar(espiritu2);
        medium1.setUbicacion(ashenvale);
        mediumService.actualizar(medium1);
        medium2.setUbicacion(ashenvale);
        mediumService.actualizar(medium2);

        espirituService.conectar(espiritu1.getId(),medium1.getId());
        espirituService.conectar(espiritu2.getId(),medium1.getId());

        List<Medium> mediumsSinEsps = ubicacionService.mediumsSinEspiritusEn(ashenvale.getId());
        assertEquals(1, mediumsSinEsps.size());
    }

    @Test
    void mediumsEnUbicacionNoRegistrada(){
        List<Medium> mediumsSinEsps = ubicacionService.mediumsSinEspiritusEn(1L);
        assertEquals(0, mediumsSinEsps.size());
    }

    @Test
    void mediumsEnUbicacionNula(){
        assertThrows(IdNoValidoException.class, () -> {
            ubicacionService.mediumsSinEspiritusEn(null);
        });
    }

    @Test
    void ubicacionSinMediumsRegistrados(){
        List<Medium> mediumsSinEsps = ubicacionService.mediumsSinEspiritusEn(ashenvale.getId());
        assertEquals(0, mediumsSinEsps.size());
    }

    //test de auditoria de datos
    @Test
    void creacionTimeStampUpdateAndNoDelete(){
        Ubicacion santuario = new Santuario("santuario",100);
        Ubicacion cementerio = new Cementerio("cementerio", 100);

        ubicacionService.crear(santuario);
        ubicacionService.crear(cementerio);

        Ubicacion santuarioAct = ubicacionService.recuperar(santuario.getId()).get();
        Ubicacion cementerioAct = ubicacionService.recuperar(cementerio.getId()).get();

        assertNotNull(santuarioAct.getCreatedAt());
        assertNotNull(santuarioAct.getUpdatedAt());
        assertFalse(santuarioAct.getDeleted());
        assertNotNull(cementerioAct.getCreatedAt());
        assertNotNull(cementerioAct.getUpdatedAt());
        assertFalse(cementerioAct.getDeleted());

    }

    @Test
    void updateTimeStamp() throws InterruptedException {
        Ubicacion santuario = new Santuario("santuario",100);
        Ubicacion cementerio = new Cementerio("cementerio", 100);

        ubicacionService.crear(santuario);
        ubicacionService.crear(cementerio);

        Ubicacion santuarioAct = ubicacionService.recuperar(santuario.getId()).get();
        Ubicacion cementerioAct = ubicacionService.recuperar(cementerio.getId()).get();


        Thread.sleep(1000);

        santuarioAct.setNombre("santAct");
        ubicacionService.actualizar(santuarioAct);
        santuarioAct = ubicacionService.recuperar(santuarioAct.getId()).get();

        cementerioAct.setNombre("cAct");
        ubicacionService.actualizar(cementerioAct);
        cementerioAct = ubicacionService.recuperar(cementerioAct.getId()).get();

        int comparison = santuarioAct.getUpdatedAt().compareTo(santuarioAct.getCreatedAt());
        int comparison2 = cementerioAct.getUpdatedAt().compareTo(cementerioAct.getCreatedAt());

        assertTrue(comparison > 0);
        assertTrue(comparison2 > 0);

    }

    @Test
    void updateTimeStampDoble() throws InterruptedException {
        Ubicacion santuario = new Santuario("santuario",100);
        Ubicacion cementerio = new Cementerio("cementerio", 100);

        ubicacionService.crear(santuario);
        ubicacionService.crear(cementerio);

        Ubicacion santuarioAct = ubicacionService.recuperar(santuario.getId()).get();
        Ubicacion cementerioAct = ubicacionService.recuperar(cementerio.getId()).get();

        Thread.sleep(1000);

        santuarioAct.setNombre("santAct");
        ubicacionService.actualizar(santuarioAct);
        santuarioAct = ubicacionService.recuperar(santuarioAct.getId()).get();
        Date lastUpdate = santuarioAct.getUpdatedAt();

        cementerioAct.setNombre("cAct");
        ubicacionService.actualizar(cementerioAct);
        cementerioAct = ubicacionService.recuperar(cementerioAct.getId()).get();
        Date lastUpdate2 = cementerioAct.getUpdatedAt();

        Thread.sleep(1000);

        santuarioAct.setNombre("santAct2");
        ubicacionService.actualizar(santuarioAct);
        santuarioAct = ubicacionService.recuperar(santuarioAct.getId()).get();

        cementerioAct.setNombre("cAct2");
        ubicacionService.actualizar(cementerioAct);
        cementerioAct = ubicacionService.recuperar(cementerioAct.getId()).get();

        int comparison = santuarioAct.getUpdatedAt().compareTo(lastUpdate);
        int comparison2 = cementerioAct.getUpdatedAt().compareTo(lastUpdate2);

        assertTrue(comparison > 0);
        assertTrue(comparison2 > 0);

    }

    @Test
    void softDeletion(){
        Ubicacion santuario = new Santuario("santuario",100);
        Ubicacion cementerio = new Cementerio("cementerio", 100);

        ubicacionService.crear(santuario);
        ubicacionService.crear(cementerio);

        Ubicacion santuarioAct = ubicacionService.recuperar(santuario.getId()).get();
        Ubicacion cementerioAct = ubicacionService.recuperar(cementerio.getId()).get();

        ubicacionService.eliminar(santuarioAct);
        ubicacionService.eliminar(cementerioAct);

        Ubicacion santuarioBorrado= ubicacionService.recuperarAunConSoftDelete(santuarioAct.getId()).get();
        Ubicacion cementerioBorrado = ubicacionService.recuperarAunConSoftDelete(cementerioAct.getId()).get();

        assertThrows(EntidadEliminadaException.class, () -> {
            ubicacionService.recuperar(santuarioAct.getId());
        });
        assertThrows(EntidadEliminadaException.class, () -> {
            ubicacionService.recuperar(cementerioAct.getId());
        });
        assertTrue(santuarioBorrado.getDeleted());
        assertTrue(cementerioBorrado.getDeleted());

    }

    @Test
    void noRecuperaTodosConSoftdelete(){
        dataService.eliminarTodo();
        Ubicacion santuario = new Santuario("santuario",100);
        Ubicacion cementerio = new Cementerio("cementerio", 100);
        Ubicacion cementerio2 = new Cementerio("cementerio2", 100);


        ubicacionService.crear(santuario);
        ubicacionService.crear(cementerio);
        ubicacionService.crear(cementerio2);

        Ubicacion santuarioAct = ubicacionService.recuperar(santuario.getId()).get();
        Ubicacion cementerioAct = ubicacionService.recuperar(cementerio.getId()).get();


        ubicacionService.eliminar(santuarioAct);
        ubicacionService.eliminar(cementerioAct);


        santuarioAct = ubicacionService.recuperarAunConSoftDelete(santuario.getId()).get();
        cementerioAct = ubicacionService.recuperarAunConSoftDelete(cementerio.getId()).get();

        Collection<Ubicacion> todos = ubicacionService.recuperarTodos();


        assertThrows(EntidadEliminadaException.class, () -> {
            ubicacionService.recuperar(santuario.getId());
        });
        assertThrows(EntidadEliminadaException.class, () -> {
            ubicacionService.recuperar(cementerio.getId());
        });
        assertTrue(santuarioAct.getDeleted());
        assertTrue(cementerioAct.getDeleted());
        assertEquals(todos.size(),1);
    }

    @Test
    void conectarUnidireccional() {
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());

        UbicacionNeo4J origen = ubicacionService.recuperarNeo4J(fellwood.getId());
        UbicacionNeo4J destino = ubicacionService.recuperarNeo4J(ashenvale.getId());

        Assertions.assertEquals(0, destino.getUbicaciones().size());
        Assertions.assertEquals(1, origen.getUbicaciones().size());
        //Assertions.assertEquals(destino.getId(), origen.getUbicaciones().iterator().next().getId());
    }
/*
    @Test void conectarDosVecesUnidireccional() {
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());

        UbicacionNeo4J origen = ubicacionService.recuperarNeo4J(fellwood.getId());
        UbicacionNeo4J destino1 = ubicacionService.recuperarNeo4J(ashenvale.getId());
        UbicacionNeo4J destino2 = ubicacionService.recuperarNeo4J(ashenvale.getId());

        assertEquals(0, destino1.getUbicaciones().size());
        assertEquals(0, destino2.getUbicaciones().size());
        assertEquals(2, origen.getUbicaciones().size());
        assertEquals(destino1.getId(), origen.getUbicaciones().iterator().next().getId());
        assertEquals(destino2.getId(), origen.getUbicaciones().iterator().next().getId());
    }

    @Test
    void conectarDosVecesMismoDestino() {
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());

        UbicacionNeo4J origen = ubicacionService.recuperarNeo4J(fellwood.getId());
        UbicacionNeo4J destino = ubicacionService.recuperarNeo4J(ashenvale.getId());

        assertEquals(0, destino.getUbicaciones().size());
        assertEquals(1, origen.getUbicaciones().size());
        assertEquals(destino.getId(), origen.getUbicaciones().iterator().next().getId());
    }

    @Test
    void conectarBidireccional() {
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), fellwood.getId());

        UbicacionNeo4J origen = ubicacionService.recuperarNeo4J(fellwood.getId());
        UbicacionNeo4J destino = ubicacionService.recuperarNeo4J(ashenvale.getId());

        assertEquals(1, destino.getUbicaciones().size());
        assertEquals(1, origen.getUbicaciones().size());
        assertEquals(destino.getId(), origen.getUbicaciones().iterator().next().getId());
        assertEquals(origen.getId(), destino.getUbicaciones().iterator().next().getId());
    }

    @Test
    void conectarUbicacionOrigenConIdNulo() {
        assertThrows(IdNoValidoException.class,() -> {
            ubicacionService.conectar(null, fellwood.getId());
        });
    }

    @Test
    void conectarUbicacionDestinoConIdNulo() {
        assertThrows(IdNoValidoException.class,() -> {
            ubicacionService.conectar(ashenvale.getId(), null);
        });
    }

    @Test
    void conectarUbicacionOrigenConIdInexistente() {
        assertThrows(RecursoNoEncontradoException.class,() -> {
            ubicacionService.conectar(1L, fellwood.getId());
        });
    }

    @Test
    void conectarUbicacionDestinoInexistente() {
        assertThrows(RecursoNoEncontradoException.class,() -> {
            ubicacionService.conectar(fellwood.getId(), 1L);
        });
    }

    @Test
    void conectarseASiMisma() {
        ubicacionService.conectar(ashenvale.getId(), ashenvale.getId());
    }
*/

    @AfterEach
    void cleanUp() {
        dataService.eliminarTodo();
    }
}

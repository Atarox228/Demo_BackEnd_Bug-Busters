package ar.edu.unq.epersgeist.service;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.enums.DegreeType;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.service.dataService.DataService;
import ar.edu.unq.epersgeist.servicios.exception.*;
import ar.edu.unq.epersgeist.servicios.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import java.util.*;
import java.util.stream.Collectors;
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
    @Autowired
    private UbicacionRepository ubicacionRepository;
    private Ubicacion fellwood;
    private Ubicacion ashenvale;
    private Ubicacion santaMaria;
    private Santuario catedral;
    private Espiritu espiritu1;
    private Espiritu espiritu2;
    private Medium medium1;
    private Medium medium2;
    private GeoJsonPolygon areaAshenvale;
    private GeoJsonPolygon areaFellwood;
    private GeoJsonPolygon areaSantaMaria;
    private GeoJsonPolygon areaCatedral;

    @BeforeEach
    void prepare() {
        List<Point> area1 = List.of(
                new Point(-58.2730, -34.7210),
                new Point(-58.2700, -34.7230),
                new Point(-58.2680, -34.7200),
                new Point(-58.2730, -34.7210)
        );
        areaAshenvale = new GeoJsonPolygon(area1);

        List<Point> area2 = List.of(
                new Point(-58.2630, -34.7070),
                new Point(-58.2600, -34.7090),
                new Point(-58.2580, -34.7060),
                new Point(-58.2630, -34.7070)
        );
        areaFellwood = new GeoJsonPolygon(area2);

        List<Point> area3 = List.of(
                new Point(-58.3610, -34.6600),
                new Point(-58.3590, -34.6620),
                new Point(-58.3570, -34.6590),
                new Point(-58.3610, -34.6600)
        );
        areaSantaMaria = new GeoJsonPolygon(area3);

        List<Point> area4 = List.of(
                new Point(-58.4000, -34.7000),
                new Point(-58.3980, -34.7020),
                new Point(-58.3960, -34.6990),
                new Point(-58.4000, -34.7000)
        );
        areaCatedral = new GeoJsonPolygon(area4);

        fellwood = new Cementerio("Fellwood", 50);
        ubicacionService.crear(fellwood, areaFellwood);
        ashenvale = new Santuario("Ashenvale",100);
        ubicacionService.crear(ashenvale, areaAshenvale);
        santaMaria = new Santuario("SantaMaria", 80);
        ubicacionService.crear(santaMaria, areaSantaMaria);
        catedral = new Santuario("catedral", 80);
        ubicacionService.crear(catedral, areaCatedral);

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
        assertThrows(UbicacionYaCreadaException.class, () ->{
            ubicacionService.crear(fellwood, areaFellwood);
        });
    }

    @Test
    void crearUbicacionEnAreaCompletaOcupada(){
        Ubicacion solano = new Cementerio("Solano", 50);
        assertThrows(UbicacionAreaSolapadaException.class, () ->{
            ubicacionService.crear(solano, areaAshenvale);
        });
    }

    @Test
    void crearUbicacionEnLimiteOcupado(){
        List<Point> area7 = List.of(
                new Point(-58.2730, -34.7210),  // Punto A, limite de Ashenvale
                new Point(-58.2700, -34.7230),  // Punto B, limite de Ashenvale
                new Point(-58.2750, -34.7180),
                new Point(-58.2730, -34.7210)
        );
        GeoJsonPolygon areaConBordeCoincidente = new GeoJsonPolygon(area7);
        Ubicacion sanFrancisco = new Cementerio("San Francisco", 50);
        assertThrows(UbicacionAreaSolapadaException.class, () ->{
            ubicacionService.crear(sanFrancisco, areaConBordeCoincidente);
        });
    }

    @Test
    void crearUbicacionEnCoordenadaInternaOcupada() {
        List<Point> area8 = List.of(
                new Point(-58.2703, -34.7213),  // Punto interno a Ashenvale
                new Point(-58.2715, -34.7195),
                new Point(-58.2695, -34.7190),
                new Point(-58.2703, -34.7213)
        );
        GeoJsonPolygon areaSolapadaPorInterior = new GeoJsonPolygon(area8);
        Ubicacion santaMarta = new Santuario("Santa Marta", 50);
        assertThrows(UbicacionAreaSolapadaException.class, () ->{
            ubicacionService.crear(santaMarta, areaSolapadaPorInterior);
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
    void recuperarUbicacionPorNombre(){
        UbicacionNeo4J ubicacion2 = ubicacionService.recuperarPorNombre(fellwood.getNombre());
        assertEquals(fellwood.getNombre(), ubicacion2.getNombre());
    }

    @Test
    void recuperarUbicacionNoPersistidaPorNombre(){
        assertThrows(RecursoNoEncontradoException.class, () -> {
            ubicacionService.recuperarPorNombre("Juan Manuel");
        });
    }

    @Test
    void recuperarUbicacionMongoPorCoordenada() {
        GeoJsonPoint coordendada = new GeoJsonPoint(-58.2730, -34.7210);
        AreaMongo ubicacionMongo = ubicacionService.recuperarPorCoordenada(coordendada);
        assertEquals(ashenvale.getId(), ubicacionMongo.getIdUbicacion());
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
        Integer cantidadList = ubicacionService.recuperarTodos().size();
        assertEquals(4, cantidadList);
    }

    @Test
    void recuperarTodosSinUbicaciones(){
        ubicacionService.eliminar(catedral);
        ubicacionService.eliminar(ashenvale);
        ubicacionService.eliminar(fellwood);
        ubicacionService.eliminar(santaMaria);
        Integer cantidadList = ubicacionService.recuperarTodos().size();
        assertEquals(0, cantidadList);
    }

    @Test
    void actualizarUbicacion(){
        String nombrePre = fellwood.getNombre();
        fellwood.setNombre("Bosque Vil");
        ubicacionService.actualizar(fellwood,nombrePre);
        Ubicacion ubiCambiada = ubicacionService.recuperar(fellwood.getId()).get();
        assertNotEquals(nombrePre , ubiCambiada.getNombre());
    }

    @Test
    void actualizarUbicacionNoRegistrada(){
        Ubicacion ardenweald = new Cementerio("Ardenweald", 100);
        assertThrows(IdNoValidoException.class, () -> {
            ubicacionService.actualizar(ardenweald,"Ardenweald");
        });
    }

    @Test
    void actualizarUbicacionNula(){
        assertThrows(NullPointerException.class, () -> {
            ubicacionService.actualizar(null, "");
        });
    }

    @Test
    void actualizarUbicacionEliminada(){
        ubicacionService.eliminar(fellwood);
        assertThrows(EntidadEliminadaException.class, () -> {
            ubicacionService.actualizar(fellwood,"Fellwood");
        });
    }

    @Test
    void actualizarVariasUbicaciones(){
        String nombrePre1 = fellwood.getNombre();
        String nombrePre2 = ashenvale.getNombre();
        fellwood.setNombre("Bosque Vil");
        ashenvale.setNombre("Ardenweald");
        ubicacionService.actualizar(fellwood,nombrePre1);
        ubicacionService.actualizar(ashenvale,nombrePre2);
        Ubicacion ubiCambiada1 = ubicacionService.recuperar(fellwood.getId()).get();
        Ubicacion ubiCambiada2 = ubicacionService.recuperar(ashenvale.getId()).get();

        assertNotEquals(nombrePre1 , ubiCambiada1.getNombre());
        assertNotEquals(nombrePre2 , ubiCambiada2.getNombre());
    }

    @Test
    void actualizarUbicacionConValoresInvalidos(){
        fellwood.setNombre("");
        assertThrows(DataIntegrityViolationException.class, () -> {
            ubicacionService.actualizar(fellwood,"Fellwood");
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
        Ubicacion santuarioAct = ubicacionService.recuperar(ashenvale.getId()).get();
        Ubicacion cementerioAct = ubicacionService.recuperar(fellwood.getId()).get();

        assertNotNull(santuarioAct.getCreatedAt());
        assertNotNull(santuarioAct.getUpdatedAt());
        assertFalse(santuarioAct.getDeleted());
        assertNotNull(cementerioAct.getCreatedAt());
        assertNotNull(cementerioAct.getUpdatedAt());
        assertFalse(cementerioAct.getDeleted());
    }

    @Test
    void updateTimeStamp() throws InterruptedException {
        Ubicacion santuarioAct = ubicacionService.recuperar(ashenvale.getId()).get();
        Ubicacion cementerioAct = ubicacionService.recuperar(fellwood.getId()).get();

        Thread.sleep(1000);

        santuarioAct.setNombre("santAct");
        ubicacionService.actualizar(santuarioAct,ashenvale.getNombre());
        santuarioAct = ubicacionService.recuperar(santuarioAct.getId()).get();

        cementerioAct.setNombre("cAct");
        ubicacionService.actualizar(cementerioAct,fellwood.getNombre());
        cementerioAct = ubicacionService.recuperar(cementerioAct.getId()).get();

        int comparison = santuarioAct.getUpdatedAt().compareTo(santuarioAct.getCreatedAt());
        int comparison2 = cementerioAct.getUpdatedAt().compareTo(cementerioAct.getCreatedAt());

        assertTrue(comparison > 0);
        assertTrue(comparison2 > 0);
    }

    @Test
    void updateTimeStampDoble() throws InterruptedException {
        Ubicacion santuarioAct = ubicacionService.recuperar(ashenvale.getId()).get();
        Ubicacion cementerioAct = ubicacionService.recuperar(fellwood.getId()).get();

        Thread.sleep(1000);

        santuarioAct.setNombre("santAct");
        ubicacionService.actualizar(santuarioAct,ashenvale.getNombre());
        santuarioAct = ubicacionService.recuperar(santuarioAct.getId()).get();
        Date lastUpdate = santuarioAct.getUpdatedAt();

        cementerioAct.setNombre("cAct");
        ubicacionService.actualizar(cementerioAct,fellwood.getNombre());
        cementerioAct = ubicacionService.recuperar(cementerioAct.getId()).get();
        Date lastUpdate2 = cementerioAct.getUpdatedAt();

        Thread.sleep(1000);

        santuarioAct.setNombre("santAct2");
        ubicacionService.actualizar(santuarioAct,"santAct");
        santuarioAct = ubicacionService.recuperar(santuarioAct.getId()).get();

        cementerioAct.setNombre("cAct2");
        ubicacionService.actualizar(cementerioAct,"cAct");
        cementerioAct = ubicacionService.recuperar(cementerioAct.getId()).get();

        int comparison = santuarioAct.getUpdatedAt().compareTo(lastUpdate);
        int comparison2 = cementerioAct.getUpdatedAt().compareTo(lastUpdate2);

        assertTrue(comparison > 0);
        assertTrue(comparison2 > 0);
    }

    @Test
    void softDeletion(){
        Ubicacion santuarioAct = ubicacionService.recuperar(ashenvale.getId()).get();
        Ubicacion cementerioAct = ubicacionService.recuperar(fellwood.getId()).get();

        ubicacionService.eliminar(santuarioAct);
        ubicacionService.eliminar(cementerioAct);

        Ubicacion santuarioBorrado= this.recuperarAunConSoftDelete(santuarioAct.getId()).get();
        Ubicacion cementerioBorrado = this.recuperarAunConSoftDelete(cementerioAct.getId()).get();

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
    void noRecuperaTodosConSoftdelete() {
        dataService.eliminarTodo();
        List<Point> area1 = List.of(
                new Point(-58.2730, -34.7210),
                new Point(-58.2700, -34.7230),
                new Point(-58.2680, -34.7200),
                new Point(-58.2730, -34.7210)
        );
        areaFellwood = new GeoJsonPolygon(area1);

        List<Point> area2 = List.of(
                new Point(-58.2630, -34.7070),
                new Point(-58.2600, -34.7090),
                new Point(-58.2580, -34.7060),
                new Point(-58.2630, -34.7070)
        );
        areaAshenvale = new GeoJsonPolygon(area2);

        List<Point> area3 = List.of(
                new Point(-58.3610, -34.6600),
                new Point(-58.3590, -34.6620),
                new Point(-58.3570, -34.6590),
                new Point(-58.3610, -34.6600)
        );
        areaSantaMaria = new GeoJsonPolygon(area3);

        Ubicacion santuario = new Santuario("santuario", 100);
        Ubicacion cementerio = new Cementerio("cementerio", 100);
        Ubicacion cementerio2 = new Cementerio("cementerio2", 100);

        ubicacionService.crear(santuario, areaAshenvale);
        ubicacionService.crear(cementerio, areaFellwood);
        ubicacionService.crear(cementerio2, areaSantaMaria);

        Ubicacion santuarioAct = ubicacionService.recuperar(santuario.getId()).get();
        Ubicacion cementerioAct = ubicacionService.recuperar(cementerio.getId()).get();

        ubicacionService.eliminar(santuarioAct);
        ubicacionService.eliminar(cementerioAct);

        santuarioAct = this.recuperarAunConSoftDelete(santuario.getId()).get();
        cementerioAct = this.recuperarAunConSoftDelete(cementerio.getId()).get();

        Collection<Ubicacion> todos = ubicacionService.recuperarTodos();

        assertThrows(EntidadEliminadaException.class, () -> {
            ubicacionService.recuperar(santuario.getId());
        });
        assertThrows(EntidadEliminadaException.class, () -> {
            ubicacionService.recuperar(cementerio.getId());
        });
        assertTrue(santuarioAct.getDeleted());
        assertTrue(cementerioAct.getDeleted());
        assertEquals(1, todos.size());
    }

    public Optional<Ubicacion> recuperarAunConSoftDelete(Long ubicacionId) {
        dataService.revisarId(ubicacionId);
        Ubicacion ubicacion = ubicacionRepository.recuperar(ubicacionId);
        return Optional.of(ubicacion);
    }

    @Test
    void conectarUnidireccional() {
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());

        UbicacionNeo4J origen = ubicacionService.recuperarPorNombre(fellwood.getNombre());
        UbicacionNeo4J destino = ubicacionService.recuperarPorNombre(ashenvale.getNombre());

        assertEquals(0, destino.getUbicaciones().size());
        assertEquals(1, origen.getUbicaciones().size());
    }

    @Test void conectarDosVecesUnidireccionalDistintoDestino() {
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());

        UbicacionNeo4J origen = ubicacionService.recuperarPorNombre(fellwood.getNombre());
        UbicacionNeo4J destino1 = ubicacionService.recuperarPorNombre(ashenvale.getNombre());
        UbicacionNeo4J destino2 = ubicacionService.recuperarPorNombre(santaMaria.getNombre());

        assertEquals(0, destino1.getUbicaciones().size());
        assertEquals(0, destino2.getUbicaciones().size());
        assertEquals(2, origen.getUbicaciones().size());
    }

    @Test
    void conectarDosVecesUnidireccionalMismoDestino() {
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());

        UbicacionNeo4J origen = ubicacionService.recuperarPorNombre(fellwood.getNombre());
        UbicacionNeo4J destino = ubicacionService.recuperarPorNombre(ashenvale.getNombre());

        assertEquals(0, destino.getUbicaciones().size());
        assertEquals(1, origen.getUbicaciones().size());
    }

    @Test
    void conectarUnidireccionalCiclica() {
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), santaMaria.getId());
        ubicacionService.conectar(santaMaria.getId(), fellwood.getId());

        UbicacionNeo4J ubiA = ubicacionService.recuperarPorNombre(fellwood.getNombre());
        UbicacionNeo4J ubiB = ubicacionService.recuperarPorNombre(ashenvale.getNombre());
        UbicacionNeo4J ubiC = ubicacionService.recuperarPorNombre(ashenvale.getNombre());

        assertEquals(1, ubiA.getUbicaciones().size());
        assertEquals(1, ubiB.getUbicaciones().size());
        assertEquals(1, ubiC.getUbicaciones().size());
    }

    @Test
    void conectarBidireccional() {
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), fellwood.getId());

        UbicacionNeo4J origen = ubicacionService.recuperarPorNombre(fellwood.getNombre());
        UbicacionNeo4J destino = ubicacionService.recuperarPorNombre(ashenvale.getNombre());

        assertEquals(1, origen.getUbicaciones().size());
        assertEquals(1, destino.getUbicaciones().size());
    }

    @Test
    void conectarMismaUbicacion() {
        assertThrows(MismaUbicacionException.class, () -> {
            ubicacionService.conectar(fellwood.getId(), fellwood.getId());
        });
    }

    @Test
    void conectarUbicacionConIdNulo() {
        assertThrows(IdNoValidoException.class,() -> {
            ubicacionService.conectar(ashenvale.getId(), null);
        });
        assertThrows(IdNoValidoException.class,() -> {
            ubicacionService.conectar(null, fellwood.getId());
        });
        assertThrows(IdNoValidoException.class,() -> {
            ubicacionService.conectar(null, null);
        });
    }

    @Test
    void conectarUbicacionConIdInexistente() {
        assertThrows(RecursoNoEncontradoException.class,() -> {
            ubicacionService.conectar(fellwood.getId(), 1L);
        });
        assertThrows(RecursoNoEncontradoException.class,() -> {
            ubicacionService.conectar(1L, fellwood.getId());
        });
        assertThrows(RecursoNoEncontradoException.class,() -> {
            ubicacionService.conectar(1L, 2L);
        });
    }

    @Test
    void estanConetcadasDosUbicacionesConectadasUnidireccionalmente() {
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());

        assertTrue(ubicacionService.estanConectadas(fellwood.getId(), ashenvale.getId()));
        assertFalse(ubicacionService.estanConectadas(ashenvale.getId(), fellwood.getId()));
    }

    @Test
    void estanConetcadasDosUbicacionesSinConexion() {
        assertFalse(ubicacionService.estanConectadas(fellwood.getId(), ashenvale.getId()));
    }

    @Test
    void estanConetcadasDosUbicacionesBidireccionalemente() {
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), fellwood.getId());

        assertTrue(ubicacionService.estanConectadas(fellwood.getId(), ashenvale.getId()));
        assertTrue(ubicacionService.estanConectadas(ashenvale.getId(), fellwood.getId()));
    }

    @Test
    void estanConetcadasDosUbicacionesPorMasDeUnSalto() {
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), santaMaria.getId());

        assertFalse(ubicacionService.estanConectadas(fellwood.getId(), santaMaria.getId()));
    }

    @Test
    void estanConectadasConIdNulo() {
        assertThrows(IdNoValidoException.class,() -> {
            ubicacionService.conectar(null, fellwood.getId());
        });
        assertThrows(IdNoValidoException.class,() -> {
            ubicacionService.conectar(fellwood.getId(), null);
        });
        assertThrows(IdNoValidoException.class,() -> {
            ubicacionService.conectar(null, null);
        });
    }

    @Test
    void estanConectadasConIdInexistente() {
        assertThrows(RecursoNoEncontradoException.class,() -> {
            ubicacionService.estanConectadas(fellwood.getId(), 1L);
        });
        assertThrows(RecursoNoEncontradoException.class,() -> {
            ubicacionService.estanConectadas(1L, fellwood.getId());
        });
        assertThrows(RecursoNoEncontradoException.class,() -> {
            ubicacionService.estanConectadas(1L, 2L);
        });
    }

    @Test
    void estanConetcadaUbicacionASiMisma() {
        assertFalse(ubicacionService.estanConectadas(fellwood.getId(), fellwood.getId()));
    }

    @Test
    void recuperarUbicacionesSobreCargadas() {
        List<UbicacionNeo4J> ubicacionesSobrecargadas = ubicacionService.ubicacionesSobrecargadas(50);
        assertEquals(3, ubicacionesSobrecargadas.size());
        List<String> nombres = ubicacionesSobrecargadas.stream()
                                                        .map(UbicacionNeo4J::getNombre)
                                                        .collect(Collectors.toList());
        assertTrue(nombres.contains(ashenvale.getNombre()));
        assertTrue(nombres.contains(santaMaria.getNombre()));
    }

    @Test
    void recuperarNingunaUbicacionSobreCargada() {
        List<UbicacionNeo4J> ubicacionesSobrecargadas = ubicacionService.ubicacionesSobrecargadas(100);
        assertEquals(0, ubicacionesSobrecargadas.size());
    }

    @Test
    void caminoMasCorto1Salto() {
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(santaMaria.getId(), ashenvale.getId());

        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());

        List<UbicacionNeo4J> camino = ubicacionService.caminoMasCorto(fellwood.getId(),  ashenvale.getId());

        assertEquals(2, camino.size());
        assertEquals(camino.get(0).getNombre(), fellwood.getNombre());
        assertEquals(camino.get(1).getNombre(), ashenvale.getNombre());
    }

    @Test
    void caminoMasCorto2Saltos() {
        List<Point> area5 = List.of(
                new Point(-58.3700, -34.6200),
                new Point(-58.3680, -34.6220),
                new Point(-58.3660, -34.6190),
                new Point(-58.3700, -34.6200)
        );
        GeoJsonPolygon areaJardinDePaz = new GeoJsonPolygon(area5);

        List<Point> area6 = List.of(
                new Point(-58.3200, -34.6800),
                new Point(-58.3180, -34.6820),
                new Point(-58.3160, -34.6790),
                new Point(-58.3200, -34.6800)
        );
        GeoJsonPolygon areaSanIgnacio = new GeoJsonPolygon(area6);

        Ubicacion jardinDePaz = new Cementerio("Jardin de Paz", 50);
        ubicacionService.crear(jardinDePaz, areaJardinDePaz);
        Ubicacion sanIgnacio = new Santuario("San Ignacio", 50);
        ubicacionService.crear(sanIgnacio, areaSanIgnacio);

        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(santaMaria.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), sanIgnacio.getId());

        ubicacionService.conectar(fellwood.getId(), jardinDePaz.getId());
        ubicacionService.conectar(jardinDePaz.getId(), sanIgnacio.getId());

        List<UbicacionNeo4J> camino = ubicacionService.caminoMasCorto(fellwood.getId(),  sanIgnacio.getId());

        assertEquals(3, camino.size());
        assertEquals(camino.get(0).getNombre(), fellwood.getNombre());
        assertEquals(camino.get(1).getNombre(), jardinDePaz.getNombre());
        assertEquals(camino.get(2).getNombre(), sanIgnacio.getNombre());
    }

    @Test
    void caminoMasCortoConDosCaminosIguales() {
        List<Point> area5 = List.of(
                new Point(-58.3700, -34.6200),
                new Point(-58.3680, -34.6220),
                new Point(-58.3660, -34.6190),
                new Point(-58.3700, -34.6200)
        );
        GeoJsonPolygon areaJardinDePaz = new GeoJsonPolygon(area5);

        List<Point> area6 = List.of(
                new Point(-58.3200, -34.6800),
                new Point(-58.3180, -34.6820),
                new Point(-58.3160, -34.6790),
                new Point(-58.3200, -34.6800)
        );
        GeoJsonPolygon areaSanIgnacio = new GeoJsonPolygon(area6);

        Ubicacion jardinDePaz = new Cementerio("Jardin de Paz", 50);
        ubicacionService.crear(jardinDePaz, areaJardinDePaz);
        Ubicacion sanIgnacio = new Santuario("San Ignacio", 50);
        ubicacionService.crear(sanIgnacio, areaSanIgnacio);

        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(santaMaria.getId(), ashenvale.getId());

        ubicacionService.conectar(fellwood.getId(), jardinDePaz.getId());
        ubicacionService.conectar(jardinDePaz.getId(), ashenvale.getId());

        List<UbicacionNeo4J> camino = ubicacionService.caminoMasCorto(fellwood.getId(),  ashenvale.getId());
        assertEquals(3, camino.size());
        // Funciona al azar, trae cualquier de los 2 caminos mas cortos
    }

    @Test
    void caminoMasCortoSinConexion() {
        assertThrows(UbicacionesNoConectadasException.class,() -> {
            ubicacionService.caminoMasCorto(fellwood.getId(), ashenvale.getId());
        });
    }

    @Test
    void caminoMasCortoSoloConRelacionesInversas() {
        ubicacionService.conectar(santaMaria.getId(), fellwood.getId());

        assertThrows(UbicacionesNoConectadasException.class, () -> {
            ubicacionService.caminoMasCorto(fellwood.getId(), santaMaria.getId());
        });
    }

    @Test
    void caminoMasCortoConBidireccionales() {
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(santaMaria.getId(), fellwood.getId());
        ubicacionService.conectar(santaMaria.getId(),ashenvale.getId());

        List<UbicacionNeo4J> camino = ubicacionService.caminoMasCorto(fellwood.getId(),  ashenvale.getId());
        assertEquals(3, camino.size());
        assertEquals(camino.get(0).getNombre(), fellwood.getNombre());
        assertEquals(camino.get(1).getNombre(), santaMaria.getNombre());
        assertEquals(camino.get(2).getNombre(), ashenvale.getNombre());
    }


    @Test
    void ubicacionesConSusCloseness() {
        ubicacionService.eliminar(catedral);
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(santaMaria.getId(), ashenvale.getId());
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), fellwood.getId());
        ubicacionService.conectar(santaMaria.getId(), fellwood.getId());

        List<Long> ids = List.of(fellwood.getId(), ashenvale.getId(), santaMaria.getId());

        List<ClosenessResult> closeness = ubicacionService.closenessOf(ids);

        assertEquals(3, closeness.size());
        assertEquals(closeness.get(0).ubicacion().getNombre(), ashenvale.getNombre());
        assertEquals(closeness.get(1).ubicacion().getNombre(), fellwood.getNombre());
        assertEquals(closeness.get(2).ubicacion().getNombre(), santaMaria.getNombre());
        assertEquals((double) 1 /3, closeness.get(0).closeness());
        assertEquals((double) 1 /2, closeness.get(1).closeness());
        assertEquals((double) 1 /2, closeness.get(2).closeness());
    }

    @Test
    void closeness1UbicacionSinConexion() {
        ubicacionService.eliminar(catedral);
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(santaMaria.getId(), fellwood.getId());

        List<Long> ids = List.of(fellwood.getId(), ashenvale.getId(), santaMaria.getId());
        List<ClosenessResult> closeness = ubicacionService.closenessOf(ids);

        assertEquals(3, closeness.size());
        assertEquals(closeness.get(0).ubicacion().getNombre(), ashenvale.getNombre());
        assertEquals(closeness.get(1).ubicacion().getNombre(), fellwood.getNombre());
        assertEquals(closeness.get(2).ubicacion().getNombre(), santaMaria.getNombre());
        assertEquals((double) 1 /20, closeness.get(0).closeness());
        assertEquals((double) 1 /2, closeness.get(1).closeness());
        assertEquals((double) 1 /3, closeness.get(2).closeness());
    }

    @Test
    void closeness1UbicacionSinDestinoNiOrigen() {
        ubicacionService.eliminar(catedral);

        List<Point> area5 = List.of(
                new Point(-58.3700, -34.6200),
                new Point(-58.3680, -34.6220),
                new Point(-58.3660, -34.6190),
                new Point(-58.3700, -34.6200)
        );
        GeoJsonPolygon areaJardinDePaz = new GeoJsonPolygon(area5);

        Ubicacion jardinDePaz = new Cementerio("Jardin de Paz", 50);
        ubicacionService.crear(jardinDePaz, areaJardinDePaz);

        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(santaMaria.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), fellwood.getId());

        List<Long> ids = List.of(fellwood.getId(), ashenvale.getId(), santaMaria.getId(), jardinDePaz.getId());
        List<ClosenessResult> closeness = ubicacionService.closenessOf(ids);

        assertEquals(4, closeness.size());
        assertEquals(closeness.get(0).ubicacion().getNombre(), ashenvale.getNombre());
        assertEquals(closeness.get(1).ubicacion().getNombre(), fellwood.getNombre());
        assertEquals(closeness.get(2).ubicacion().getNombre(), jardinDePaz.getNombre());
        assertEquals(closeness.get(3).ubicacion().getNombre(), santaMaria.getNombre());
        assertEquals((double) 1 / 13, closeness.get(0).closeness());
        assertEquals((double) 1 / 13, closeness.get(1).closeness());
        assertEquals((double) 1 / 30, closeness.get(2).closeness());
        assertEquals((double) 1 / 13, closeness.get(3).closeness());
    }

    @Test
    void closenessSoloPidiendo1Id() {
        ubicacionService.eliminar(catedral);
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(santaMaria.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), fellwood.getId());

        List<Long> ids = List.of(fellwood.getId());
        List<ClosenessResult> closeness = ubicacionService.closenessOf(ids);

        assertEquals(1, closeness.size());
        assertEquals(closeness.getFirst().ubicacion().getNombre(), fellwood.getNombre());
        assertEquals((double) 1 / 3, closeness.getFirst().closeness());
    }

    @Test
    void closenessSinIds() {
        List<Long> ids = List.of();
        List<ClosenessResult> closeness = ubicacionService.closenessOf(ids);
        assertEquals(0, closeness.size());
    }

    @Test
    void degreeTestOutcomming1(){
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(fellwood.getId(), catedral.getId());
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(santaMaria.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), fellwood.getId());

        List<Long> ids = List.of(fellwood.getId(),santaMaria.getId(),ashenvale.getId(),catedral.getId());

        DegreeResult result = ubicacionService.degreeOf(ids, DegreeType.OUTCOMMING);


        assertEquals(result.node().getNombre(),fellwood.getNombre());
        assertEquals((double) 3 / 5 , result.centrality());
        assertEquals(DegreeType.OUTCOMMING, result.typeResult());
    }

    @Test
    void degreeTestOutcommingEmpate(){
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(fellwood.getId(), catedral.getId());
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(santaMaria.getId(), ashenvale.getId());
        ubicacionService.conectar(santaMaria.getId(), catedral.getId());
        ubicacionService.conectar(santaMaria.getId(), fellwood.getId());
        ubicacionService.conectar(ashenvale.getId(), fellwood.getId());

        List<Long> ids = List.of(fellwood.getId(),santaMaria.getId(),ashenvale.getId(),catedral.getId());

        DegreeResult result = ubicacionService.degreeOf(ids, DegreeType.OUTCOMMING);

        List<String> posiblesGanadores = List.of(fellwood.getNombre(),santaMaria.getNombre());
        assertTrue(posiblesGanadores.contains(result.node().getNombre()));
        assertEquals( (double) 3 / 7 ,result.centrality()) ;
        assertEquals(DegreeType.OUTCOMMING, result.typeResult());
    }

    @Test
    void outcommingWithNoId(){
        List<Long> ids = List.of();

        assertThrows(sinResultadosException.class,() -> {
            ubicacionService.degreeOf(ids, DegreeType.OUTCOMMING);
        });
    }

    @Test
    void outcommingWithInexistentId(){
        List<Long> ids = List.of((long) -1);

        assertThrows(sinResultadosException.class,() -> {
            ubicacionService.degreeOf(ids, DegreeType.OUTCOMMING);
        });
    }

    @Test
    void degreeTestIncomming1(){
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(fellwood.getId(), catedral.getId());
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(santaMaria.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), fellwood.getId());

        List<Long> ids = List.of(fellwood.getId(),santaMaria.getId(),ashenvale.getId(),catedral.getId());

        DegreeResult result = ubicacionService.degreeOf(ids, DegreeType.INCOMMING);


        assertEquals(result.node().getNombre(),ashenvale.getNombre());
        assertEquals((double) 2 / 5 , result.centrality());
        assertEquals(DegreeType.INCOMMING, result.typeResult());
    }

    @Test
    void degreeTestIncommingEmpate(){
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(fellwood.getId(), catedral.getId());
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(santaMaria.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), catedral.getId());

        List<Long> ids = List.of(fellwood.getId(),santaMaria.getId(),ashenvale.getId(),catedral.getId());

        DegreeResult result = ubicacionService.degreeOf(ids, DegreeType.INCOMMING);


        List<String> posiblesGanadores = List.of(catedral.getNombre(),ashenvale.getNombre());
        assertTrue(posiblesGanadores.contains(result.node().getNombre()));
        assertEquals((double) 2 / 5, result.centrality());
        assertEquals(DegreeType.INCOMMING, result.typeResult());
    }

    @Test
    void incommingWithNoId(){
        List<Long> ids = List.of();

        assertThrows(sinResultadosException.class,() -> {
            ubicacionService.degreeOf(ids, DegreeType.INCOMMING);
        });
    }

    @Test
    void incommingWithInexistentId(){
        List<Long> ids = List.of((long) -1);

        assertThrows(sinResultadosException.class,() -> {
            ubicacionService.degreeOf(ids, DegreeType.INCOMMING);
        });
    }

    @Test
    void degreeTestAll(){
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(fellwood.getId(), catedral.getId());
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(santaMaria.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), fellwood.getId());

        List<Long> ids = List.of(fellwood.getId(),santaMaria.getId(),ashenvale.getId(),catedral.getId());

        DegreeResult result = ubicacionService.degreeOf(ids, DegreeType.ALL);


        assertEquals(result.node().getNombre(),fellwood.getNombre());
        assertEquals((double) 4 / 5, result.centrality());
        assertEquals(DegreeType.ALL, result.typeResult());
    }

    @Test
    void degreeTestAllEmpate(){
        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(fellwood.getId(), catedral.getId());
        ubicacionService.conectar(fellwood.getId(), ashenvale.getId());
        ubicacionService.conectar(santaMaria.getId(), catedral.getId());
        ubicacionService.conectar(ashenvale.getId(), catedral.getId());

        List<Long> ids = List.of(fellwood.getId(),santaMaria.getId(),ashenvale.getId(),catedral.getId());

        DegreeResult result = ubicacionService.degreeOf(ids, DegreeType.ALL);

        List<String> posiblesGanadores = List.of(catedral.getNombre(),fellwood.getNombre());
        assertTrue(posiblesGanadores.contains(result.node().getNombre()));
        assertEquals((double) 3 / 5 , result.centrality());
        assertEquals(DegreeType.ALL, result.typeResult());
    }

    @Test
    void allWithNoId(){
        List<Long> ids = List.of();

        assertThrows(sinResultadosException.class,() -> {
            ubicacionService.degreeOf(ids, DegreeType.ALL);
        });
    }

    @Test
    void allWithInexistentId(){
        List<Long> ids = List.of((long) -1);

        assertThrows(sinResultadosException.class,() -> {
            ubicacionService.degreeOf(ids, DegreeType.ALL);
        });
    }

    @AfterEach
    void cleanUp() {
        dataService.eliminarTodo();
        ubicacionRepository.eliminarTodos();
    }
}

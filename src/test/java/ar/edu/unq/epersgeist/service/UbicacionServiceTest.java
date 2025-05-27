package ar.edu.unq.epersgeist.service;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.enums.DegreeType;
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


    @BeforeEach
    void prepare() {
        fellwood = new Cementerio("Fellwood", 50);
        ubicacionService.crear(fellwood);
        ashenvale = new Santuario("Ashenvale",100);
        ubicacionService.crear(ashenvale);
        santaMaria = new Santuario("SantaMaria", 80);
        ubicacionService.crear(santaMaria);
        catedral = new Santuario("catedral", 80);
        ubicacionService.crear(catedral);

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
        Ubicacion santuarioAct = ubicacionService.recuperar(ashenvale.getId()).get();
        Ubicacion cementerioAct = ubicacionService.recuperar(fellwood.getId()).get();

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
        revisarId(ubicacionId);
        Ubicacion ubicacion = ubicacionRepository.recuperar(ubicacionId);
        return Optional.of(ubicacion);
    }

    private void revisarId(Long id) {
        if (id == null || id <= 0) {
            throw new IdNoValidoException();
        }
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
        Ubicacion jardinDePaz = new Cementerio("Jardin de Paz", 50);
        ubicacionService.crear(jardinDePaz);
        Ubicacion sanIgnacio = new Santuario("San Ignacio", 50);
        ubicacionService.crear(sanIgnacio);

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
        Ubicacion jardinDePaz = new Cementerio("Jardin de Paz", 50);
        ubicacionService.crear(jardinDePaz);
        Ubicacion sanIgnacio = new Santuario("San Ignacio", 50);
        ubicacionService.crear(sanIgnacio);

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
        assertEquals(closeness.get(0).ubicacion().getNombre(), fellwood.getNombre());
        assertEquals(closeness.get(1).ubicacion().getNombre(), ashenvale.getNombre());
        assertEquals(closeness.get(2).ubicacion().getNombre(), santaMaria.getNombre());
        assertEquals((double) 1 /2, closeness.get(0).closeness());
        assertEquals((double) 1 /3, closeness.get(1).closeness());
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
        assertEquals(closeness.get(0).ubicacion().getNombre(), fellwood.getNombre());
        assertEquals(closeness.get(1).ubicacion().getNombre(), ashenvale.getNombre());
        assertEquals(closeness.get(2).ubicacion().getNombre(), santaMaria.getNombre());
        assertEquals((double) 1 /2, closeness.get(0).closeness());
        assertEquals((double) 1 /20, closeness.get(1).closeness());
        assertEquals((double) 1 /3, closeness.get(2).closeness());
    }

    @Test
    void closeness1UbicacionSinDestinoNiOrigen() {
        ubicacionService.eliminar(catedral);
        Ubicacion jardinDePaz = new Cementerio("Jardin de Paz", 50);
        ubicacionService.crear(jardinDePaz);

        ubicacionService.conectar(fellwood.getId(), santaMaria.getId());
        ubicacionService.conectar(santaMaria.getId(), ashenvale.getId());
        ubicacionService.conectar(ashenvale.getId(), fellwood.getId());

        List<Long> ids = List.of(fellwood.getId(), ashenvale.getId(), santaMaria.getId(), jardinDePaz.getId());
        List<ClosenessResult> closeness = ubicacionService.closenessOf(ids);

        assertEquals(4, closeness.size());
        assertEquals(closeness.get(0).ubicacion().getNombre(), fellwood.getNombre());
        assertEquals(closeness.get(1).ubicacion().getNombre(), ashenvale.getNombre());
        assertEquals(closeness.get(2).ubicacion().getNombre(), santaMaria.getNombre());
        assertEquals(closeness.get(3).ubicacion().getNombre(), jardinDePaz.getNombre());
        assertEquals((double) 1 / 13, closeness.get(0).closeness());
        assertEquals((double) 1 / 13, closeness.get(1).closeness());
        assertEquals((double) 1 / 13, closeness.get(2).closeness());
        assertEquals((double) 1 / 30, closeness.get(3).closeness());
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
        assertEquals(result.centrality(), (double) 3 / 5 );
        assertEquals(result.typeResult(), DegreeType.OUTCOMMING);
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
        assertEquals(result.centrality(), (double) 3 / 7 );
        assertEquals(result.typeResult(), DegreeType.OUTCOMMING);
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
        assertEquals(result.centrality(), (double) 2 / 5 );
        assertEquals(result.typeResult(), DegreeType.INCOMMING);
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
        assertEquals(result.centrality(), (double) 2 / 5 );
        assertEquals(result.typeResult(), DegreeType.INCOMMING);
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
        assertEquals(result.centrality(), (double) 4 / 5 );
        assertEquals(result.typeResult(), DegreeType.ALL);
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
        assertEquals(result.centrality(), (double) 3 / 5 );
        assertEquals(result.typeResult(), DegreeType.ALL);
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
    }
}

package ar.edu.unq.epersgeist.service;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.exception.*;
import ar.edu.unq.epersgeist.persistencia.dao.CoordenadaDAOMongo;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.MediumRepository;
import ar.edu.unq.epersgeist.servicios.exception.*;
import ar.edu.unq.epersgeist.service.dataService.DataService;
import ar.edu.unq.epersgeist.servicios.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class MediumServiceTest {

    @Autowired
    private DataService dataService;

    @Autowired
    private MediumService mediumService;
    @Autowired
    private UbicacionService ubicacionService;
    @Autowired
    private EspirituService espirituService;
    @Autowired
    private MediumRepository repository;
    @Autowired
    private CoordenadaDAOMongo coordenadaDAO;
    private Medium medium;
    private Medium medium2;
    private GeneradorNumeros dado;
    private Espiritu espiritu;
    private Espiritu espiritu2;
    private Ubicacion bernal;
    private Ubicacion santuario;
    private Ubicacion cementerio;
    private Medium medium3;
    private GeoJsonPolygon areaSantuario;
    private GeoJsonPolygon areaBernal;
    private GeoJsonPolygon areaCementerio;
    private CoordenadaMongo coordenadaMedium;
    private CoordenadaMongo coordenadaMedium2;
    private CoordenadaMongo coordenadaMedium3;
    private CoordenadaMongo coordenadaEspiritu;
    private CoordenadaMongo coordenadaEspiritu2;

    @BeforeEach
    void setUp() {
        List<Point> area1 = List.of(
                new Point(-58.2730, -34.7210),
                new Point(-58.2700, -34.7230),
                new Point(-58.2680, -34.7200),
                new Point(-58.2730, -34.7210)
        );
        areaBernal = new GeoJsonPolygon(area1);

        List<Point> area2 = List.of(
                new Point(-58.2630, -34.7070),
                new Point(-58.2600, -34.7090),
                new Point(-58.2580, -34.7060),
                new Point(-58.2630, -34.7070)
        );
        areaSantuario = new GeoJsonPolygon(area2);

        List<Point> area3 = List.of(
                new Point(-58.3610, -34.6600),
                new Point(-58.3590, -34.6620),
                new Point(-58.3570, -34.6590),
                new Point(-58.3610, -34.6600)
        );
        areaCementerio = new GeoJsonPolygon(area3);

        bernal = new Cementerio("Bernal", 50);
        ubicacionService.crear(bernal, areaBernal);

        santuario = new Santuario("Abadía de St. Carta", 30);
        ubicacionService.crear(santuario, areaSantuario);

        cementerio = new Cementerio("Cementerio de Derry", 50);
        ubicacionService.crear(cementerio, areaCementerio);

        medium = new Medium("Lizzie",150,0);
        medium.setUbicacion(bernal);
        mediumService.crear(medium);
        coordenadaMedium = new CoordenadaMongo(new GeoJsonPoint(-58.2730, -34.7210), "MEDIUM", medium.getId());
        coordenadaDAO.save(coordenadaMedium);

        medium2 = new Medium("Lala", 100, 0);
        medium2.setUbicacion(bernal);
        mediumService.crear(medium2);
        coordenadaMedium2 = new CoordenadaMongo(new GeoJsonPoint(-58.2730, -34.7210), "MEDIUM", medium2.getId());
        coordenadaDAO.save(coordenadaMedium2);

        medium3 = new Medium("Lorraine", 100, 50);
        medium3.setUbicacion(bernal);
        mediumService.crear(medium3);
        coordenadaMedium3 = new CoordenadaMongo(new GeoJsonPoint(-58.2730, -34.7210), "MEDIUM", medium3.getId());
        coordenadaDAO.save(coordenadaMedium3);

        espiritu = new Angel("Casper");
        espiritu.setNivelConexion(5);
        espiritu.setUbicacion(bernal);
        espirituService.crear(espiritu);
        coordenadaEspiritu = new CoordenadaMongo(new GeoJsonPoint(-58.2730, -34.7210), "ESPIRITU", espiritu.getId());
        coordenadaDAO.save(coordenadaEspiritu);

        espiritu2 = new Demonio("Ghosty");
        espiritu2.setNivelConexion(40);
        espiritu2.setUbicacion(bernal);
        espirituService.crear(espiritu2);
        coordenadaEspiritu2 = new CoordenadaMongo(new GeoJsonPoint(-58.2730, -34.7210), "ESPIRITU", espiritu2.getId());
        coordenadaDAO.save(coordenadaEspiritu2);

        this.dado = Dado.getInstance();
    }

    @Test
    void crearMedium() {
        assertNotNull(medium.getId());
    }

    @Test
    void crearMismoMediumDosVeces(){
        Long id1 = medium.getId();
        mediumService.crear(medium);
        assertEquals(medium.getId(), id1);
    }

    @Test
    void recuperarMedium() {
        Medium mediumperado = mediumService.recuperar(medium.getId());
        assertNotNull(mediumperado);
        assertEquals(medium.getId(), mediumperado.getId());
    }

    @Test
    void recuperarMediumConIdInvalido(){
        assertThrows(RecursoNoEncontradoException.class, () -> {
            mediumService.recuperar(125L);
        });
    }

    @Test
    void recuperarMediumConIdNulo() {
        assertThrows(IdNoValidoException.class, () -> {
            mediumService.recuperar(null);
        });
    }

//    @Test
//    void recuperarMediumMongo() {
//        MediumMongo medium = mediumService.recuperarMongo(medium2.getId());
//        assertEquals(medium2.getId(), medium.getMediumIdSQL());
//        AreaMongo ubicacionMongo = ubicacionService.recuperarPorCoordenada(medium.getCoordenada());
//        assertEquals(medium2.getUbicacion().getId(), ubicacionMongo.getIdUbicacion());
//    }

    @Test
    void recuperarTodos() {
        Collection<Medium> mediums = mediumService.recuperarTodos();
        assertEquals(3, mediums.size());
        List<String> nombres = mediums.stream().map(Medium::getNombre).toList();
        assertEquals(List.of( "Lala","Lizzie", "Lorraine"), nombres);
    }

    @Test
    void recuperarTodosSinMediums(){
        mediumService.eliminarTodo();
        Collection<Medium> mediums = mediumService.recuperarTodos();
        assertEquals(0, mediums.size());
    }

    @Test
    void eliminarMedium() {
        Long mediumId = medium.getId();
        assertDoesNotThrow(() -> mediumService.recuperar(mediumId));
        mediumService.eliminar(medium);
        assertThrows(EntidadEliminadaException.class, () -> {
            mediumService.recuperar(mediumId);
        });
    }

    @Test
    void eliminarMediumYaEliminado(){
        mediumService.eliminar(medium);
        assertThrows(EntidadEliminadaException.class, () -> {
            mediumService.eliminar(medium);
        });
    }

    @Test
    void eliminarTodosLosMediums() {
        Long mediumId = medium.getId();
        Long mediumId2 = medium2.getId();

        assertDoesNotThrow(() -> mediumService.recuperar(mediumId));
        assertDoesNotThrow(() -> mediumService.recuperar(mediumId2));

        mediumService.eliminarTodo();
        assertThrows(RecursoNoEncontradoException.class, () -> {
            mediumService.recuperar(mediumId);
        });
        assertThrows(RecursoNoEncontradoException.class, () -> {
            mediumService.recuperar(mediumId2);
        });

    }

    @Test
    void actualizarMedium(){
        Medium sinActualizar = mediumService.recuperar(medium.getId());
        medium.setNombre("Juan");
        mediumService.actualizar(medium);
        Medium actualizado = mediumService.recuperar(medium.getId());
        assertEquals(sinActualizar.getId(), medium.getId());
        assertEquals("Lizzie", sinActualizar.getNombre());
        assertEquals("Juan", actualizado.getNombre());
    }

    @Test
    void actualizarMediumEliminado(){
        mediumService.eliminar(medium);
        assertThrows(EntidadEliminadaException.class, () -> {
            mediumService.actualizar(medium);
        });
    }

    @Test
    void actualizarMediumNoRegistrado(){
        Medium medium = new Medium("Ed", 30, 5);
        assertThrows(IdNoValidoException.class, () -> {
            mediumService.actualizar(medium);
        });
    }


    @Test
    void exorcizarMedium1a1Victorioso(){
        dado.setModo(new ModoTrucado(6,60));
        espiritu = espirituService.recuperar(espiritu.getId()).get();
        espiritu.setNivelConexion(80);
        espirituService.actualizar(espiritu);

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();

        assertEquals(1, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertTrue(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
    }

    @Test
    void exorcizarMedium1a1VictoriosoTeniendoDemoniacosYangelicalesDeMas(){
        dado.setModo(new ModoTrucado(6,60));
        espiritu.setNivelConexion(80);
        espirituService.actualizar(espiritu);

        Espiritu azael = new Demonio("Azael");
        azael.setNivelConexion(40);
        espirituService.crear(azael);
        Espiritu castiel = new Angel( "Castiel");
        castiel.setNivelConexion(40);
        espirituService.crear(castiel);

        azael.setUbicacion(bernal);
        espirituService.actualizar(azael);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());
        espirituService.conectar(azael.getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();
        Espiritu azaelAct = espirituService.recuperar(azael.getId()).get();
        Espiritu castielAct = espirituService.recuperar(castiel.getId()).get();

        assertEquals(2, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertTrue(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
        assertFalse(azaelAct.estaLibre());
        assertFalse(castielAct.estaLibre());
    }

    @Test
    void exorcizarMedium1a1DerrotaSinDesconexion(){
        dado.setModo(new ModoTrucado(6,60));

        espiritu.setNivelConexion(40);
        espirituService.actualizar(espiritu);

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();

        assertEquals(1, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
    }

    @Test
    void exorcizarMedium1a1DerrotaConDesconexion(){

        dado.setModo(new ModoTrucado(6,60));

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();

        assertEquals(0, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.estaLibre());
        assertTrue(espirituAct.estaLibre());
    }

    @Test
    void exorcizarMedium2a1Victoria(){
        dado.setModo(new ModoTrucado(5,10));
        espiritu.setNivelConexion(30);
        espirituService.actualizar(espiritu);
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(50);
        espirituService.crear(castiel);

        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();
        Espiritu castielAct = espirituService.recuperar(castiel.getId()).get();

        assertEquals(2, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertTrue(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
        assertFalse(castielAct.estaLibre());
    }

    @Test
    void exorcizarMedium2a1AmbasDerrotasSinDesconexion(){
        dado.setModo(new ModoTrucado(5,90));
        espiritu.setNivelConexion(30);
        espirituService.actualizar(espiritu);
        Espiritu kyu = new Angel("Kyu");
        kyu.setNivelConexion(30);
        espirituService.crear(kyu);
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(50);
        espirituService.crear(castiel);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);


        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();
        Espiritu castielAct = espirituService.recuperar(castiel.getId()).get();

        assertEquals(2, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
        assertFalse(castielAct.estaLibre());
    }

    @Test
    void exorcizarMedium2a1AmbasDerrotasConDesconexion(){
        dado.setModo(new ModoTrucado(5,90));
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(5);
        espirituService.crear(castiel);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();
        Espiritu castielAct = espirituService.recuperar(castiel.getId()).get();

        assertEquals(0, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.estaLibre());
        assertTrue(espirituAct.estaLibre());
        assertTrue(castielAct.estaLibre());
    }

    @Test
    void exorcizarMedium2a1UnaDerrotaYUnaVictoriaSinDesconexion(){
        dado.setModo(new ModoTrucado(5,40));
        espiritu.setNivelConexion(10);
        espirituService.actualizar(espiritu);
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(40);
        espirituService.crear(castiel);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();
        Espiritu castielAct = espirituService.recuperar(castiel.getId()).get();

        assertEquals(2, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
        assertFalse(castielAct.estaLibre());
    }

    @Test
    void exorcizarMedium2a1UnaDerrotaYUnaVictoriaConDesconexion(){
        dado.setModo(new ModoTrucado(5,40));
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(40);
        espirituService.crear(castiel);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();
        Espiritu castielAct = espirituService.recuperar(castiel.getId()).get();

        assertEquals(1, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.estaLibre());
        assertTrue(espirituAct.estaLibre());
        assertFalse(castielAct.estaLibre());
    }

    @Test
    void exorcizarMedium2a1UnaDerrotaYUnaVictoriaConDesconexionDeAmbosLados() {
        dado.setModo(new ModoTrucado(5, 40));
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(40);
        espirituService.crear(castiel);
        espiritu2.setNivelConexion(20);
        espirituService.actualizar(espiritu2);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();
        Espiritu castielAct = espirituService.recuperar(castiel.getId()).get();

        assertEquals(1, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertTrue(espiritu2Act.estaLibre());
        assertTrue(espirituAct.estaLibre());
        assertFalse(castielAct.estaLibre());
    }

    @Test
    void exorcizarMedium2a2VictoriaAbsoluta(){
        dado.setModo(new ModoTrucado(5,40));
        espiritu.setNivelConexion(50);
        espirituService.actualizar(espiritu);
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(70);
        espirituService.crear(castiel);
        espiritu2.setNivelConexion(25);
        espirituService.actualizar(espiritu2);
        Espiritu azael = new Demonio("azael");
        azael.setNivelConexion(35);
        espirituService.crear(azael);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);
        azael.setUbicacion(bernal);
        espirituService.actualizar(azael);

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());
        espirituService.conectar(azael.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();
        Espiritu castielAct = espirituService.recuperar(castiel.getId()).get();
        Espiritu azaelAct = espirituService.recuperar(azael.getId()).get();

        assertEquals(2, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertTrue(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
        assertFalse(castielAct.estaLibre());
        assertTrue(azaelAct.estaLibre());
    }

    @Test
    void exorcizarMedium2a2DerrotaAbsolutaSinYConDesconexion(){
        dado.setModo(new ModoTrucado(5,70));
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(10);
        espirituService.crear(castiel);
        espiritu2.setNivelConexion(25);
        espirituService.actualizar(espiritu2);
        Espiritu azael = new Demonio("azael");
        azael.setNivelConexion(35);
        espirituService.crear(azael);

        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);
        azael.setUbicacion(bernal);
        espirituService.actualizar(azael);

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());
        espirituService.conectar(azael.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();
        Espiritu castielAct = espirituService.recuperar(castiel.getId()).get();
        Espiritu azaelAct = espirituService.recuperar(azael.getId()).get();

        assertEquals(1, espiritusMedium1.size());
        assertEquals(2, espiritusMedium2.size());
        assertFalse(espiritu2Act.estaLibre());
        assertTrue(espirituAct.estaLibre());
        assertFalse(castielAct.estaLibre());
        assertFalse(azaelAct.estaLibre());
    }

    @Test
    void exorcizarMedium2a2UnaVictoriaYUnaDerrotaConDesconexionDeAmbosLados(){
        dado.setModo(new ModoTrucado(5,70));
        espiritu.setNivelConexion(70);
        espirituService.actualizar(espiritu);
        espiritu2.setNivelConexion(25);
        espirituService.actualizar(espiritu2);
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(5);
        espirituService.crear(castiel);
        Espiritu azael = new Demonio("azael");
        azael.setNivelConexion(35);
        espirituService.crear(azael);

        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);
        azael.setUbicacion(bernal);
        espirituService.actualizar(azael);

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());
        espirituService.conectar(azael.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId()).get();
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();
        Espiritu castielAct = espirituService.recuperar(castiel.getId()).get();
        Espiritu azaelAct = espirituService.recuperar(azael.getId()).get();

        assertEquals(1, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertTrue(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
        assertTrue(castielAct.estaLibre());
        assertFalse(azaelAct.estaLibre());
    }

    @Test
    void exorcizarEjemplo(){
        dado.setModo(new ModoTrucado(5,60));

        Espiritu rika = new Angel("Rika");
        rika.setNivelConexion(60);
        espirituService.crear(rika);
        Espiritu ivaar = new Angel("Ivaar");
        ivaar.setNivelConexion(80);
        espirituService.crear(ivaar);
        Espiritu hana = new Angel("Hana");
        hana.setNivelConexion(5);
        espirituService.crear(hana);
        Espiritu jeager = new Demonio("Jeager");
        jeager.setNivelConexion(50);
        espirituService.crear(jeager);
        Espiritu noroi = new Demonio("Noroi");
        noroi.setNivelConexion(66);
        espirituService.crear(noroi);

        rika.setUbicacion(bernal);
        espirituService.actualizar(rika);
        ivaar.setUbicacion(bernal);
        espirituService.actualizar(ivaar);
        hana.setUbicacion(bernal);
        espirituService.actualizar(hana);
        jeager.setUbicacion(bernal);
        espirituService.actualizar(jeager);
        noroi.setUbicacion(bernal);
        espirituService.actualizar(noroi);

        espirituService.conectar(rika.getId(), medium.getId());
        espirituService.conectar(ivaar.getId(), medium.getId());
        espirituService.conectar(hana.getId(), medium.getId());
        espirituService.conectar(jeager.getId(), medium2.getId());
        espirituService.conectar(noroi.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu jeagerAct = espirituService.recuperar(jeager.getId()).get();
        Espiritu ivaarAct = espirituService.recuperar(ivaar.getId()).get();
        Espiritu rikaAct = espirituService.recuperar(rika.getId()).get();
        Espiritu hanaAct = espirituService.recuperar(hana.getId()).get();
        Espiritu noroiAct = espirituService.recuperar(noroi.getId()).get();

        assertEquals(2, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertTrue(jeagerAct.estaLibre());
        assertFalse(noroiAct.estaLibre());
        assertTrue(hanaAct.estaLibre());
        assertFalse(ivaarAct.estaLibre());
        assertFalse(rikaAct.estaLibre());
    }

    @Test
    void noHayAngelesException(){
        assertThrows(NoHayAngelesException.class,()->{
            mediumService.exorcizar(medium.getId(), medium2.getId());
        });
    }

    @Test
    void noHayDemonios(){
        espirituService.conectar(espiritu.getId(), medium.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        assertEquals(1, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertFalse(espirituAct.estaLibre());
    }

    @Test
    void descansarMedium(){
        Medium sinDescansar = mediumService.recuperar(medium.getId());
        mediumService.descansar(medium.getId());
        Medium descansado = mediumService.recuperar(medium.getId());
        assertEquals(sinDescansar.getId(), descansado.getId());
        assertNotEquals(sinDescansar.getMana(), descansado.getMana());
    }

    @Test
    void descansarMediumConDemonioEnCementerio(){
        espirituService.conectar(espiritu2.getId(), medium3.getId());
        mediumService.descansar(medium3.getId());

        Medium descansadomedium = mediumService.recuperar(medium3.getId());
        Espiritu descansadoEspiritu = espirituService.recuperar(espiritu2.getId()).get();

        assertEquals(medium3.getId(), descansadomedium.getId());
        assertEquals(1, descansadomedium.getEspiritus().size());
        assertEquals(descansadoEspiritu.getMedium().getId(), descansadomedium.getId());
        assertEquals(75, descansadomedium.getMana());
        assertEquals(100, descansadoEspiritu.getNivelConexion());
    }

    @Test
    void descansarMediumConAngelEnCementerio(){
        espirituService.conectar(espiritu.getId(), medium3.getId());

        Espiritu espirituNoDescansado = espirituService.recuperar(espiritu.getId()).get();
        Medium mediumNoDescansado = mediumService.recuperar(medium3.getId());
        assertEquals(50, mediumNoDescansado.getMana());

        mediumService.descansar(medium3.getId());
        Medium mediumDescansado = mediumService.recuperar(medium3.getId());
        Espiritu espirituDescansado = espirituService.recuperar(espiritu.getId()).get();

        assertEquals(espirituDescansado.getMedium().getId(), mediumDescansado.getId());
        assertEquals(espirituNoDescansado.getNivelConexion(), espirituDescansado.getNivelConexion());
        assertNotEquals(mediumNoDescansado.getMana(), mediumDescansado.getMana());
        assertEquals(75, mediumDescansado.getMana());
    }

    @Test
    void descansarMediumConAngelEnSantuario(){
        espiritu.setUbicacion(santuario);
        medium3.setUbicacion(santuario);
        mediumService.actualizar(medium3);
        espirituService.actualizar(espiritu);
        espirituService.conectar(espiritu.getId(), medium3.getId());

        mediumService.descansar(medium3.getId());

        Medium descansadomedium = mediumService.recuperar(medium3.getId());
        Espiritu descansadoEspiritu = espirituService.recuperar(espiritu.getId()).get();

        assertEquals(medium3.getId(), descansadomedium.getId());
        assertEquals(1,
                descansadomedium.getEspiritus().size());
        assertEquals(descansadoEspiritu.getMedium().getId(), descansadomedium.getId());
        assertEquals(95, descansadomedium.getMana());
        assertEquals(45, descansadoEspiritu.getNivelConexion());
    }

    @Test
    void descansarMediumConDemonioEnSantuario(){
        espiritu2.setUbicacion(santuario);
        medium3.setUbicacion(santuario);
        mediumService.actualizar(medium3);
        espirituService.actualizar(espiritu2);

        espirituService.conectar(espiritu2.getId(), medium3.getId());

        Espiritu espirituNoDescansado = espirituService.recuperar(espiritu2.getId()).get();
        Medium mediumNoDescansado = mediumService.recuperar(medium3.getId());
        assertEquals(50, mediumNoDescansado.getMana());

        mediumService.descansar(medium3.getId());
        Medium mediumDescansado = mediumService.recuperar(medium3.getId());
        Espiritu espirituDescansado = espirituService.recuperar(espiritu2.getId()).get();

        assertEquals(espirituDescansado.getMedium().getId(), mediumDescansado.getId());
        assertEquals(espirituNoDescansado.getNivelConexion(), espirituDescansado.getNivelConexion());
        assertNotEquals(mediumNoDescansado.getMana(), mediumDescansado.getMana());
        assertEquals(95, mediumDescansado.getMana());
    }


    @Test
    void descansarMediumConIdNulo(){
        assertThrows(IdNoValidoException.class, () -> {
            mediumService.descansar(null);
        });
    }

    @Test
    void descansarMediumConIdInexistente(){
        assertThrows(RecursoNoEncontradoException.class,()->{
            mediumService.descansar(1258L);
        });
    }

    @Test
    void invocarEspirituLibreConManaSuficiente() {
        espiritu2.setUbicacion(cementerio);
        espirituService.actualizar(espiritu2);

        Espiritu espirituInvocado = mediumService.invocar(medium3.getId(), espiritu2.getId());
        medium3 = mediumService.recuperar(medium3.getId());

        assertEquals(medium3.getMana(), 40);
        assertEquals(espirituInvocado.getUbicacion(), medium3.getUbicacion());
    }

    @Test
    void invocarEspirituLibreEnMismaUbicacion() {
        Espiritu espirituInvocado = mediumService.invocar(medium3.getId(), espiritu2.getId());
        medium3 = mediumService.recuperar(medium3.getId());

        assertEquals(medium3.getMana(), 40);
        assertEquals(espirituInvocado.getUbicacion().getId(), espiritu2.getUbicacion().getId());
    }

    @Test
    void invocarDemonioLibreEnCementerio() {
        medium3.setUbicacion(cementerio);
        mediumService.actualizar(medium3);

        Espiritu espirituInvocado = mediumService.invocar(medium3.getId(), espiritu2.getId());
        medium3 = mediumService.recuperar(medium3.getId());

        assertEquals(medium3.getMana(), 40);
        assertEquals(medium3.getUbicacion().getId(), espirituInvocado.getUbicacion().getId());
    }

    @Test
    void invocacacionFallidaAngelEnCementerio() {
        medium3.setUbicacion(cementerio);
        mediumService.actualizar(medium3);

        assertThrows(InvocacionFallidaPorUbicacionException.class, () -> {
            mediumService.invocar(medium3.getId(), espiritu.getId());
        });
    }

    @Test
    void invocarAngelLibreEnSantuario() {
        medium3.setUbicacion(santuario);
        mediumService.actualizar(medium3);

        Espiritu espirituInvocado = mediumService.invocar(medium3.getId(), espiritu.getId());
        medium3 = mediumService.recuperar(medium3.getId());

        assertEquals(medium3.getMana(), 40);
        assertEquals(medium3.getUbicacion().getId(), espirituInvocado.getUbicacion().getId());
    }

    @Test
    void invocacacionFallidaDemonioEnSantuario() {
        medium3.setUbicacion(santuario);
        mediumService.actualizar(medium3);

        assertThrows(InvocacionFallidaPorUbicacionException.class, () -> {
            mediumService.invocar(medium3.getId(), espiritu2.getId());
        });
    }

    @Test
    void invocarEspirituNoLibre() {
        espirituService.conectar(espiritu2.getId(), medium.getId());
        assertThrows(EspirituNoLibreException.class, () -> mediumService.invocar(medium3.getId(), espiritu2.getId()));
    }

    @Test
    void invocarEspirituSinMana() {
        Espiritu espirituAntes = espirituService.recuperar(espiritu.getId()).get();
        Espiritu espirituNoInvocado = mediumService.invocar(medium.getId(), espiritu.getId());

        assertEquals(espirituNoInvocado.getMedium(), espirituAntes.getMedium());
        assertEquals(espirituNoInvocado.getUbicacion(), espirituAntes.getUbicacion());
    }

    @Test
    void invocarEspirituConIdMediumNull() {
        assertThrows(IdNoValidoException.class, () -> {
            mediumService.recuperar(null);
        });
    }

    @Test
    void invocarEspirituConIdMediumInvalido() {
        assertThrows(RecursoNoEncontradoException.class,()->{
            mediumService.invocar(2025L, espiritu2.getId());
        });
    }

    @Test
    void invocarEspirituConIdEspirituNull() {
        assertThrows(IdNoValidoException.class,()->{
            mediumService.invocar(medium3.getId(), null);
        });
    }

    @Test
    void invocarEspirituConIdEspirituInvalido() {
        assertThrows(RecursoNoEncontradoException.class,()->{
            mediumService.invocar(medium3.getId(), 2025L);
        });
    }

    @Test
    void espiritusDeMedium(){
        espirituService.conectar(espiritu.getId(), medium2.getId());
        assertEquals(1, (mediumService.espiritus(medium2.getId())).size());
    }

    @Test
    void espiritusDeMediumSinEspiritus(){
        assertEquals(0, (mediumService.espiritus(medium2.getId())).size());
    }

    @Test
    void espiritusDeMediumConVariosEspiritus(){
        espirituService.conectar(espiritu.getId(), medium2.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());
        assertEquals(2, (mediumService.espiritus(medium2.getId())).size());
    }

    @Test
    void espiritusDeMediumConIdInvalida(){
        assertThrows(RecursoNoEncontradoException.class,()->{
            mediumService.espiritus(2025L);
        });
    }

    @Test
    void espiritusDeMediumConIdNull(){
        assertThrows(IdNoValidoException.class, () -> {
        mediumService.espiritus(null);
        });
    }

    @Test
    void movimientoDeMediumYEspiritusConConexionSuficienteASantuario() {
        medium = espirituService.conectar(espiritu.getId(), medium.getId()).get();
        medium = espirituService.conectar(espiritu2.getId(), medium.getId()).get();

        ubicacionService.conectar(bernal.getId(),santuario.getId());

        mediumService.mover(medium.getId(), -34.7070, -58.2630);

        Medium actualizado = mediumService.recuperar(medium.getId());
        List<Espiritu> espiritus = mediumService.espiritus(actualizado.getId());

        Espiritu demonio = espiritus.get(1);
        Espiritu angel = espiritus.get(0);

        AreaMongo ubicacionMongo = ubicacionService.recuperarPorCoordenada(new GeoJsonPoint(-58.2630, -34.7070));
        assertEquals(santuario.getId(), ubicacionMongo.getIdUbicacion());

        assertEquals(2, espiritus.size());
        assertEquals(santuario.getNombre(), actualizado.getUbicacion().getNombre());
        assertEquals(santuario.getNombre(), angel.getUbicacion().getNombre());
        assertEquals(santuario.getNombre(), demonio.getUbicacion().getNombre());
        assertEquals(angel.getMedium().getId(), actualizado.getId());
        assertEquals(demonio.getMedium().getId(), actualizado.getId());
        assertEquals(demonio.getNivelConexion(), 30);
        assertNotEquals(demonio.getNivelConexion(), espiritu2.getNivelConexion());
    }

    @Test
    void movimientoDeMediumYEspiritusConConexionSuficienteACementerio() {
        espiritu.setNivelConexion(10);
        espirituService.actualizar(espiritu);
        medium = espirituService.conectar(espiritu.getId(), medium.getId()).get();
        medium = espirituService.conectar(espiritu2.getId(), medium.getId()).get();

        ubicacionService.conectar(bernal.getId(),cementerio.getId());

        mediumService.mover(medium.getId(), -34.6600, -58.3610);

        Medium actualizado = mediumService.recuperar(medium.getId());
        List<Espiritu> espiritus = mediumService.espiritus(actualizado.getId());

        Espiritu demonio = espiritus.get(1);
        Espiritu angel = espiritus.get(0);

        AreaMongo ubicacionMongo = ubicacionService.recuperarPorCoordenada(new GeoJsonPoint(-58.3610, -34.6600));
        assertEquals(cementerio.getId(), ubicacionMongo.getIdUbicacion());

        assertEquals(2, espiritus.size());
        assertEquals(cementerio.getId(), actualizado.getUbicacion().getId());
        assertEquals(cementerio.getId(), angel.getUbicacion().getId());
        assertEquals(cementerio.getId(), demonio.getUbicacion().getId());
        assertEquals(angel.getMedium().getId(), actualizado.getId());
        assertEquals(demonio.getMedium().getId(), actualizado.getId());
        assertEquals(angel.getNivelConexion(),5);
        assertEquals(demonio.getNivelConexion(), espiritu2.getNivelConexion());
    }

    @Test
    void movimientoDeMediumASantuarioDemonioPierdeConexionYSeDesvincula() {
        espiritu2.setNivelConexion(1);
        espirituService.actualizar(espiritu2);

        medium = espirituService.conectar(espiritu2.getId(), medium.getId()).get();

        List<Espiritu> espiritusAntes = mediumService.espiritus(medium.getId());
        assertEquals(1, espiritusAntes.size());

        ubicacionService.conectar(bernal.getId(),santuario.getId());

        mediumService.mover(medium.getId(), -34.7070, -58.2630);

        Medium actualizado = mediumService.recuperar(medium.getId());
        List<Espiritu> espiritusDespues = mediumService.espiritus(actualizado.getId());

        AreaMongo ubicacionMongo = ubicacionService.recuperarPorCoordenada(new GeoJsonPoint(-58.2630, -34.7070));
        assertEquals(santuario.getId(), ubicacionMongo.getIdUbicacion());

        assertEquals(0, espiritusDespues.size());

        Espiritu demonioRecuperado = espirituService.recuperar(espiritu2.getId()).get();
        assertNull(demonioRecuperado.getMedium());
        assertEquals(santuario.getNombre(), actualizado.getUbicacion().getNombre());
        assertEquals(santuario.getNombre(), demonioRecuperado.getUbicacion().getNombre());
        assertNull(demonioRecuperado.getMedium());
        assertEquals(demonioRecuperado.getNivelConexion(), 0);
    }

    @Test
    void movimientoDeMediumACementerioAngelPierdeConexionYSeDesvincula() {
        espiritu.setNivelConexion(1);
        espirituService.actualizar(espiritu);

        medium = espirituService.conectar(espiritu.getId(), medium.getId()).get();

        List<Espiritu> espiritusAntes = mediumService.espiritus(medium.getId());
        assertEquals(1, espiritusAntes.size());

        ubicacionService.conectar(bernal.getId(),cementerio.getId());

        mediumService.mover(medium.getId(), -34.6600, -58.3610);

        Medium actualizado = mediumService.recuperar(medium.getId());
        List<Espiritu> espiritusDespues = mediumService.espiritus(actualizado.getId());

        AreaMongo ubicacionMongo = ubicacionService.recuperarPorCoordenada(new GeoJsonPoint(-58.3610, -34.6600));
        assertEquals(cementerio.getId(), ubicacionMongo.getIdUbicacion());

        assertEquals(0, espiritusDespues.size());

        Espiritu angelRecuperado = espirituService.recuperar(espiritu.getId()).get();
        assertNull(angelRecuperado.getMedium());
        assertEquals(cementerio.getNombre(), actualizado.getUbicacion().getNombre());
        assertEquals(cementerio.getNombre(), angelRecuperado.getUbicacion().getNombre());
        assertNull(angelRecuperado.getMedium());
        assertEquals(angelRecuperado.getNivelConexion(), 0);
    }

    @Test
    void movimientoDeMediumAMismaUbicacion() {
        assertThrows(MovimientoInvalidoException.class, () -> {
            mediumService.mover(medium2.getId() , -34.7230,-58.2700);
        });
    }

    @Test
    void movimientoDeMediumConIdInexistente() {
        assertThrows(RecursoNoEncontradoException.class,() -> {
            mediumService.mover(25L, -34.7070, -58.2630);;
        });
    }

    @Test
    void movimientoDeMediumAUbicacionConCoordenadasInvalidas() {
        assertThrows(UncategorizedMongoDbException.class,() -> {
            mediumService.mover(medium2.getId(), -34.7070, -358.0);;
        });
    }

    @Test
    void movimientoDeMediumConIdNulo() {
        assertThrows(IdNoValidoException.class,() -> {
            mediumService.mover(null,-34.7070, -58.2630);;
        });
    }

    @Test
    void movimientoDeMediumAUbicacionCercana() {
        medium = espirituService.conectar(espiritu.getId(), medium.getId()).get();
        ubicacionService.conectar(bernal.getId(), santuario.getId());

        mediumService.mover(medium.getId(), -34.7070, -58.2630);

        AreaMongo ubicacionMongo = ubicacionService.recuperarPorCoordenada(new GeoJsonPoint(-58.2630, -34.7070));
        assertEquals(santuario.getId(), ubicacionMongo.getIdUbicacion());

        CoordenadaMongo coordenadaActualizadaMedium = coordenadaDAO.findByEntityIdAndEntityType(medium.getId(), "MEDIUM").get();
        CoordenadaMongo coordenadaActualizadaEspiritu = coordenadaDAO.findByEntityIdAndEntityType(espiritu.getId(), "ESPIRITU").get();
        assertEquals(-58.2630, coordenadaActualizadaMedium.getLongitud());
        assertEquals(-34.7070, coordenadaActualizadaMedium.getLatitud());
        assertEquals(-58.2630, coordenadaActualizadaEspiritu.getLongitud());
        assertEquals(-34.7070, coordenadaActualizadaEspiritu.getLatitud());
    }

    @Test
    void movimientoDeMediumSinUbicacionPrevia() {
        medium.setUbicacion(null);
        mediumService.actualizar(medium);
        coordenadaDAO.delete(coordenadaMedium);

        mediumService.mover(medium.getId(), -34.7070, -58.2630);

        AreaMongo ubicacionMongo = ubicacionService.recuperarPorCoordenada(new GeoJsonPoint(-58.2630, -34.7070));
        assertEquals(santuario.getId(), ubicacionMongo.getIdUbicacion());

        CoordenadaMongo coordenadaActualizadaMedium = coordenadaDAO.findByEntityIdAndEntityType(medium.getId(), "MEDIUM").get();
        assertEquals(-58.2630, coordenadaActualizadaMedium.getLongitud());
        assertEquals(-34.7070, coordenadaActualizadaMedium.getLatitud());
    }

    @Test
    void movimientoDeMediumAUbicacionConCoordenadasQueNoPertencenANingunaUbicacion() {
        assertThrows(RecursoNoEncontradoException.class,() -> {
            mediumService.mover(medium2.getId(),55.5,152.5);
        });
    }

    @Test
    void movimientoDeMediumAUbicacionLejana() {
        List<Point> area2 = List.of(
                new Point(-59.1042, -34.5700),
                new Point(-59.1012, -34.5720),
                new Point(-59.0992, -34.5690),
                new Point(-59.1042, -34.5700)
        );
        GeoJsonPolygon areaLujan = new GeoJsonPolygon(area2);
        Ubicacion lujan = new Cementerio("Lujan", 50);
        ubicacionService.crear(lujan, areaLujan);

        assertThrows(UbicacionLejanaException.class,() -> {
            mediumService.mover(medium2.getId(), -34.5700, -59.1042);
        });
    }

    @Test
    void exorcismoEnDiferentesUbicaciones(){
        espiritu.setUbicacion(cementerio);
        espirituService.actualizar(espiritu2);

        medium.setUbicacion(cementerio);
        mediumService.actualizar(medium);
        medium2.setUbicacion(santuario);
        mediumService.actualizar(medium2);

        medium.conectarseAEspiritu(espiritu);
        mediumService.actualizar(medium);

        assertThrows(ExorcismoEnDiferenteUbicacionException.class, () -> {
            mediumService.exorcizar(medium.getId(), medium2.getId());
        });
    }

    @Test
    void autoExorcismo(){
        medium.setUbicacion(cementerio);
        mediumService.actualizar(medium);


        assertThrows(MismoMediumException.class, () -> {
            mediumService.exorcizar(medium.getId(), medium.getId());
        });
    }

    //test de auditoria de datos
    @Test
    void creacionTimeStampUpdateAndNoDelete(){
        Medium medium = new Medium("pedro",100,20);

        mediumService.crear(medium);

        Medium mediumAct = mediumService.recuperar(medium.getId());

        assertNotNull(mediumAct.getCreatedAt());
        assertNotNull(mediumAct.getUpdatedAt());
        assertFalse(mediumAct.getDeleted());

    }


    @Test
    void updateTimeStamp() throws InterruptedException {
        Medium medium = new Medium("pedro",100,20);

        mediumService.crear(medium);

        Medium mediumAct = mediumService.recuperar(medium.getId());

        Thread.sleep(1000);

        mediumAct.setNombre("juancho");
        mediumService.actualizar(mediumAct);
        mediumAct = mediumService.recuperar(mediumAct.getId());

        int comparison = mediumAct.getUpdatedAt().compareTo(mediumAct.getCreatedAt());

        assertTrue(comparison > 0);

    }

    @Test
    void updateTimeStampDoble() throws InterruptedException {
        Medium medium = new Medium("pedro",100,20);

        mediumService.crear(medium);

        Medium mediumAct = mediumService.recuperar(medium.getId());

        Thread.sleep(1000);

        mediumAct.setNombre("juancho");
        mediumService.actualizar(mediumAct);
        mediumAct = mediumService.recuperar(mediumAct.getId());
        Date lastUpdate = mediumAct.getUpdatedAt();

        Thread.sleep(1000);

        mediumAct.setNombre("pedritos");
        mediumService.actualizar(mediumAct);
        mediumAct = mediumService.recuperar(mediumAct.getId());

        int comparison = mediumAct.getUpdatedAt().compareTo(lastUpdate);

        assertTrue(comparison > 0);

    }


    @Test
    void softDeletion(){
        Medium medium = new Medium("pedro",100,20);

        mediumService.crear(medium);

        Medium mediumAct = mediumService.recuperar(medium.getId());

        mediumService.eliminar(mediumAct);

        Medium mediumBorrado = this.recuperarAunConSoftDelete(mediumAct.getId()).get();
        assertThrows(EntidadEliminadaException.class, () -> {
            mediumService.recuperar(mediumAct.getId());
        });
        assertTrue(mediumBorrado.getDeleted());

    }

    @Test
    void noRecuperaTodosConSoftdelete(){
        dataService.eliminarTodo();
        Medium medium = new Medium("pedro",100,20);
        Medium medium2 = new Medium("juan",100,20);

        mediumService.crear(medium);
        mediumService.crear(medium2);

        Medium mediumAct = mediumService.recuperar(medium.getId());

        mediumService.eliminar(mediumAct);

        Collection<Medium> todos = mediumService.recuperarTodos();


        Medium mediumBorrado = this.recuperarAunConSoftDelete(mediumAct.getId()).get();

        assertThrows(EntidadEliminadaException.class, () -> {
            mediumService.recuperar(mediumAct.getId());
        });
        assertTrue(mediumBorrado.getDeleted());
        assertEquals(todos.size(),1);
    }

    @Test
    void softDeleteEnExorcismo(){
        dado.setModo(new ModoTrucado(6,60));
        espiritu = espirituService.recuperar(espiritu.getId()).get();
        espiritu.setNivelConexion(80);
        espirituService.actualizar(espiritu);

        espirituService.conectar(espiritu.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        Espiritu espirituAct = espirituService.recuperar(espiritu.getId()).get();

        espirituService.eliminar(espirituAct);

        assertThrows(NoHayAngelesException.class, () -> {
            mediumService.exorcizar(medium.getId(), medium2.getId());
        });
    }

    public Optional<Medium> recuperarAunConSoftDelete(Long mediumId) {
        dataService.revisarId(mediumId);
        Medium medium = repository.recuperar(mediumId);
        return Optional.of(medium);
    }

    @Test
    void moverNuevo(){
        ubicacionService.conectar(bernal.getId(),santuario.getId());
        mediumService.mover(medium.getId(), -34.7070, -58.2630);

        Medium mediumAct = mediumService.recuperar(medium.getId());

        assertEquals(mediumAct.getUbicacion().getId(), santuario.getId());
    }

    @Test
    void moverNuevoException(){

        assertThrows(UbicacionLejanaException.class, () -> {
            mediumService.mover(medium.getId(), -34.7070, -58.2630);
        });
    }

    @Test
    void moverConectadoAlReves(){
        ubicacionService.conectar(santuario.getId(),bernal.getId());

        assertThrows(UbicacionLejanaException.class, () -> {
            mediumService.mover(medium.getId(), -34.7070, -58.2630);
        });
    }

    @Test
    void moverNuevoBidireccional(){
        ubicacionService.conectar(bernal.getId(),santuario.getId());
        ubicacionService.conectar(santuario.getId(),bernal.getId());
        mediumService.mover(medium.getId(), -34.7070, -58.2630);

        Medium mediumAct = mediumService.recuperar(medium.getId());

        assertEquals(mediumAct.getUbicacion().getId(), santuario.getId());
    }

    @AfterEach
    void cleanUp() {
        dataService.eliminarTodo();
        coordenadaDAO.deleteAll();
        dado.setModo(new ModoRandom());
    }
}
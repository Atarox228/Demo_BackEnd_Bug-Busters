package ar.edu.unq.epersgeist.service;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.servicios.exception.MovimientoInvalidoException;
import ar.edu.unq.epersgeist.modelo.exception.EspirituNoLibreException;
import ar.edu.unq.epersgeist.modelo.exception.InvocacionFallidaPorUbicacionException;
import ar.edu.unq.epersgeist.modelo.exception.NoHayAngelesException;
import ar.edu.unq.epersgeist.service.dataService.DataService;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;



@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class MediumServiceTest {

    private DataService dataService;

    @Autowired
    private MediumService mediumService;
    @Autowired
    private UbicacionService ubicacionService;
    @Autowired
    private EspirituService espirituService;
    private Medium medium;
    private Medium medium2;
    private GeneradorNumeros dado;
    private Espiritu espiritu;
    private Espiritu espiritu2;
    private Ubicacion bernal;
    private Ubicacion santuario;
    private Ubicacion cementerio;
    private Medium medium3;

    @BeforeEach
    void setUp() {
        bernal = new Cementerio("Bernal", 50);
        ubicacionService.crear(bernal);

        santuario = new Santuario("Abadía de St. Carta", 30);
        ubicacionService.crear(santuario);

        cementerio = new Cementerio("Cementerio de Derry", 50);
        ubicacionService.crear(cementerio);

        medium = new Medium("Lizzie",150,0);
        medium.setUbicacion(bernal);
        mediumService.crear(medium);

        medium2 = new Medium("Lala", 100, 0);
        medium2.setUbicacion(bernal);
        mediumService.crear(medium2);

        medium3 = new Medium("Lorraine", 100, 50);
        medium3.setUbicacion(bernal);
        mediumService.crear(medium3);

        espiritu = new Angel("Casper");
        espiritu.setNivelConexion(5);
        espiritu.setUbicacion(bernal);
        espirituService.crear(espiritu);

        espiritu2 = new Demonio("Ghosty");
        espiritu2.setNivelConexion(40);
        espiritu2.setUbicacion(bernal);
        espirituService.crear(espiritu2);
        
        this.dado = Dado.getInstance();
    }

    @Test
    void crearMedium() {
        assertNotNull(medium.getId());
    }

    @Test
    void crearMismoMediumDosVeces(){
        assertThrows(IdNoValidoException.class, () -> {
            mediumService.crear(medium);
        });
    }

    @Test
    void recuperarMedium() {
        Optional<Medium> mediumperado = mediumService.recuperar(medium.getId());
        assertNotNull(mediumperado);
        assertEquals(medium.getId(), mediumperado.get().getId());
    }

    @Test
    void recuperarMediumConIdInvalido(){
        assertFalse(mediumService.recuperar(125L).isPresent());
    }

    @Test
    void recuperarMediumConIdNulo() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            mediumService.recuperar(null);
        });
    }

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
        assertTrue(mediumService.recuperar(mediumId).isPresent());
        mediumService.eliminar(medium);
        assertFalse(mediumService.recuperar(mediumId).isPresent());
    }
/*
    @Test
    void eliminarMediumYaEliminado(){
        mediumService.eliminar(medium);
        assertThrows(OptimisticLockException.class, () -> {
            mediumService.eliminar(medium);
        });
    }
*/
    @Test
    void eliminarTodosLosMediums() {
        Long mediumId = medium.getId();
        Long mediumId2 = medium2.getId();

        assertTrue(mediumService.recuperar(mediumId).isPresent());
        assertTrue(mediumService.recuperar(mediumId2).isPresent());

        mediumService.eliminarTodo();

        assertFalse(mediumService.recuperar(mediumId).isPresent());
        assertFalse(mediumService.recuperar(mediumId2).isPresent());
    }

    @Test
    void actualizarMedium(){
        Optional<Medium> sinActualizar = mediumService.recuperar(medium.getId());
        medium.setNombre("Juan");
        mediumService.actualizar(medium);
        Optional<Medium> actualizado = mediumService.recuperar(medium.getId());
        assertEquals(sinActualizar.get().getId(), medium.getId());
        assertEquals("Lizzie", sinActualizar.get().getNombre());
        assertEquals("Juan", actualizado.get().getNombre());
    }

    @Test
    void actualizarMediumEliminado(){
        mediumService.eliminar(medium);
        assertThrows(IdNoValidoException.class, () -> {
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

        Optional<Espiritu> espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Optional<Espiritu> espirituAct = espirituService.recuperar(espiritu.getId());

        assertEquals(1, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertTrue(espiritu2Act.get().estaLibre());
        assertFalse(espirituAct.get().estaLibre());
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

        Optional<Espiritu> espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Optional<Espiritu> espirituAct = espirituService.recuperar(espiritu.getId());
        Optional<Espiritu> azaelAct = espirituService.recuperar(azael.getId());
        Optional<Espiritu> castielAct = espirituService.recuperar(castiel.getId());

        assertEquals(2, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertTrue(espiritu2Act.get().estaLibre());
        assertFalse(espirituAct.get().estaLibre());
        assertFalse(azaelAct.get().estaLibre());
        assertFalse(castielAct.get().estaLibre());
    }

    @Test
    void exorcizarMedium1a1DerrotaSinDesconexion(){
        dado.setModo(new ModoTrucado(6,60));

        espiritu.get().setNivelConexion(40);
        espirituService.actualizar(espiritu.get());

        espirituService.conectar(espiritu.get().getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Optional<Espiritu> espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Optional<Espiritu> espirituAct = espirituService.recuperar(espiritu.get().getId());

        assertEquals(1, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.get().estaLibre());
        assertFalse(espirituAct.get().estaLibre());
    }

    @Test
    void exorcizarMedium1a1DerrotaConDesconexion(){

        dado.setModo(new ModoTrucado(6,60));

        espirituService.conectar(espiritu.get().getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Optional<Espiritu> espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Optional<Espiritu> espirituAct = espirituService.recuperar(espiritu.get().getId());

        assertEquals(0, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.get().estaLibre());
        assertTrue(espirituAct.get().estaLibre());
    }

    @Test
    void exorcizarMedium2a1Victoria(){
        dado.setModo(new ModoTrucado(5,10));
        espiritu.get().setNivelConexion(30);
        espirituService.actualizar(espiritu.get());
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(50);
        espirituService.crear(castiel);

        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);

        espirituService.conectar(espiritu.get().getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Optional<Espiritu> espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Optional<Espiritu> espirituAct = espirituService.recuperar(espiritu.get().getId());
        Optional<Espiritu> castielAct = espirituService.recuperar(castiel.getId());

        assertEquals(2, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertTrue(espiritu2Act.get().estaLibre());
        assertFalse(espirituAct.get().estaLibre());
        assertFalse(castielAct.get().estaLibre());
    }

    @Test
    void exorcizarMedium2a1AmbasDerrotasSinDesconexion(){
        dado.setModo(new ModoTrucado(5,90));
        espiritu.get().setNivelConexion(30);
        espirituService.actualizar(espiritu.get());
        Espiritu kyu = new Angel("Kyu");
        kyu.setNivelConexion(30);
        espirituService.crear(kyu);
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(50);
        espirituService.crear(castiel);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);


        espirituService.conectar(espiritu.get().getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Optional<Espiritu> espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Optional<Espiritu> espirituAct = espirituService.recuperar(espiritu.get().getId());
        Optional<Espiritu> castielAct = espirituService.recuperar(castiel.getId());

        assertEquals(2, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.get().estaLibre());
        assertFalse(espirituAct.get().estaLibre());
        assertFalse(castielAct.get().estaLibre());
    }

    @Test
    void exorcizarMedium2a1AmbasDerrotasConDesconexion(){
        dado.setModo(new ModoTrucado(5,90));
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(5);
        espirituService.crear(castiel);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);

        espirituService.conectar(espiritu.get().getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Optional<Espiritu> espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());

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

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());

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

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());

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

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());

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

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());
        Espiritu azaelAct = espirituService.recuperar(azael.getId());

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

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());
        Espiritu azaelAct = espirituService.recuperar(azael.getId());

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

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());
        Espiritu azaelAct = espirituService.recuperar(azael.getId());

        assertEquals(1, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertTrue(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
        assertTrue(castielAct.estaLibre());
        assertFalse(azaelAct.estaLibre());
    }

    @Test
    void exorcizarMedium1a1VictoriosoTeniendoDemoniacosYAngelicalesDeMas(){
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

        Espiritu jeagerAct = espirituService.recuperar(jeager.getId());
        Espiritu ivaarAct = espirituService.recuperar(ivaar.getId());
        Espiritu rikaAct = espirituService.recuperar(rika.getId());
        Espiritu hanaAct = espirituService.recuperar(hana.getId());
        Espiritu noroiAct = espirituService.recuperar(noroi.getId());

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
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        assertEquals(1, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertFalse(espirituAct.estaLibre());
    }

    @Test
    void descansarMedium(){
        Optional<Medium> sinDescansar = mediumService.recuperar(medium.getId());
        mediumService.descansar(medium.getId());
        Optional<Medium> descansado = mediumService.recuperar(medium.getId());
        assertEquals(sinDescansar.get().getId(), descansado.get().getId());
        assertNotEquals(sinDescansar.get().getMana(), descansado.get().getMana());
    }

    @Test
    void descansarMediumConDemonioEnCementerio(){
        espirituService.conectar(espiritu2.getId(), medium3.getId());
        mediumService.descansar(medium3.getId());

        Optional<Medium> descansadomedium = mediumService.recuperar(medium3.getId());
        Espiritu descansadoEspiritu = espirituService.recuperar(espiritu2.getId());

        assertEquals(medium3.getId(), descansadomedium.get().getId());
        assertEquals(1, descansadomedium.get().getEspiritus().size());
        assertEquals(descansadoEspiritu.getMedium().getId(), descansadomedium.get().getId());
        assertEquals(75, descansadomedium.get().getMana());
        assertEquals(100, descansadoEspiritu.getNivelConexion());
    }

    @Test
    void descansarMediumConAngelEnCementerio(){
        espirituService.conectar(espiritu.getId(), medium3.getId());

        Espiritu espirituNoDescansado = espirituService.recuperar(espiritu.getId());
        Optional<Medium> mediumNoDescansado = mediumService.recuperar(medium3.getId());
        assertEquals(50, mediumNoDescansado.get().getMana());

        mediumService.descansar(medium3.getId());
        Optional<Medium> mediumDescansado = mediumService.recuperar(medium3.getId());
        Espiritu espirituDescansado = espirituService.recuperar(espiritu.getId());

        assertEquals(espirituDescansado.getMedium().getId(), mediumDescansado.get().getId());
        assertEquals(espirituNoDescansado.getNivelConexion(), espirituDescansado.getNivelConexion());
        assertNotEquals(mediumNoDescansado.get().getMana(), mediumDescansado.get().getMana());
        assertEquals(75, mediumDescansado.get().getMana());
    }

    @Test
    void descansarMediumConAngelEnSantuario(){
        espiritu.setUbicacion(santuario);
        medium3.setUbicacion(santuario);
        mediumService.actualizar(medium3);
        espirituService.actualizar(espiritu);
        espirituService.conectar(espiritu.getId(), medium3.getId());

        mediumService.descansar(medium3.getId());

        Optional<Medium> descansadomedium = mediumService.recuperar(medium3.getId());
        Espiritu descansadoEspiritu = espirituService.recuperar(espiritu.getId());

        assertEquals(medium3.getId(), descansadomedium.get().getId());
        assertEquals(1, descansadomedium.get().getEspiritus().size());
        assertEquals(descansadoEspiritu.getMedium().getId(), descansadomedium.get().getId());
        assertEquals(95, descansadomedium.get().getMana());
        assertEquals(45, descansadoEspiritu.getNivelConexion());
    }

    @Test
    void descansarMediumConDemonioEnSantuario(){
        espiritu2.setUbicacion(santuario);
        medium3.setUbicacion(santuario);
        mediumService.actualizar(medium3);
        espirituService.actualizar(espiritu2);

        espirituService.conectar(espiritu2.getId(), medium3.getId());

        Espiritu espirituNoDescansado = espirituService.recuperar(espiritu2.getId());
        Optional<Medium> mediumNoDescansado = mediumService.recuperar(medium3.getId());
        assertEquals(50, mediumNoDescansado.get().getMana());

        mediumService.descansar(medium3.getId());
        Optional<Medium> mediumDescansado = mediumService.recuperar(medium3.getId());
        Espiritu espirituDescansado = espirituService.recuperar(espiritu2.getId());

        assertEquals(espirituDescansado.getMedium().getId(), mediumDescansado.get().getId());
        assertEquals(espirituNoDescansado.getNivelConexion(), espirituDescansado.getNivelConexion());
        assertNotEquals(mediumNoDescansado.get().getMana(), mediumDescansado.get().getMana());
        assertEquals(95, mediumDescansado.get().getMana());
    }


    @Test
    void descansarMediumConIdNulo(){
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
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
        espiritu2.setUbicacion(cementerio);
        espirituService.actualizar(espiritu2);

        Optional<Espiritu> espirituInvocado = mediumService.invocar(medium3.getId(), espiritu2.getId());
        medium3 = mediumService.recuperar(medium3.getId()).get();

        assertEquals(medium3.getMana(), 40);
        assertEquals(espirituInvocado.get().getUbicacion(), medium3.getUbicacion());
    }

    @Test
    void invocarEspirituLibreEnMismaUbicacion() {
        Optional<Espiritu> espirituInvocado = mediumService.invocar(medium3.getId(), espiritu2.getId());
        medium3 = mediumService.recuperar(medium3.getId()).get();

        assertEquals(medium3.getMana(), 40);
        assertEquals(espirituInvocado.get().getUbicacion(), espiritu2.getUbicacion());
    }

    @Test
    void invocarDemonioLibreEnCementerio() {
        medium3.setUbicacion(cementerio);
        mediumService.actualizar(medium3);

        Optional<Espiritu> espirituInvocado = mediumService.invocar(medium3.getId(), espiritu2.getId());
        medium3 = mediumService.recuperar(medium3.getId()).get();

        assertEquals(medium3.getMana(), 40);
        assertEquals(medium3.getUbicacion().getId(), espirituInvocado.get().getUbicacion().getId());
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

        Optional<Espiritu> espirituInvocado = mediumService.invocar(medium3.getId(), espiritu.getId());
        medium3 = mediumService.recuperar(medium3.getId()).get();

        assertEquals(medium3.getMana(), 40);
        assertEquals(medium3.getUbicacion().getId(), espirituInvocado.get().getUbicacion().getId());
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
        Espiritu espirituAntes = espirituService.recuperar(espiritu.getId());
        Optional<Espiritu> espirituNoInvocado = mediumService.invocar(medium.getId(), espiritu.getId());

        assertEquals(espirituNoInvocado.get().getMedium(), espirituAntes.getMedium());
        assertEquals(espirituNoInvocado.get().getUbicacion(), espirituAntes.getUbicacion());
    }

    @Test
    void invocarEspirituConIdMediumNull() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            mediumService.recuperar(null);
        });
    }

    @Test
    void invocarEspirituConIdMediumInvalido() {
        assertThrows(IdNoValidoException.class,()->{
            mediumService.invocar(2025L, espiritu2.getId());
        });
    }

    @Test
    void invocarEspirituConIdEspirituNull() {
        assertThrows(InvalidDataAccessApiUsageException.class,()->{
            mediumService.invocar(medium3.getId(), null);
        });
    }

    @Test
    void invocarEspirituConIdEspirituInvalido() {
        assertThrows(IdNoValidoException.class,()->{
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
        assertThrows(IdNoValidoException.class,()->{
            mediumService.espiritus(2025L);
        });
    }

    @Test
    void espiritusDeMediumConIdNull(){
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
        mediumService.espiritus(null);
        });
    }

    @Test
    void movimientoDeMediumYEspiritusConConexionSuficienteASantuario() {
        medium = espirituService.conectar(espiritu.getId(), medium.getId());
        medium = espirituService.conectar(espiritu2.getId(), medium.getId());

        mediumService.mover(medium.getId(), santuario.getId());

        Optional<Medium> actualizado = mediumService.recuperar(medium.getId());
        List<Espiritu> espiritus = mediumService.espiritus(actualizado.get().getId());

        Espiritu demonio = espiritus.get(1);
        Espiritu angel = espiritus.get(0);

        assertEquals(2, espiritus.size());
        assertEquals(santuario.getNombre(), actualizado.get().getUbicacion().getNombre());
        assertEquals(santuario.getNombre(), angel.getUbicacion().getNombre());
        assertEquals(santuario.getNombre(), demonio.getUbicacion().getNombre());
        assertEquals(angel.getMedium().getId(), actualizado.get().getId());
        assertEquals(demonio.getMedium().getId(), actualizado.get().getId());
        assertEquals(angel.getNivelConexion(), espiritu.getNivelConexion());
        assertNotEquals(demonio.getNivelConexion(), espiritu2.getNivelConexion());
    }

    @Test
    void movimientoDeMediumYEspiritusConConexionSuficienteACementerio() {
        espiritu.setNivelConexion(10);
        espirituService.actualizar(espiritu);
        medium = espirituService.conectar(espiritu.getId(), medium.getId());
        medium = espirituService.conectar(espiritu2.getId(), medium.getId());

        mediumService.mover(medium.getId(), cementerio.getId());

        Optional<Medium> actualizado = mediumService.recuperar(medium.getId());
        List<Espiritu> espiritus = mediumService.espiritus(actualizado.get().getId());

        Espiritu demonio = espiritus.get(1);
        Espiritu angel = espiritus.get(0);

        assertEquals(2, espiritus.size());
        assertEquals(cementerio.getId(), actualizado.get().getUbicacion().getId());
        assertEquals(cementerio.getId(), angel.getUbicacion().getId());
        assertEquals(cementerio.getId(), demonio.getUbicacion().getId());
        assertEquals(angel.getMedium().getId(), actualizado.get().getId());
        assertEquals(demonio.getMedium().getId(), actualizado.get().getId());
        assertNotEquals(angel.getNivelConexion(), espiritu.getNivelConexion());
        assertEquals(demonio.getNivelConexion(), espiritu2.getNivelConexion());
    }

    @Test
    void movimientoDeMediumASantuarioDemonioPierdeConexionYSeDesvincula() {
        espiritu2.setNivelConexion(1);
        espirituService.actualizar(espiritu2);

        medium = espirituService.conectar(espiritu2.getId(), medium.getId());

        List<Espiritu> espiritusAntes = mediumService.espiritus(medium.getId());
        assertEquals(1, espiritusAntes.size());

        mediumService.mover(medium.getId(), santuario.getId());

        Optional<Medium> actualizado = mediumService.recuperar(medium.getId());
        List<Espiritu> espiritusDespues = mediumService.espiritus(actualizado.get().getId());

        assertEquals(0, espiritusDespues.size());

        Espiritu demonioRecuperado = espirituService.recuperar(espiritu2.getId());
        assertNull(demonioRecuperado.getMedium());
        assertEquals(santuario.getNombre(), actualizado.get().getUbicacion().getNombre());
        assertEquals(santuario.getNombre(), demonioRecuperado.getUbicacion().getNombre());
        assertNull(demonioRecuperado.getMedium());
        assertEquals(demonioRecuperado.getNivelConexion(), 0);
    }

    @Test
    void movimientoDeMediumACementerioAngelPierdeConexionYSeDesvincula() {
        espiritu.setNivelConexion(1);
        espirituService.actualizar(espiritu);

        medium = espirituService.conectar(espiritu.getId(), medium.getId());

        List<Espiritu> espiritusAntes = mediumService.espiritus(medium.getId());
        assertEquals(1, espiritusAntes.size());

        mediumService.mover(medium.getId(), cementerio.getId());

        Optional<Medium> actualizado = mediumService.recuperar(medium.getId());
        List<Espiritu> espiritusDespues = mediumService.espiritus(actualizado.get().getId());

        assertEquals(0, espiritusDespues.size());

        Espiritu angelRecuperado = espirituService.recuperar(espiritu.getId());
        assertNull(angelRecuperado.getMedium());
        assertEquals(cementerio.getNombre(), actualizado.get().getUbicacion().getNombre());
        assertEquals(cementerio.getNombre(), angelRecuperado.getUbicacion().getNombre());
        assertNull(angelRecuperado.getMedium());
        assertEquals(angelRecuperado.getNivelConexion(), 0);
    }

    @Test
    void movimientoDeMediumAMismaUbicacion() {
        assertThrows(MovimientoInvalidoException.class, () -> {
            mediumService.mover(medium2.getId(), bernal.getId());
        });
    }

    @Test
    void movimientoDeMediumConIdInexistente() {
        assertThrows(IdNoValidoException.class,() -> {
            mediumService.mover(25L, santuario.getId());;
        });
    }

    @Test
    void movimientoDeMediumAUbicacionConIdInexistente() {
        assertThrows(IdNoValidoException.class,() -> {
            mediumService.mover(medium2.getId(), 21L);;
        });
    }

    @Test
    void movimientoDeMediumConIdNulo() {
        assertThrows(InvalidDataAccessApiUsageException.class,() -> {
            mediumService.mover(null, santuario.getId());;
        });
    }

    @Test
    void movimientoDeMediumAUbicacionConIdNulo() {
        assertThrows(InvalidDataAccessApiUsageException.class,() -> {
            mediumService.mover(medium2.getId(), null);
        });
    }

    @AfterEach
    void cleanUp() {
        espirituService.eliminarTodo();
        mediumService.eliminarTodo();
        ubicacionService.clearAll();
        //dado.setModo(new ModoRandom());
    }
}


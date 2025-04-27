package ar.edu.unq.epersgeist.service;

import ar.edu.unq.epersgeist.modelo.*;
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
    private Optional<Medium> mediumRecu;
    private Optional<Medium> mediumRecu2;
    private GeneradorNumeros dado;
    private Espiritu espiritu;
    private Espiritu espiritu2;
    private Espiritu espirituRecu;
    private Espiritu espirituRecu2;
    private Ubicacion bernal;
    private Ubicacion santuario;
    private Ubicacion cementerio;

    @BeforeEach
    void setUp() {
        bernal = new Cementerio("Bernal", 100);
        ubicacionService.crear(bernal);

        santuario = new Santuario("Abadía de St. Carta", 100);
        ubicacionService.crear(santuario);

        cementerio = new Cementerio("Cementerio de Derry", 100);
        ubicacionService.crear(cementerio);

        medium = new Medium("Lizzie",150,100);
        medium.setUbicacion(bernal);
        mediumService.crear(medium);

        medium2 = new Medium("Lala", 100, 50);
        medium2.setUbicacion(bernal);
        mediumService.crear(medium2);

        mediumRecu = mediumService.recuperar(medium.getId());
        mediumRecu.get().setMana(0);
        mediumService.actualizar(mediumRecu.orElse(null));

        mediumRecu2 = mediumService.recuperar(medium2.getId());
        mediumRecu2.get().setMana(0);
        mediumService.actualizar(mediumRecu2.orElse(null));

        espiritu = new Angel("Casper");
        espiritu.setNivelConexion(5);
        espiritu.setUbicacion(bernal);
        espirituService.crear(espiritu);

        espiritu2 = new Demonio("Ghosty");
        espiritu2.setNivelConexion(40);
        espiritu2.setUbicacion(bernal);
        espirituService.crear(espiritu2);

        espiritu.setUbicacion(bernal);
        espirituService.actualizar(espiritu);

        espirituRecu = espirituService.recuperar(espiritu.getId());
        espirituRecu2 = espirituService.recuperar(espiritu2.getId());

        //mediumService.mover(medium.getId(),bernal.getId());
        //mediumService.mover(medium2.getId(),bernal.getId());

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
        Optional<Medium> mediumRecuperado = mediumService.recuperar(medium.getId());
        assertNotNull(mediumRecuperado);
        assertEquals(medium.getId(), mediumRecuperado.get().getId());
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
        assertEquals(2, mediums.size());
        List<String> nombres = mediums.stream().map(Medium::getNombre).toList();
        assertEquals(List.of( "Lala","Lizzie"), nombres);
    }

    @Test
    void recuperarTodosSinMediums(){
        mediumService.eliminar(medium);
        mediumService.eliminar(medium2);
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
        espirituRecu = espirituService.recuperar(espiritu.getId());
        espirituRecu.setNivelConexion(80);
        espirituService.actualizar(espirituRecu);

        espirituService.conectar(espirituRecu.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espirituRecu.getId());

        assertEquals(1, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertTrue(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
    }

    @Test
    void exorcizarMedium1a1VictoriosoTeniendoDemoniacosYangelicalesDeMas(){
        dado.setModo(new ModoTrucado(6,60));
        espirituRecu.setNivelConexion(80);
        espirituService.actualizar(espirituRecu);

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

        espirituService.conectar(espirituRecu.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());
        espirituService.conectar(azael.getId(), medium.getId());
        espirituService.conectar(castiel.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espirituRecu.getId());
        Espiritu azaelAct = espirituService.recuperar(azael.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());

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

        espirituRecu.setNivelConexion(40);
        espirituService.actualizar(espirituRecu);

        espirituService.conectar(espirituRecu.getId(), medium.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espirituRecu.getId());

        assertEquals(1, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
    }

    @Test
    void exorcizarMedium1a1DerrotaConDesconexion(){

        dado.setModo(new ModoTrucado(6,60));

        espirituService.conectar(espiritu.getId(), mediumRecu.get().getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(mediumRecu.get().getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.get().getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());

        assertEquals(0, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.estaLibre());
        assertTrue(espirituAct.estaLibre());
    }

    @Test
    void exorcizarMedium2a1Victoria(){
        dado.setModo(new ModoTrucado(5,10));
        espirituRecu.setNivelConexion(30);
        espirituService.actualizar(espirituRecu);
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(50);
        espirituService.crear(castiel);

        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);

        espirituService.conectar(espirituRecu.getId(), mediumRecu.get().getId());
        espirituService.conectar(castiel.getId(), mediumRecu.get().getId());
        espirituService.conectar(espiritu2.getId(), mediumRecu2.get().getId());

        mediumService.exorcizar(mediumRecu.get().getId(), mediumRecu2.get().getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.get().getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.get().getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espirituRecu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());

        assertEquals(2, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertTrue(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
        assertFalse(castielAct.estaLibre());
    }

    @Test
    void exorcizarMedium2a1AmbasDerrotasSinDesconexion(){
        dado.setModo(new ModoTrucado(5,90));
        espirituRecu.setNivelConexion(30);
        espirituService.actualizar(espirituRecu);
        Espiritu kyu = new Angel("Kyu");
        kyu.setNivelConexion(30);
        espirituService.crear(kyu);
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(50);
        espirituService.crear(castiel);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);


        espirituService.conectar(espirituRecu.getId(), mediumRecu.get().getId());
        espirituService.conectar(castiel.getId(), mediumRecu.get().getId());
        espirituService.conectar(espiritu2.getId(), mediumRecu2.get().getId());

        mediumService.exorcizar(mediumRecu.get().getId(), mediumRecu2.get().getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.get().getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.get().getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espirituRecu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());

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

        espirituService.conectar(espiritu.getId(), mediumRecu.get().getId());
        espirituService.conectar(castiel.getId(), mediumRecu.get().getId());
        espirituService.conectar(espiritu2.getId(), mediumRecu2.get().getId());

        mediumService.exorcizar(mediumRecu.get().getId(), mediumRecu2.get().getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.get().getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.get().getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
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
        espirituRecu.setNivelConexion(10);
        espirituService.actualizar(espirituRecu);
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(40);
        espirituService.crear(castiel);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);

        espirituService.conectar(espirituRecu.getId(), mediumRecu.get().getId());
        espirituService.conectar(castiel.getId(), mediumRecu.get().getId());
        espirituService.conectar(espiritu2.getId(), mediumRecu2.get().getId());

        mediumService.exorcizar(mediumRecu.get().getId(), mediumRecu2.get().getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.get().getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.get().getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espirituRecu.getId());
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

        espirituService.conectar(espiritu.getId(), mediumRecu.get().getId());
        espirituService.conectar(castiel.getId(), mediumRecu.get().getId());
        espirituService.conectar(espiritu2.getId(), mediumRecu2.get().getId());

        mediumService.exorcizar(mediumRecu.get().getId(), mediumRecu2.get().getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.get().getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.get().getId());

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
        espirituRecu2.setNivelConexion(20);
        espirituService.actualizar(espirituRecu2);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);

        espirituService.conectar(espiritu.getId(), mediumRecu.get().getId());
        espirituService.conectar(castiel.getId(), mediumRecu.get().getId());
        espirituService.conectar(espirituRecu2.getId(), mediumRecu2.get().getId());

        mediumService.exorcizar(mediumRecu.get().getId(), mediumRecu2.get().getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.get().getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.get().getId());

        Espiritu espiritu2Act = espirituService.recuperar(espirituRecu2.getId());
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
        espirituRecu.setNivelConexion(50);
        espirituService.actualizar(espirituRecu);
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(70);
        espirituService.crear(castiel);
        espirituRecu2.setNivelConexion(25);
        espirituService.actualizar(espirituRecu2);
        Espiritu azael = new Demonio("azael");
        azael.setNivelConexion(35);
        espirituService.crear(azael);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);
        azael.setUbicacion(bernal);
        espirituService.actualizar(azael);

        espirituService.conectar(espiritu.getId(), mediumRecu.get().getId());
        espirituService.conectar(castiel.getId(), mediumRecu.get().getId());
        espirituService.conectar(espirituRecu2.getId(), mediumRecu2.get().getId());
        espirituService.conectar(azael.getId(), mediumRecu2.get().getId());

        mediumService.exorcizar(mediumRecu.get().getId(), mediumRecu2.get().getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.get().getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.get().getId());

        Espiritu espiritu2Act = espirituService.recuperar(espirituRecu2.getId());
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
        espirituRecu2.setNivelConexion(25);
        espirituService.actualizar(espirituRecu2);
        Espiritu azael = new Demonio("azael");
        azael.setNivelConexion(35);
        espirituService.crear(azael);

        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);
        azael.setUbicacion(bernal);
        espirituService.actualizar(azael);

        espirituService.conectar(espiritu.getId(), mediumRecu.get().getId());
        espirituService.conectar(castiel.getId(), mediumRecu.get().getId());
        espirituService.conectar(espirituRecu2.getId(), mediumRecu2.get().getId());
        espirituService.conectar(azael.getId(), mediumRecu2.get().getId());

        mediumService.exorcizar(mediumRecu.get().getId(), mediumRecu2.get().getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.get().getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.get().getId());

        Espiritu espiritu2Act = espirituService.recuperar(espirituRecu2.getId());
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
        espirituRecu.setNivelConexion(70);
        espirituService.actualizar(espirituRecu);
        espirituRecu2.setNivelConexion(25);
        espirituService.actualizar(espirituRecu2);
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

        espirituService.conectar(espirituRecu.getId(), mediumRecu.get().getId());
        espirituService.conectar(castiel.getId(), mediumRecu.get().getId());
        espirituService.conectar(espirituRecu2.getId(), mediumRecu2.get().getId());
        espirituService.conectar(azael.getId(), mediumRecu2.get().getId());

        mediumService.exorcizar(mediumRecu.get().getId(), mediumRecu2.get().getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.get().getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.get().getId());

        Espiritu espiritu2Act = espirituService.recuperar(espirituRecu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espirituRecu.getId());
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

        espirituService.conectar(rika.getId(), mediumRecu.get().getId());
        espirituService.conectar(ivaar.getId(), mediumRecu.get().getId());
        espirituService.conectar(hana.getId(), mediumRecu.get().getId());
        espirituService.conectar(jeager.getId(), mediumRecu2.get().getId());
        espirituService.conectar(noroi.getId(), mediumRecu2.get().getId());

        mediumService.exorcizar(mediumRecu.get().getId(), mediumRecu2.get().getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.get().getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.get().getId());

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
        Cementerio cementerio = new Cementerio("cementerio", 50);
        ubicacionService.crear(cementerio);
        espiritu2.setNivelConexion(10);
        medium.setUbicacion(cementerio);
        medium.setMana(50);
        mediumService.actualizar(medium);
        espirituService.actualizar(espiritu2);
        mediumService.invocar(medium.getId(), espiritu2.getId());
        espirituService.conectar(espiritu2.getId(), medium.getId());
        mediumService.descansar(medium.getId());
        Optional<Medium> descansadoMedium = mediumService.recuperar(medium.getId());
        Espiritu descansadoEspiritu = espirituService.recuperar(espiritu2.getId());

        assertEquals(medium.getId(), descansadoMedium.get().getId());
        assertEquals(1, descansadoMedium.get().getEspiritus().size());
        assertEquals(descansadoEspiritu.getMedium().getId(), descansadoMedium.get().getId());
        assertEquals(65, descansadoMedium.get().getMana());
        assertEquals(68, descansadoEspiritu.getNivelConexion());
    }

    //    Santuario santuario = new Santuario("santuario", 50);
//        Casper.setNivelConexion(10);
//        medium.setUbicacion(santuario);
//        medium.invocar(Casper);
//        medium.conectarseAEspiritu(Casper);
//        medium.descansar();
//    assertEquals(1, medium.getEspiritus().size());
//    assertTrue(medium.getEspiritus().contains(Casper));
//    assertEquals(medium,  Casper.getMedium());
//    //assertEquals(90, medium.getMana());
//    assertEquals(68, Casper.getNivelConexion());

    @Test
    void descansarMediumConAngelEnSantuario(){
        Santuario santuario = new Santuario("santuario", 30);
        ubicacionService.crear(santuario);
        espiritu.setNivelConexion(10);
        medium.setUbicacion(santuario);
        medium.setMana(50);
        mediumService.actualizar(medium);
        espirituService.actualizar(espiritu);
        mediumService.invocar(medium.getId(), espiritu.getId());
        espirituService.conectar(espiritu.getId(), medium.getId());
        mediumService.descansar(medium.getId());
        Optional<Medium> descansadoMedium = mediumService.recuperar(medium.getId());
        Espiritu descansadoEspiritu = espirituService.recuperar(espiritu.getId());

        assertEquals(medium.getId(), descansadoMedium.get().getId());
        assertEquals(1, descansadoMedium.get().getEspiritus().size());
        assertEquals(descansadoEspiritu.getMedium().getId(), descansadoMedium.get().getId());
        assertEquals(85, descansadoMedium.get().getMana());
        assertEquals(48, descansadoEspiritu.getNivelConexion());
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
        mediumRecu2.get().setMana(100);
        mediumService.actualizar(mediumRecu2.orElse(null));

        Ubicacion quilmes = new Cementerio("Quilmes", 100);
        ubicacionService.crear(quilmes);
        espiritu2.setUbicacion(quilmes);

        espirituService.actualizar(espiritu2);
        Espiritu espirituAntes = espirituService.recuperar(espiritu2.getId());

        Optional<Espiritu> espirituInvocado = mediumService.invocar(mediumRecu2.get().getId(), espiritu2.getId());

        assertTrue(espirituInvocado.isPresent());
        assertNotEquals(espirituInvocado.get().getUbicacion(), espirituAntes.getMedium());
        assertEquals(espirituInvocado.get().getUbicacion(), mediumRecu2.get().getUbicacion());
        System.out.println(espirituAntes.getUbicacion().getId());
        System.out.println(espirituInvocado.get().getUbicacion().getId());
        assertNotEquals(espirituInvocado.get().getUbicacion().getId(), espirituAntes.getUbicacion().getId());
    }

    @Test
    void invocarEspirituLibreEnMismaUbicacion() {
        mediumRecu2.get().setMana(100);
        mediumService.actualizar(mediumRecu2.orElse(null));

        Optional<Espiritu> espirituInvocado = mediumService.invocar(mediumRecu2.get().getId(), espiritu2.getId());
        assertEquals(espirituInvocado.get().getUbicacion(), espirituRecu2.getUbicacion());
    }

    @Test
    void invocarDemonioLibreEnCementerio() {
        Cementerio cementerio = new Cementerio("cementerio", 100);
        ubicacionService.crear(cementerio);
        mediumRecu2.get().setMana(100);
        mediumRecu2.get().setUbicacion(cementerio);
        mediumService.actualizar(mediumRecu2.orElse(null));
        espiritu2.setNivelConexion(10);
        espiritu2.setUbicacion(bernal);
        espirituService.actualizar(espiritu2);
        assertNotEquals(mediumRecu2.get().getUbicacion().getId(), espiritu2.getUbicacion().getId());
        Optional<Espiritu> espirituInvocado = mediumService.invocar(mediumRecu2.get().getId(), espiritu2.getId());
        assertEquals(mediumRecu2.get().getUbicacion().getId(), espirituInvocado.get().getUbicacion().getId());
    }

    @Test
    void invocacacionFallidaAngelEnCementerio() {
        Cementerio cementerio = new Cementerio("cementerio", 100);
        ubicacionService.crear(cementerio);
        mediumRecu2.get().setMana(100);
        mediumRecu2.get().setUbicacion(cementerio);
        mediumService.actualizar(mediumRecu2.orElse(null));
        espiritu.setNivelConexion(10);
        espiritu.setUbicacion(bernal);
        espirituService.actualizar(espiritu);
        assertNotEquals(mediumRecu2.get().getUbicacion().getId(), espiritu.getUbicacion().getId());
        assertThrows(InvocacionFallidaPorUbicacionException.class, () -> {
            mediumService.invocar(mediumRecu2.get().getId(), espiritu.getId());
        });
    }

    @Test
    void invocarAngelLibreEnSantuario() {
        Santuario santuario = new Santuario("santuario", 100);
        ubicacionService.crear(santuario);
        mediumRecu2.get().setMana(100);
        mediumRecu2.get().setUbicacion(santuario);
        mediumService.actualizar(mediumRecu2.orElse(null));
        espiritu.setNivelConexion(10);
        espiritu.setUbicacion(bernal);
        espirituService.actualizar(espiritu);
        assertNotEquals(mediumRecu2.get().getUbicacion().getId(), espiritu.getUbicacion().getId());
        Optional<Espiritu> espirituInvocado = mediumService.invocar(mediumRecu2.get().getId(), espiritu.getId());
        assertEquals(mediumRecu2.get().getUbicacion().getId(), espirituInvocado.get().getUbicacion().getId());
    }

    @Test
    void invocacacionFallidaDemonioEnSantuario() {
        Cementerio cementerio = new Cementerio("cementerio", 100);
        ubicacionService.crear(cementerio);
        mediumRecu2.get().setMana(100);
        mediumRecu2.get().setUbicacion(cementerio);
        mediumService.actualizar(mediumRecu2.orElse(null));
        espiritu2.setNivelConexion(10);
        espiritu2.setUbicacion(bernal);
        espirituService.actualizar(espiritu2);
        assertNotEquals(mediumRecu2.get().getUbicacion().getId(), espiritu2.getUbicacion().getId());
        assertThrows(InvocacionFallidaPorUbicacionException.class, () -> {
            mediumService.invocar(mediumRecu2.get().getId(), espiritu.getId());
        });
    }

    @Test
    void invocarEspirituNoLibre() {
        mediumRecu.get().setMana(100);
        mediumService.actualizar(mediumRecu.orElse(null));
        mediumRecu2.get().setMana(100);
        mediumService.actualizar(mediumRecu2.orElse(null));

        mediumService.invocar(mediumRecu2.get().getId(), espiritu2.getId());
        espirituService.conectar(espiritu2.getId(), mediumRecu.get().getId());
        assertThrows(EspirituNoLibreException.class, () -> mediumService.invocar(mediumRecu.get().getId(), espiritu2.getId()));
    }

    @Test
    void invocarEspirituSinMana() {
        Medium mediumSinMana = new Medium("Nomana", 100, 0);
        Espiritu espirituAntes = espirituService.recuperar(espiritu.getId());
        mediumService.crear(mediumSinMana);
        mediumSinMana.setUbicacion(bernal);
        mediumService.actualizar(mediumSinMana);
        //mediumService.mover(mediumSinMana.getId(),bernal.getId());
        Optional<Espiritu> espirituNoInvocado = mediumService.invocar(mediumSinMana.getId(), espiritu.getId());
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
        mediumRecu.get().setMana(100);
        mediumService.actualizar(mediumRecu.orElse(null));
        assertThrows(InvalidDataAccessApiUsageException.class,()->{
            mediumService.invocar(mediumRecu.get().getId(), null);
        });
    }

    @Test
    void invocarEspirituConIdEspirituInvalido() {
        mediumRecu.get().setMana(100);
        mediumService.actualizar(mediumRecu.orElse(null));
        assertThrows(IdNoValidoException.class,()->{
            mediumService.invocar(mediumRecu.get().getId(), 2025L);
        });
    }

    @Test
    void espiritusDeMedium(){
        //Utilizo conectar pero solo para agregar espiritus al medium.
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
        assertNotEquals(demonioRecuperado.getNivelConexion(), espiritu2.getNivelConexion());
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
        assertNotEquals(angelRecuperado.getNivelConexion(), espiritu.getNivelConexion());
    }
/*
    @Test
    void movimientoDeMediumAMismaUbicacion() {
        assertThrows(MovimientoInvalidoException.class, () -> {
            mediumService.mover(medium2.getId(), bernal.getId());
        });
    }
*/
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


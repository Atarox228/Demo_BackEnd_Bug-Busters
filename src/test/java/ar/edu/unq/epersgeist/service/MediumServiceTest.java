package ar.edu.unq.epersgeist.service;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateUbicacionDAO;
import ar.edu.unq.epersgeist.servicios.DataService;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.impl.*;
import jakarta.persistence.OptimisticLockException;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.modelo.exception.EspirituNoLibreException;
import ar.edu.unq.epersgeist.modelo.exception.NoHayAngelesException;
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

    private DataService dataService;
    private MediumService mediumService;
    private UbicacionService ubicacionService;
    private EspirituService espirituService;
    private Medium medium;
    private Medium medium2;
    private Medium mediumRecu;
    private Medium mediumRecu2;
    private GeneradorNumeros dado;
    private Espiritu espiritu;
    private Espiritu espiritu2;
    private Espiritu espirituRecu;
    private Espiritu espirituRecu2;
    private Ubicacion bernal;



    @BeforeEach
    void setUp() {
        dataService = new DataServiceImpl(new HibernateEspirituDAO(), new HibernateMediumDAO(), new HibernateUbicacionDAO());
        espirituService = new EspirituServiceImpl(new HibernateEspirituDAO(), new HibernateMediumDAO(),new HibernateUbicacionDAO());
        ubicacionService  = new UbicacionServiceImpl(new HibernateUbicacionDAO(),new HibernateMediumDAO(), new HibernateEspirituDAO());
        mediumService = new MediumServiceImpl(new HibernateMediumDAO(), new HibernateEspirituDAO());
        bernal = new Ubicacion("Bernal");
        ubicacionService.crear(bernal);
        medium = new Medium("Lizzie",150,100);
        mediumService.crear(medium);
        medium2 = new Medium("Lala", 100, 50);
        mediumService.crear(medium2);
        espiritu = new Angel("Casper");
        espiritu.setNivelConexion(5);
        espirituService.crear(espiritu);
        espiritu2 = new Demonio("Ghosty");
        espiritu2.setNivelConexion(40);
        espirituService.crear(espiritu2);

        espiritu.setUbicacion(bernal);
        espirituService.actualizar(espiritu);
        espiritu2.setUbicacion(bernal);
        espirituService.actualizar(espiritu2);
        espirituRecu = espirituService.recuperar(espiritu.getId());
        espirituRecu2 = espirituService.recuperar(espiritu2.getId());
        medium.setUbicacion(bernal);
        medium2.setUbicacion(bernal);
        mediumService.actualizar(medium);
        mediumService.actualizar(medium2);
//        mediumService.mover(medium.getId(),bernal.getId());
//        mediumService.mover(medium2.getId(),bernal.getId());
        mediumRecu = mediumService.recuperar(medium.getId());
        mediumRecu.setMana(0);
        mediumService.actualizar(mediumRecu);
        mediumRecu2 = mediumService.recuperar(medium2.getId());
        mediumRecu2.setMana(0);
        mediumService.actualizar(mediumRecu2);
        this.dado = Dado.getInstance();
    }

    @Test
    void crearMedium() {
        assertNotNull(medium.getId());
    }

    @Test
    void recuperarMedium() {
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
        assertNotNull(mediumService.recuperar(mediumId));
        mediumService.eliminar(medium);
        assertNull(mediumService.recuperar(mediumId));
    }

    @Test
    void eliminarMediumYaEliminado(){
        mediumService.eliminar(medium);
        assertThrows(OptimisticLockException.class, () -> {
            mediumService.eliminar(medium);
        });
    }

    @Test
    void eliminarTodosLosMediums() {
        Long mediumId = medium.getId();
        Long mediumId2 = medium2.getId();
        assertNotNull(mediumService.recuperar(mediumId));
        assertNotNull(mediumService.recuperar(mediumId2));
        dataService.eliminarTodo();
        assertNull(mediumService.recuperar(mediumId));
        assertNull(mediumService.recuperar(mediumId2));
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
        assertThrows(OptimisticLockException.class, () -> {
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

        espirituService.conectar(espiritu.getId(), mediumRecu.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(mediumRecu.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.getId());
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

        espirituService.conectar(espirituRecu.getId(), mediumRecu.getId());
        espirituService.conectar(castiel.getId(), mediumRecu.getId());
        espirituService.conectar(espiritu2.getId(), mediumRecu2.getId());

        mediumService.exorcizar(mediumRecu.getId(), mediumRecu2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.getId());

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


        espirituService.conectar(espirituRecu.getId(), mediumRecu.getId());
        espirituService.conectar(castiel.getId(), mediumRecu.getId());
        espirituService.conectar(espiritu2.getId(), mediumRecu2.getId());

        mediumService.exorcizar(mediumRecu.getId(), mediumRecu2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.getId());

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

        espirituService.conectar(espiritu.getId(), mediumRecu.getId());
        espirituService.conectar(castiel.getId(), mediumRecu.getId());
        espirituService.conectar(espiritu2.getId(), mediumRecu2.getId());

        mediumService.exorcizar(mediumRecu.getId(), mediumRecu2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.getId());

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

        espirituService.conectar(espirituRecu.getId(), mediumRecu.getId());
        espirituService.conectar(castiel.getId(), mediumRecu.getId());
        espirituService.conectar(espiritu2.getId(), mediumRecu2.getId());

        mediumService.exorcizar(mediumRecu.getId(), mediumRecu2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.getId());

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

        espirituService.conectar(espiritu.getId(), mediumRecu.getId());
        espirituService.conectar(castiel.getId(), mediumRecu.getId());
        espirituService.conectar(espiritu2.getId(), mediumRecu2.getId());

        mediumService.exorcizar(mediumRecu.getId(), mediumRecu2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.getId());

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
    void exorcizarMedium2a1UnaDerrotaYUnaVictoriaConDesconexionDeAmbosLados(){
        dado.setModo(new ModoTrucado(5,40));
        Espiritu castiel = new Angel("castiel");
        castiel.setNivelConexion(40);
        espirituService.crear(castiel);
        espirituRecu2.setNivelConexion(20);
        espirituService.actualizar(espirituRecu2);
        castiel.setUbicacion(bernal);
        espirituService.actualizar(castiel);

        espirituService.conectar(espiritu.getId(), mediumRecu.getId());
        espirituService.conectar(castiel.getId(), mediumRecu.getId());
        espirituService.conectar(espirituRecu2.getId(), mediumRecu2.getId());

        mediumService.exorcizar(mediumRecu.getId(), mediumRecu2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.getId());

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

        espirituService.conectar(espiritu.getId(), mediumRecu.getId());
        espirituService.conectar(castiel.getId(), mediumRecu.getId());
        espirituService.conectar(espirituRecu2.getId(), mediumRecu2.getId());
        espirituService.conectar(azael.getId(), mediumRecu2.getId());

        mediumService.exorcizar(mediumRecu.getId(), mediumRecu2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.getId());

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

        espirituService.conectar(espiritu.getId(), mediumRecu.getId());
        espirituService.conectar(castiel.getId(), mediumRecu.getId());
        espirituService.conectar(espirituRecu2.getId(), mediumRecu2.getId());
        espirituService.conectar(azael.getId(), mediumRecu2.getId());

        mediumService.exorcizar(mediumRecu.getId(), mediumRecu2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.getId());

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

        espirituService.conectar(espirituRecu.getId(), mediumRecu.getId());
        espirituService.conectar(castiel.getId(), mediumRecu.getId());
        espirituService.conectar(espirituRecu2.getId(), mediumRecu2.getId());
        espirituService.conectar(azael.getId(), mediumRecu2.getId());

        mediumService.exorcizar(mediumRecu.getId(), mediumRecu2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.getId());

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

        espirituService.conectar(rika.getId(), mediumRecu.getId());
        espirituService.conectar(ivaar.getId(), mediumRecu.getId());
        espirituService.conectar(hana.getId(), mediumRecu.getId());
        espirituService.conectar(jeager.getId(), mediumRecu2.getId());
        espirituService.conectar(noroi.getId(), mediumRecu2.getId());

        mediumService.exorcizar(mediumRecu.getId(), mediumRecu2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumRecu.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumRecu2.getId());

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
        mediumRecu2.setMana(100);
        mediumService.actualizar(mediumRecu2);
        Ubicacion quilmes = new Ubicacion(("Quilmes"));
        ubicacionService.crear(quilmes);
        espiritu2.setUbicacion(quilmes);
        espirituService.actualizar(espiritu2);
        Espiritu espirituAntes = espirituService.recuperar(espiritu2.getId());

        Espiritu espirituInvocado = mediumService.invocar(mediumRecu2.getId(), espiritu2.getId());
        assertNotEquals(espirituInvocado.getMedium(), espirituAntes.getMedium());
        assertNotEquals(espirituInvocado.getUbicacion(), espirituAntes.getUbicacion());
    }

    @Test
    void invocarEspirituLibreEnMismaUbicacion() {
        mediumRecu2.setMana(100);
        mediumService.actualizar(mediumRecu2);

        Espiritu espirituInvocado = mediumService.invocar(mediumRecu2.getId(), espiritu2.getId());
        assertNotEquals(espirituInvocado.getMedium(), espirituRecu2.getMedium());
        assertEquals(espirituInvocado.getUbicacion(), espirituRecu2.getUbicacion());
    }

    @Test
    void invocarEspirituNoLibre() {
        mediumRecu.setMana(100);
        mediumService.actualizar(mediumRecu);
        mediumRecu2.setMana(100);
        mediumService.actualizar(mediumRecu2);

        mediumService.invocar(mediumRecu2.getId(), espiritu.getId());
        assertThrows(EspirituNoLibreException.class, () -> mediumService.invocar(mediumRecu.getId(), espiritu.getId()));
    }

    @Test
    void invocarEspirituSinMana() {
        Medium mediumSinMana = new Medium("Nomana", 100, 0);
        Espiritu espirituAntes = espirituService.recuperar(espiritu.getId());
        mediumService.crear(mediumSinMana);
        mediumSinMana.setUbicacion(bernal);
        mediumService.actualizar(mediumSinMana);
//        mediumService.mover(mediumSinMana.getId(),bernal.getId());
        Espiritu espirituNoInvocado = mediumService.invocar(mediumSinMana.getId(), espiritu.getId());
        assertEquals(espirituNoInvocado.getMedium(), espirituAntes.getMedium());
        assertEquals(espirituNoInvocado.getUbicacion(), espirituAntes.getUbicacion());
    }

    @Test
    void invocarEspirituConIdMediumNull() {
        assertThrows(IdNoValidoException.class,()->{
            mediumService.invocar(null, espiritu2.getId());
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
        mediumRecu.setMana(100);
        mediumService.actualizar(mediumRecu);
        assertThrows(IdNoValidoException.class,()->{
            mediumService.invocar(mediumRecu.getId(), null);
        });
    }

    @Test
    void invocarEspirituConIdEspirituInvalido() {
        mediumRecu.setMana(100);
        mediumService.actualizar(mediumRecu);
        assertThrows(IdNoValidoException.class,()->{
            mediumService.invocar(mediumRecu.getId(), 2025L);
        });
    }

    @Test
    void espiritusDeMedium(){
        // Utilizo conectar pero solo para agregar espiritus al medium.
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
        assertThrows(IdNoValidoException.class,()->{
            mediumService.espiritus(null);
        });
    }

    @AfterEach
    void cleanUp() {
        dataService.eliminarTodo();

        dado.setModo(new ModoRandom());

    }
}


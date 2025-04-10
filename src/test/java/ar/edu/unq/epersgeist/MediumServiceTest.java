package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateUbicacionDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.impl.*;
import jakarta.persistence.OptimisticLockException;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.persistencia.dao.exception.EspirituNoLibreException;
import ar.edu.unq.epersgeist.persistencia.dao.exception.NoHayAngelesException;
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

    private MediumService mediumService;
    private UbicacionService ubicacionService;
    private EspirituService espirituService;
    private Medium medium;
    private Medium medium2;
    private GeneradorNumeros dado;
    private Espiritu espiritu;
    private Espiritu espiritu2;
    private Ubicacion bernal;


    @BeforeEach
    void setUp() {
        espirituService = new EspirituServiceImpl(new HibernateEspirituDAO(), new HibernateMediumDAO(),new HibernateUbicacionDAO());
        ubicacionService  = new UbicacionServiceImpl(new HibernateUbicacionDAO(),new HibernateMediumDAO(), new HibernateEspirituDAO());
        mediumService = new MediumServiceImpl(new HibernateMediumDAO(), new HibernateEspirituDAO(), new HibernateUbicacionDAO());
        bernal = new Ubicacion("Bernal");
        ubicacionService.crear(bernal);
        medium = new Medium("Lizzie",150,100);
        mediumService.crear(medium);
        medium2 = new Medium("Lala", 100, 50);
        mediumService.crear(medium2);
        espiritu = new Espiritu(TipoEspiritu.ANGELICAL, 5, "Casper");
        espirituService.crear(espiritu);
        espiritu2 = new Espiritu(TipoEspiritu.DEMONIACO, 40, "Ghosty");
        espirituService.crear(espiritu2);

        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        espirituService.ubicarseEn(espiritu2.getId(),bernal.getId());

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

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
        mediumService.eliminarTodo();
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
    void descansarMedium(){
        Medium sinDescansar = mediumService.recuperar(medium.getId());
        mediumService.descansar(medium.getId());
        Medium descansado = mediumService.recuperar(medium.getId());
        assertEquals(sinDescansar.getId(), descansado.getId());
        assertNotEquals(sinDescansar.getMana(), descansado.getMana());
    }

    @Test
    void testExorcizarMedium1a1Victorioso(){
        dado.setModo(new ModoTrucado(6,60));
        espiritu.setNivelConexion(80);
        espirituService.actualizar(espiritu);

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());


        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());

        assertEquals(1, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertTrue(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());

    }

    @Test
    void testExorcizarMedium1a1VictoriosoteniendoDemoniacosYangelicalesDeMas(){
        dado.setModo(new ModoTrucado(6,60));
        espiritu.setNivelConexion(80);
        espirituService.actualizar(espiritu);

        Espiritu azael = new Espiritu(TipoEspiritu.DEMONIACO, 40,"Azael");
        espirituService.crear(azael);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 40,"Castiel");
        espirituService.crear(castiel);

        espirituService.ubicarseEn(azael.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());
        mediumConectado = espirituService.conectar(azael.getId(), medium.getId());
        mediumConectado2 = espirituService.conectar(castiel.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());
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
    void testExorcizarMedium1a1DerrotaSinDesconexion(){
        dado.setModo(new ModoTrucado(6,60));
        espiritu.setNivelConexion(40);
        espirituService.actualizar(espiritu);

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());

        assertEquals(1, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
    }

    @Test
    void testExorcizarMedium1a1DerrotaConDesconexion(){
        medium.setMana(0);
        mediumService.actualizar(medium);

        dado.setModo(new ModoTrucado(6,60));

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());

        assertEquals(0, espiritusMedium1.size());
        assertEquals(1, espiritusMedium2.size());
        assertFalse(espiritu2Act.estaLibre());
        assertTrue(espirituAct.estaLibre());
    }

    @Test
    void testExorcizarMedium2a1Victoria(){
        medium.setMana(0);
        medium2.setMana(0);
        mediumService.actualizar(medium);
        mediumService.actualizar(medium2);

        dado.setModo(new ModoTrucado(5,10));
        espiritu.setNivelConexion(30);
        espirituService.actualizar(espiritu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 50,"castiel");
        espirituService.crear(castiel);

        espirituService.ubicarseEn(castiel.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        Espiritu espiritu2Act = espirituService.recuperar(espiritu2.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());

        assertEquals(2, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertTrue(espiritu2Act.estaLibre());
        assertFalse(espirituAct.estaLibre());
        assertFalse(castielAct.estaLibre());
    }

    @Test
    void testExorcizarMedium2a1AmbasDerrotasSinDesconexion(){
        medium.setMana(0);
        medium2.setMana(0);
        mediumService.actualizar(medium);
        mediumService.actualizar(medium2);

        dado.setModo(new ModoTrucado(5,90));
        espiritu.setNivelConexion(30);
        espirituService.actualizar(espiritu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 50,"castiel");
        espirituService.crear(castiel);

        espirituService.ubicarseEn(castiel.getId(),bernal.getId());


        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

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
    void testExorcizarMedium2a1AmbasDerrotasConDesconexion(){
        medium.setMana(0);
        medium2.setMana(0);
        mediumService.actualizar(medium);
        mediumService.actualizar(medium2);

        dado.setModo(new ModoTrucado(5,90));
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 5,"castiel");
        espirituService.crear(castiel);


        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(espiritu2.getId(),bernal.getId());

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

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
    void testExorcizarMedium2a1UnaDerrotaYUnaVictoriaSinDesconexion(){
        medium.setMana(0);
        medium2.setMana(0);
        mediumService.actualizar(medium);
        mediumService.actualizar(medium2);

        dado.setModo(new ModoTrucado(5,40));
        espiritu.setNivelConexion(10);
        espirituService.actualizar(espiritu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 40,"castiel");
        espirituService.crear(castiel);


        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(espiritu2.getId(),bernal.getId());

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

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
    void testExorcizarMedium2a1UnaDerrotaYUnaVictoriaConDesconexion(){
        medium.setMana(0);
        medium2.setMana(0);
        mediumService.actualizar(medium);
        mediumService.actualizar(medium2);

        dado.setModo(new ModoTrucado(5,40));
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 40,"castiel");
        espirituService.crear(castiel);


        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(espiritu2.getId(),bernal.getId());

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

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
    void testExorcizarMedium2a1UnaDerrotaYUnaVictoriaConDesconexionDeAmbosLados(){
        medium.setMana(0);
        medium2.setMana(0);
        mediumService.actualizar(medium);
        mediumService.actualizar(medium2);

        dado.setModo(new ModoTrucado(5,40));
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 40,"castiel");
        espirituService.crear(castiel);
        espiritu2.setNivelConexion(20);
        espirituService.actualizar(espiritu2);

        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(espiritu2.getId(),bernal.getId());

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

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
    void testExorcizarMedium2a2VictoriaAbsoluta(){
        medium.setMana(0);
        medium2.setMana(0);
        mediumService.actualizar(medium);
        mediumService.actualizar(medium2);

        dado.setModo(new ModoTrucado(5,40));
        espiritu.setNivelConexion(50);
        espirituService.actualizar(espiritu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 70,"castiel");
        espirituService.crear(castiel);
        espiritu2.setNivelConexion(25);
        espirituService.actualizar(espiritu2);
        Espiritu azael = new Espiritu(TipoEspiritu.DEMONIACO, 35,"azael");
        espirituService.crear(azael);

        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(espiritu2.getId(),bernal.getId());
        espirituService.ubicarseEn(azael.getId(),bernal.getId());

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());
        mediumConectado2 = espirituService.conectar(azael.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

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
    void testExorcizarMedium2a2DerrotaAbsolutaSinYConDesconexion(){
        medium.setMana(0);
        medium2.setMana(0);
        mediumService.actualizar(medium);
        mediumService.actualizar(medium2);

        dado.setModo(new ModoTrucado(5,70));
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 10,"castiel");
        espirituService.crear(castiel);
        espiritu2.setNivelConexion(25);
        espirituService.actualizar(espiritu2);
        Espiritu azael = new Espiritu(TipoEspiritu.DEMONIACO, 35,"azael");
        espirituService.crear(azael);

        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(espiritu2.getId(),bernal.getId());
        espirituService.ubicarseEn(azael.getId(),bernal.getId());

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());
        mediumConectado2 = espirituService.conectar(azael.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

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
    void testExorcizarMedium2a2UnaVictoriaYUnaDerrotaConDesconexionDeAmbosLados(){
        medium.setMana(0);
        medium2.setMana(0);
        mediumService.actualizar(medium);
        mediumService.actualizar(medium2);

        dado.setModo(new ModoTrucado(5,70));
        espiritu.setNivelConexion(70);
        espirituService.actualizar(espiritu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 5,"castiel");
        espirituService.crear(castiel);
        espiritu2.setNivelConexion(25);
        espirituService.actualizar(espiritu2);
        Espiritu azael = new Espiritu(TipoEspiritu.DEMONIACO, 35,"azael");
        espirituService.crear(azael);

        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(espiritu2.getId(),bernal.getId());
        espirituService.ubicarseEn(azael.getId(),bernal.getId());

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(espiritu2.getId(), medium2.getId());
        mediumConectado2 = espirituService.conectar(azael.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

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
    void TestEjemplo(){
        medium.setMana(0);
        medium2.setMana(0);
        mediumService.actualizar(medium);
        mediumService.actualizar(medium2);

        dado.setModo(new ModoTrucado(5,60));
        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Espiritu rika = new Espiritu(TipoEspiritu.ANGELICAL, 60,"Rika");
        espirituService.crear(rika);
        Espiritu ivaar = new Espiritu(TipoEspiritu.ANGELICAL, 80,"Ivaar");
        espirituService.crear(ivaar);
        Espiritu hana = new Espiritu(TipoEspiritu.ANGELICAL, 5,"Hana");
        espirituService.crear(hana);
        Espiritu jeager = new Espiritu(TipoEspiritu.DEMONIACO, 50,"Jeager");
        espirituService.crear(jeager);
        Espiritu noroi = new Espiritu(TipoEspiritu.DEMONIACO, 66,"Noroi");
        espirituService.crear(noroi);

        espirituService.ubicarseEn(rika.getId(),bernal.getId());
        espirituService.ubicarseEn(ivaar.getId(),bernal.getId());
        espirituService.ubicarseEn(hana.getId(),bernal.getId());
        espirituService.ubicarseEn(jeager.getId(),bernal.getId());
        espirituService.ubicarseEn(noroi.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(rika.getId(), medium.getId());
        mediumConectado = espirituService.conectar(ivaar.getId(), medium.getId());
        mediumConectado = espirituService.conectar(hana.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(jeager.getId(), medium2.getId());
        mediumConectado2 = espirituService.conectar(noroi.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
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
    void NoHayAngelesException(){
        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        assertThrows(NoHayAngelesException.class,()->{
            mediumService.exorcizar(medium.getId(), medium2.getId());
        });
    }

    @Test
    void NoHayDemonios(){
        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        espiritu.setNivelConexion(70);
        espirituService.actualizar(espiritu);

        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(espiritu.getId(), medium.getId());
        Espiritu espirituAct = espirituService.recuperar(espiritu.getId());

        mediumService.exorcizar(medium.getId(), medium2.getId());

        List<Espiritu> espiritusMedium1 = mediumService.espiritus(medium.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(medium2.getId());

        assertEquals(1, espiritusMedium1.size());
        assertEquals(0, espiritusMedium2.size());
        assertFalse(espirituAct.estaLibre());
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
        Ubicacion quilmes = new Ubicacion(("Quilmes"));
        ubicacionService.crear(quilmes);
        espirituService.ubicarseEn(espiritu2.getId(),quilmes.getId());
        Espiritu espirituAntes = espirituService.recuperar(espiritu2.getId());

        mediumService.ubicarseEn(medium2.getId(),bernal.getId());
        Espiritu espirituInvocado = mediumService.invocar(medium2.getId(), espiritu2.getId());
        assertNotEquals(espirituInvocado.getMedium(), espirituAntes.getMedium());
        assertNotEquals(espirituInvocado.getUbicacion(), espirituAntes.getUbicacion());
    }

    // Testear que pasa si invoco en la misma ubicacion

    @Test
    void invocarEspirituNoLibre() {

        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());

        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        mediumService.invocar(medium2.getId(), espiritu.getId());
        assertThrows(EspirituNoLibreException.class, () -> mediumService.invocar(medium.getId(), espiritu.getId()));
    }

    @Test
    void invocarEspirituSinMana() {
        Medium mediumSinMana = new Medium("Nomana", 100, 0);
        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        Espiritu espirituAntes = espirituService.recuperar(espiritu.getId());
        mediumService.crear(mediumSinMana);
        mediumService.ubicarseEn(mediumSinMana.getId(),bernal.getId());
        Espiritu espirituNoInvocado = mediumService.invocar(mediumSinMana.getId(), espiritu.getId());
        assertEquals(espirituNoInvocado.getMedium(), espirituAntes.getMedium());
        assertEquals(espirituNoInvocado.getUbicacion(), espirituAntes.getUbicacion());
    }

    @Test
    void espiritusDeMedium(){
        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());
        // Utilizo conectar pero solo para agregar espiritus al medium.
        espirituService.conectar(espiritu.getId(), medium2.getId());
        assertEquals(1, (mediumService.espiritus(medium2.getId())).size());
    }

    @Test
    void espiritusDeMediumSinEspiritus(){
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());
        assertEquals(0, (mediumService.espiritus(medium2.getId())).size());
    }

    @Test
    void espiritusDeMediumConVariosEspiritus(){
        espirituService.ubicarseEn(espiritu.getId(),bernal.getId());
        espirituService.ubicarseEn(espiritu2.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());
        espirituService.conectar(espiritu.getId(), medium2.getId());
        espirituService.conectar(espiritu2.getId(), medium2.getId());
        assertEquals(2, (mediumService.espiritus(medium2.getId())).size());
    }

    @AfterEach
    void cleanUp() {
        espirituService.eliminarTodo();
        mediumService.eliminarTodo();

        dado.setModo(new ModoRandom());
        ubicacionService.eliminarTodo();
    }
}


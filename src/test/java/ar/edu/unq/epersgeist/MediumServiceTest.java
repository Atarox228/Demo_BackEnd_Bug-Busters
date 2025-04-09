package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.TipoEspiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.dao.exception.EspirituNoLibreException;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateUbicacionDao;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.exception.ConstraintViolationException;
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
    private Medium medium;
    private Medium medium2;
    private EspirituService espirituService;
    private GeneradorNumeros dado;
    private Espiritu espiritu;
    private Ubicacion bernal;

    private Medium medium3;
    private Medium mediumSinMana;
    private UbicacionService ubicacionService;

    private Ubicacion quilmes;
    private Espiritu espiritu2;
    private Espiritu espiritu3;

    @BeforeEach
    void setUp() {
        MediumDAO mDAO =new HibernateMediumDAO();
        EspirituDAO eDAO = new HibernateEspirituDAO();
        UbicacionDAO uDAO = new HibernateUbicacionDao();
        mediumService = new MediumServiceImpl(mDAO, eDAO, uDAO);
        espirituService = new EspirituServiceImpl(eDAO, mDAO, uDAO);
        ubicacionService = new UbicacionServiceImpl(uDAO,mDAO,eDAO);
        quilmes = new Ubicacion(("Quilmes"));
        bernal = new Ubicacion("Bernal");
        ubicacionService.crear(quilmes);
        medium3 = new Medium("Lala", 100, 50, bernal);
        mediumSinMana = new Medium("Nomana", 100, 0, bernal);
        ubicacionService.crear(bernal);
        this.medium = new Medium("Lizzie",150,100, bernal);
        this.medium2 = new Medium("Lorraine", 200, 50, bernal);

        this.dado = Dado.getInstance();

        espiritu = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Casper", bernal);
        espiritu2 = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Ghosty", quilmes);
        espiritu3 = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Bernalero", bernal);


    }

    @Test
    void guardarMedium() {
        mediumService.guardar(medium);
        assertNotNull(medium.getId());
    }

    @Test
    void recuperarMedium() {
        mediumService.guardar(medium);
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
        mediumService.guardar(medium);
        mediumService.guardar(medium2);
        Collection<Medium> mediums = mediumService.recuperarTodos();
        assertEquals(2, mediums.size());
        List<String> nombres = mediums.stream().map(Medium::getNombre).toList();
        assertEquals(List.of("Lizzie", "Lorraine"), nombres);
    }

    @Test
    void recuperarTodosSinMediums(){
        Collection<Medium> mediums = mediumService.recuperarTodos();
        assertEquals(0, mediums.size());
    }

    @Test
    void eliminarMedium() {
        mediumService.guardar(medium);
        Long mediumId = medium.getId();
        assertNotNull(mediumService.recuperar(mediumId));
        mediumService.eliminar(medium);
        assertNull(mediumService.recuperar(mediumId));
    }

    @Test
    void eliminarMediumYaEliminado(){
        mediumService.guardar(medium);
        mediumService.eliminar(medium);
        assertThrows(OptimisticLockException.class, () -> {
            mediumService.eliminar(medium);
        });
    }

    @Test
    void eliminarTodosLosMediums() {
        mediumService.guardar(medium);
        mediumService.guardar(medium2);
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
        mediumService.guardar(medium);
        Medium sinActualizar = mediumService.recuperar(medium.getId());
        medium.setNombre("Juan");
        System.out.println("El nombre ahora es: " + medium.getNombre());
        mediumService.actualizar(medium);
        Medium actualizado = mediumService.recuperar(medium.getId());
        assertEquals(sinActualizar.getId(), medium.getId());
        assertEquals(sinActualizar.getNombre(), "Lizzie");
        assertEquals(actualizado.getNombre(), "Juan");
    }

    @Test
    void actualizarMediumEliminado(){
        mediumService.guardar(medium);
        mediumService.eliminar(medium);
        assertThrows(OptimisticLockException.class, () -> {
            mediumService.actualizar(medium);
        });
    }

    @Test
    void descansarMedium(){
        mediumService.guardar(medium);
        Medium sinDescansar = mediumService.recuperar(medium.getId());
        mediumService.descansar(medium.getId());
        Medium descansado = mediumService.recuperar(medium.getId());
        assertEquals(sinDescansar.getId(), descansado.getId());
        assertNotEquals(sinDescansar.getMana(), descansado.getMana());
    }

    @Test
    void testExorcizarMedium1a1Victorioso(){

        dado.setModo(new ModoTrucado(6,60));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 80,"Kyu");
        espirituService.crear(kyu);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 40,"kyuMalvado");
        espirituService.crear(kyuMalvado);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());


        assertEquals(espiritusMedium1.size(), 1);
        assertEquals(espiritusMedium2.size(), 0);
        assertTrue(kyuMalvadoAct.estaLibre());
        assertFalse(kyuAct.estaLibre());


    }

    @Test
    void testExorcizarMedium1a1VictoriosoteniendoDemoniacosYangelicalesDeMas(){

        dado.setModo(new ModoTrucado(6,60));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 80,"Kyu");
        espirituService.crear(kyu);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 40,"kyuMalvado");
        espirituService.crear(kyuMalvado);
        Espiritu azael = new Espiritu(TipoEspiritu.DEMONIACO, 40,"Azael");
        espirituService.crear(azael);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 40,"Castiel");
        espirituService.crear(castiel);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());
        espirituService.ubicarseEn(azael.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());
        mediumConectado = espirituService.conectar(azael.getId(), medium.getId());
        mediumConectado2 = espirituService.conectar(castiel.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());
        Espiritu azaelAct = espirituService.recuperar(azael.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());


        assertEquals(espiritusMedium1.size(), 2);
        assertEquals(espiritusMedium2.size(), 1);
        assertTrue(kyuMalvadoAct.estaLibre());
        assertFalse(kyuAct.estaLibre());
        assertFalse(azaelAct.estaLibre());
        assertFalse(castielAct.estaLibre());


    }

    @Test
    void testExorcizarMedium1a1DerrotaSinDesconexion(){

        dado.setModo(new ModoTrucado(6,60));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 40,"Kyu");
        espirituService.crear(kyu);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 40,"kyuMalvado");
        espirituService.crear(kyuMalvado);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());


        assertEquals(espiritusMedium1.size(), 1);
        assertEquals(espiritusMedium2.size(), 1);
        assertFalse(kyuMalvadoAct.estaLibre());
        assertFalse(kyuAct.estaLibre());


    }

    @Test
    void testExorcizarMedium1a1DerrotaConDesconexion(){

        medium.setMana(0);

        dado.setModo(new ModoTrucado(6,60));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 5,"Kyu");
        espirituService.crear(kyu);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 40,"kyuMalvado");
        espirituService.crear(kyuMalvado);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());


        assertEquals(espiritusMedium1.size(), 0);
        assertEquals(espiritusMedium2.size(), 1);
        assertFalse(kyuMalvadoAct.estaLibre());
        assertTrue(kyuAct.estaLibre());


    }

    @Test
    void testExorcizarMedium2a1Victoria(){

        medium.setMana(0);
        medium2.setMana(0);

        dado.setModo(new ModoTrucado(5,10));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 30,"Kyu");
        espirituService.crear(kyu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 50,"castiel");
        espirituService.crear(castiel);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 40,"kyuMalvado");
        espirituService.crear(kyuMalvado);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());


        assertEquals(espiritusMedium1.size(), 2);
        assertEquals(espiritusMedium2.size(), 0);
        assertTrue(kyuMalvadoAct.estaLibre());
        assertFalse(kyuAct.estaLibre());
        assertFalse(castielAct.estaLibre());


    }

    @Test
    void testExorcizarMedium2a1AmbasDerrotasSinDesconexion(){

        medium.setMana(0);
        medium2.setMana(0);

        dado.setModo(new ModoTrucado(5,90));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 30,"Kyu");
        espirituService.crear(kyu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 50,"castiel");
        espirituService.crear(castiel);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 40,"kyuMalvado");
        espirituService.crear(kyuMalvado);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());


        assertEquals(espiritusMedium1.size(), 2);
        assertEquals(espiritusMedium2.size(), 1);
        assertFalse(kyuMalvadoAct.estaLibre());
        assertFalse(kyuAct.estaLibre());
        assertFalse(castielAct.estaLibre());


    }

    @Test
    void testExorcizarMedium2a1AmbasDerrotasConDesconexion(){

        medium.setMana(0);
        medium2.setMana(0);

        dado.setModo(new ModoTrucado(5,90));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 5,"Kyu");
        espirituService.crear(kyu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 5,"castiel");
        espirituService.crear(castiel);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 40,"kyuMalvado");
        espirituService.crear(kyuMalvado);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());


        assertEquals(espiritusMedium1.size(), 0);
        assertEquals(espiritusMedium2.size(), 1);
        assertFalse(kyuMalvadoAct.estaLibre());
        assertTrue(kyuAct.estaLibre());
        assertTrue(castielAct.estaLibre());


    }

    @Test
    void testExorcizarMedium2a1UnaDerrotaYUnaVictoriaSinDesconexion(){

        medium.setMana(0);
        medium2.setMana(0);

        dado.setModo(new ModoTrucado(5,40));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 10,"Kyu");
        espirituService.crear(kyu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 40,"castiel");
        espirituService.crear(castiel);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 40,"kyuMalvado");
        espirituService.crear(kyuMalvado);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());


        assertEquals(espiritusMedium1.size(), 2);
        assertEquals(espiritusMedium2.size(), 1);
        assertFalse(kyuMalvadoAct.estaLibre());
        assertFalse(kyuAct.estaLibre());
        assertFalse(castielAct.estaLibre());


    }

    @Test
    void testExorcizarMedium2a1UnaDerrotaYUnaVictoriaConDesconexion(){

        medium.setMana(0);
        medium2.setMana(0);

        dado.setModo(new ModoTrucado(5,40));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 5,"Kyu");
        espirituService.crear(kyu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 40,"castiel");
        espirituService.crear(castiel);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 40,"kyuMalvado");
        espirituService.crear(kyuMalvado);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());


        assertEquals(espiritusMedium1.size(), 1);
        assertEquals(espiritusMedium2.size(), 1);
        assertFalse(kyuMalvadoAct.estaLibre());
        assertTrue(kyuAct.estaLibre());
        assertFalse(castielAct.estaLibre());


    }

    @Test
    void testExorcizarMedium2a1UnaDerrotaYUnaVictoriaConDesconexionDeAmbosLados(){

        medium.setMana(0);
        medium2.setMana(0);

        dado.setModo(new ModoTrucado(5,40));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 5,"Kyu");
        espirituService.crear(kyu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 40,"castiel");
        espirituService.crear(castiel);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 20,"kyuMalvado");
        espirituService.crear(kyuMalvado);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());


        assertEquals(espiritusMedium1.size(), 1);
        assertEquals(espiritusMedium2.size(), 0);
        assertTrue(kyuMalvadoAct.estaLibre());
        assertTrue(kyuAct.estaLibre());
        assertFalse(castielAct.estaLibre());


    }

    @Test
    void testExorcizarMedium2a2VictoriaAbsoluta(){

        medium.setMana(0);
        medium2.setMana(0);

        dado.setModo(new ModoTrucado(5,40));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 50,"Kyu");
        espirituService.crear(kyu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 70,"castiel");
        espirituService.crear(castiel);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 25,"kyuMalvado");
        espirituService.crear(kyuMalvado);
        Espiritu azael = new Espiritu(TipoEspiritu.DEMONIACO, 35,"azael");
        espirituService.crear(azael);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());
        espirituService.ubicarseEn(azael.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());
        mediumConectado2 = espirituService.conectar(azael.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());
        Espiritu azaelAct = espirituService.recuperar(azael.getId());


        assertEquals(espiritusMedium1.size(), 2);
        assertEquals(espiritusMedium2.size(), 0);
        assertTrue(kyuMalvadoAct.estaLibre());
        assertFalse(kyuAct.estaLibre());
        assertFalse(castielAct.estaLibre());
        assertTrue(azaelAct.estaLibre());


    }

    @Test
    void testExorcizarMedium2a2DerrotaAbsolutaSinYConDesconexion(){

        medium.setMana(0);
        medium2.setMana(0);

        dado.setModo(new ModoTrucado(5,70));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 5,"Kyu");
        espirituService.crear(kyu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 10,"castiel");
        espirituService.crear(castiel);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 25,"kyuMalvado");
        espirituService.crear(kyuMalvado);
        Espiritu azael = new Espiritu(TipoEspiritu.DEMONIACO, 35,"azael");
        espirituService.crear(azael);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());
        espirituService.ubicarseEn(azael.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());
        mediumConectado2 = espirituService.conectar(azael.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());
        Espiritu azaelAct = espirituService.recuperar(azael.getId());


        assertEquals(espiritusMedium1.size(), 1);
        assertEquals(espiritusMedium2.size(), 2);
        assertFalse(kyuMalvadoAct.estaLibre());
        assertTrue(kyuAct.estaLibre());
        assertFalse(castielAct.estaLibre());
        assertFalse(azaelAct.estaLibre());


    }

    @Test
    void testExorcizarMedium2a2UnaVictoriaYUnaDerrotaConDesconexionDeAmbosLados(){

        medium.setMana(0);
        medium2.setMana(0);

        dado.setModo(new ModoTrucado(5,70));
        Espiritu kyu = new Espiritu(TipoEspiritu.ANGELICAL, 70,"Kyu");
        espirituService.crear(kyu);
        Espiritu castiel = new Espiritu(TipoEspiritu.ANGELICAL, 5,"castiel");
        espirituService.crear(castiel);
        Espiritu kyuMalvado = new Espiritu(TipoEspiritu.DEMONIACO, 25,"kyuMalvado");
        espirituService.crear(kyuMalvado);
        Espiritu azael = new Espiritu(TipoEspiritu.DEMONIACO, 35,"azael");
        espirituService.crear(azael);

        espirituService.ubicarseEn(kyu.getId(),bernal.getId());
        espirituService.ubicarseEn(castiel.getId(),bernal.getId());
        espirituService.ubicarseEn(kyuMalvado.getId(),bernal.getId());
        espirituService.ubicarseEn(azael.getId(),bernal.getId());

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

        mediumService.ubicarseEn(medium.getId(),bernal.getId());
        mediumService.ubicarseEn(medium2.getId(),bernal.getId());

        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium.getId());
        mediumConectado = espirituService.conectar(castiel.getId(), medium.getId());
        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());
        mediumConectado2 = espirituService.conectar(azael.getId(), medium2.getId());

        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());

        Medium mediumAct = mediumService.recuperar(medium.getId());
        Medium mediumAct2 = mediumService.recuperar(medium2.getId());
        List<Espiritu> espiritusMedium1 = mediumService.espiritus(mediumAct.getId());
        List<Espiritu> espiritusMedium2 = mediumService.espiritus(mediumAct2.getId());

        Espiritu kyuMalvadoAct = espirituService.recuperar(kyuMalvado.getId());
        Espiritu kyuAct = espirituService.recuperar(kyu.getId());
        Espiritu castielAct = espirituService.recuperar(castiel.getId());
        Espiritu azaelAct = espirituService.recuperar(azael.getId());


        assertEquals(espiritusMedium1.size(), 1);
        assertEquals(espiritusMedium2.size(), 1);
        assertTrue(kyuMalvadoAct.estaLibre());
        assertFalse(kyuAct.estaLibre());
        assertTrue(castielAct.estaLibre());
        assertFalse(azaelAct.estaLibre());


    }

    @Test
    void TestEjemplo(){

        medium.setMana(0);
        medium2.setMana(0);

        dado.setModo(new ModoTrucado(5,60));

        mediumService.guardar(medium);
        mediumService.guardar(medium2);

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

        assertEquals(espiritusMedium1.size(), 2);
        assertEquals(espiritusMedium2.size(), 1);
        assertTrue(jeagerAct.estaLibre());
        assertFalse(noroiAct.estaLibre());
        assertTrue(hanaAct.estaLibre());
        assertFalse(ivaarAct.estaLibre());
        assertFalse(rikaAct.estaLibre());
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
        espirituService.crear(espiritu2);
        Espiritu espirituAntes = espirituService.recuperar(espiritu2.getId());
        mediumService.guardar(medium3);
        Espiritu espirituInvocado = mediumService.invocar(medium3.getId(), espiritu2.getId());
        assertNotEquals(espirituInvocado.getMedium(), espirituAntes.getMedium());
        assertNotEquals(espirituInvocado.getUbicacion(), espirituAntes.getUbicacion());
    }

    // Testear que pasa si invoco en la misma ubicacion

    @Test
    void invocarEspirituNoLibre() {
        espirituService.crear(espiritu);
        mediumService.guardar(medium3);
        mediumService.guardar(medium2);
        mediumService.invocar(medium3.getId(), espiritu.getId());
        assertThrows(EspirituNoLibreException.class, () -> mediumService.invocar(medium2.getId(), espiritu.getId()));
    }

    @Test
    void invocarEspirituSinMana() {
        espirituService.crear(espiritu);
        Espiritu espirituAntes = espirituService.recuperar(espiritu.getId());
        mediumService.guardar(mediumSinMana);
        Espiritu espirituNoInvocado = mediumService.invocar(mediumSinMana.getId(), espiritu.getId());
        assertEquals(espirituNoInvocado.getMedium(), espirituAntes.getMedium());
        assertEquals(espirituNoInvocado.getUbicacion(), espirituAntes.getUbicacion());

    }

    @Test
    void espiritusDeMedium(){
        espirituService.crear(espiritu);
        mediumService.guardar(medium3);
        // Utilizo conectar pero solo para agregar espiritus al medium.
        espirituService.conectar(espiritu.getId(), medium3.getId());
        assertEquals((mediumService.espiritus(medium3.getId())).size(), 1);
    }

    @Test
    void espiritusDeMediumSinEspiritus(){
        mediumService.guardar(medium3);
        assertEquals((mediumService.espiritus(medium3.getId())).size(), 0);
    }

    @Test
    void espiritusDeMediumConVariosEspiritus(){
        espirituService.crear(espiritu);
        espirituService.crear(espiritu3);
        mediumService.guardar(medium3);
        espirituService.conectar(espiritu.getId(), medium3.getId());
        espirituService.conectar(espiritu3.getId(), medium3.getId());
        assertEquals((mediumService.espiritus(medium3.getId())).size(), 2);
    }

    @AfterEach
    void cleanUp() {
        espirituService.eliminarTodo();
        mediumService.eliminarTodo();

        dado.setModo(new ModoRandom());
        ubicacionService.eliminarTodo();
        medium.setMana(100);
        medium2.setMana(50);
    }
}


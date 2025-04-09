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


    @BeforeEach
    void setUp() {
        MediumDAO mDAO =new HibernateMediumDAO();
        EspirituDAO eDAO = new HibernateEspirituDAO();
        UbicacionDAO uDAO = new HibernateUbicacionDao();
        mediumService = new MediumServiceImpl(mDAO, eDAO, uDAO);
        espirituService = new EspirituServiceImpl(eDAO, mDAO, uDAO);
        ubicacionService = new UbicacionServiceImpl(uDAO,mDAO,eDAO);
        bernal = new Ubicacion("Bernal");
        medium3 = new Medium("Lala", 100, 50, bernal);
        mediumSinMana = new Medium("Nomana", 100, 0, bernal);
        ubicacionService.crear(bernal);
        this.medium = new Medium("Lizzie",150,100, bernal);
        this.medium2 = new Medium("Lorraine", 200, 50, bernal);

        this.dado = Dado.getInstance();


        this.medium = new Medium("Lizzie",150,100);
        this.medium2 = new Medium("Lorraine", 200, 50);
        espiritu = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Casper");

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
    void elminarMediumYaEliminado(){
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

//    @Test
//    void testExorcizarMedium1a1Victorioso(){
//
//        dado.setModo(new ModoTrucado(6,60));
//        Espiritu kyu = new Espiritu("Demoniaco", 80,"Kyu",ubicacion);
//        espirituService.crear(kyu);
//        Espiritu kyuMalvado = new Espiritu("Angelical", 40,"kyuMalvado",ubicacion);
//        espirituService.crear(kyuMalvado);
//
//        mediumService.guardar(medium);
//        mediumService.guardar(medium2);
//
//        Medium mediumConectado = espirituService.conectar(kyu.getId(), medium2.getId());
//        Medium mediumConectado2 = espirituService.conectar(kyuMalvado.getId(), medium2.getId());
//
//        mediumService.exorcizar(mediumConectado.getId(), mediumConectado2.getId());
//
//        assertTrue(kyuMalvado.estaLibre());
//        assertFalse(kyu.estaLibre());
//
//
//    }


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
        espirituService.crear(espiritu);
        Espiritu espirituAntes = espirituService.recuperar(espiritu.getId());
        mediumService.guardar(medium3);
        Espiritu espirituInvocado = mediumService.invocar(medium3.getId(), espiritu.getId());
        assertNotEquals(espirituInvocado.getMedium(), espirituAntes.getMedium());
        assertNotEquals(espirituInvocado.getUbicacion(), espirituAntes.getUbicacion());
    }

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

    @AfterEach
    void cleanUp() {
        espirituService.eliminarTodo();
        mediumService.eliminarTodo();

        dado.setModo(new ModoRandom());
        ubicacionService.eliminarTodo();
    }
}


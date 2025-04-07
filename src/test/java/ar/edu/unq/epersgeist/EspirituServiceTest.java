package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateUbicacionDao;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.UbicacionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EspirituServiceTest {

    private EspirituService espirituService = new EspirituServiceImpl(new HibernateEspirituDAO(), new HibernateMediumDAO(),new HibernateUbicacionDao());
    private MediumServiceImpl mediumService = new MediumServiceImpl(new HibernateMediumDAO(), new HibernateEspirituDAO(), new HibernateUbicacionDao());
    private UbicacionServiceImpl ubicacionService = new UbicacionServiceImpl( new HibernateUbicacionDao(),new HibernateMediumDAO(), new HibernateEspirituDAO());
    private Espiritu Casper;
    private Espiritu Jinn;
    private Espiritu Oni;
    private Espiritu Anabelle;
    private Espiritu Volac;
    private Medium medium;
    private Medium medium2;
    private Ubicacion Bernal;

    @BeforeEach
    void setUp(){
        Bernal = new Ubicacion("Bernal");
        ubicacionService.crear(Bernal);
        Casper = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Casper", Bernal);
        Oni = new Espiritu(TipoEspiritu.DEMONIACO, 95, "Otakemaru");
        Jinn = new Espiritu(TipoEspiritu.DEMONIACO, 100, "Marids");
        Anabelle = new Espiritu(TipoEspiritu.DEMONIACO, 48, "Anabelle");
        Volac = new Espiritu(TipoEspiritu.DEMONIACO, 55, "Volac");
        medium = new Medium("lala", 100, 50,Bernal);
        medium2 = new Medium("lolo", 100, 60);

    }


    @Test
    void testGuardarEspiritu(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        assertNotNull(Casper.getId());
        assertNotNull(Oni.getId());
        assertNotNull(Jinn.getId());
        System.out.println("El id es: " + Casper.getId());
        System.out.println("El id es: " + Oni.getId());
        System.out.println("El id es: " + Jinn.getId());
    }


    @Test
    void testActualizarEspiritu(){
        espirituService.crear(Casper);
        Espiritu sinActualizar = espirituService.recuperar(Casper.getId());
        Casper.setNombre("Lala");
        System.out.println("El nombre ahora es: " + Casper.getNombre());
        espirituService.actualizar(Casper);
        Espiritu actualizado = espirituService.recuperar(Casper.getId());
        assertEquals(sinActualizar.getId(), Casper.getId());
        assertEquals(sinActualizar.getNombre(), "Casper");
        assertEquals(actualizado.getNombre(), "Lala");
    }


    @Test
    void testRecuperarEspiritu(){
        espirituService.crear(Casper);
        Espiritu espirituRecuperado = espirituService.recuperar(Casper.getId());
        assertEquals(espirituRecuperado.getNombre(), "Casper");
        assertEquals(espirituRecuperado.getTipo(), TipoEspiritu.ANGELICAL);
        assertEquals(espirituRecuperado.getNivelDeConexion(), 0);
    }

    @Test
    void testRecuperarTodosLosEspiritus(){
        //Se deben obtener alfabeticamente
        espirituService.crear(Oni);
        espirituService.crear(Casper);
        espirituService.crear(Jinn);
        List<Espiritu> espiritus = espirituService.recuperarTodos();
        assertEquals(espiritus.size(), 3);
        assertEquals(espiritus.get(0).getNombre(), "Casper");
        assertEquals(espiritus.get(1).getNombre(), "Marids");
        assertEquals(espiritus.get(2).getNombre(), "Otakemaru");
    }

    @Test
    void testEliminarEspiritu(){
        espirituService.crear(Casper);
        Long espirituId = Casper.getId();
        assertNotNull(espirituService.recuperar(espirituId));
        espirituService.eliminar(Casper);
        assertNull(espirituService.recuperar(espirituId));
    }

    @Test
    void testObtenerLos3EspiritusDemoniacosConMayorNivel(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.DESCENDENTE, 1, 3);
        assertEquals(demonios.size(), 3);
        assertEquals(demonios.get(0).getNombre(), "Marids");
        assertEquals(demonios.get(1).getNombre(), "Otakemaru");
        assertEquals(demonios.get(2).getNombre(), "Volac");
    }

    @Test
    void testObtenerLos3EspiritusDemoniacosConMenorNivel(){
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        espirituService.crear(Casper);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 1, 3);
        assertEquals(demonios.size(), 3);
        assertEquals(demonios.get(0).getNombre(), "Anabelle");
        assertEquals(demonios.get(1).getNombre(), "Volac");
        assertEquals(demonios.get(2).getNombre(), "Otakemaru");
    }

    @Test
    void testObtener2EspiritusDemoniacosDeLaSegundPaginaOrdenAscendente(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 2, 2);
        assertEquals(demonios.size(), 2);
        assertEquals(demonios.get(0).getNombre(), "Otakemaru");
        assertEquals(demonios.get(1).getNombre(), "Marids");
    }

    @Test
    void testConectarConMedium(){
        mediumService.guardar(medium);
        espirituService.crear(Casper);
        assertEquals(0, medium.getEspiritus().size());
        Medium mediumConectado = espirituService.conectar(Casper.getId(), medium.getId());
        Espiritu espirituConectado = espirituService.recuperar(Casper.getId());
        assertEquals(mediumConectado.getId(), medium.getId());
        assertEquals(1, mediumConectado.getEspiritus().size());
        assertFalse(espirituConectado.estaLibre());
        assertEquals(10, espirituConectado.getNivelDeConexion());
    }

    @AfterEach
    void cleanup() {
        espirituService.eliminarTodo();
        mediumService.eliminarTodo();
        ubicacionService.eliminarTodo();
    }

}
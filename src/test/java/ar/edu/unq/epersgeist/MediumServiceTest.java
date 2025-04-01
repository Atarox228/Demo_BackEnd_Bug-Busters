package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
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

    @BeforeEach
    void setUp() {
        this.mediumService = new MediumServiceImpl(new HibernateMediumDAO());
        this.medium = new Medium("Lizzie",150,100);
        this.medium2 = new Medium("Lorraine", 200, 50);
    }

    @Test
    void testGuardarMedium() {
        mediumService.guardar(medium);
        assertNotNull(medium.getId());
    }

    @Test
    void testRecuperarMedium() {
        mediumService.guardar(medium);
        Medium mediumRecuperado = mediumService.recuperar(medium.getId());
        assertNotNull(mediumRecuperado);
        assertEquals(medium.getId(), mediumRecuperado.getId());
    }

    @Test
    void testRecuperarTodos() {
        mediumService.guardar(medium);
        mediumService.guardar(medium2);
        Collection<Medium> mediums = mediumService.recuperarTodos();
        assertEquals(2, mediums.size());
        List<String> nombres = mediums.stream().map(Medium::getNombre).toList();
        assertEquals(List.of("Lizzie", "Lorraine"), nombres);
    }

    @Test
    void testEliminarMedium() {
        MediumService mediumService = new MediumServiceImpl(new HibernateMediumDAO());
        Medium medium = new Medium("Lizzie", 150, 100);
        mediumService.guardar(medium);
        Long mediumId = medium.getId();
        assertNotNull(mediumService.recuperar(mediumId));
        mediumService.eliminar(medium);
        assertNull(mediumService.recuperar(mediumId));
    }

    @Test
    void testEliminarTodosLosMediums() {
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
    void actualizarEspiritu(){
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

    @AfterEach
    void tearDown() {
        mediumService.eliminarTodo();
    }
}


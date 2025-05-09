package ar.edu.unq.epersgeist.service;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.enums.TipoEspiritu;
import ar.edu.unq.epersgeist.service.dataService.DataService;
import ar.edu.unq.epersgeist.servicios.*;
import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import ar.edu.unq.epersgeist.servicios.exception.*;
import ar.edu.unq.epersgeist.modelo.exception.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EspirituServiceTest {

    @Autowired
    private EspirituService espirituService;
    @Autowired
    private MediumService mediumService;
    @Autowired
    private UbicacionService ubicacionService;
    @Autowired
    private DataService dataService;
    private Angel Casper;
    private Demonio Jinn;
    private Demonio Oni;
    private Demonio Anabelle;
    private Demonio Volac;
    private Medium medium;
    private Medium  medium2;
    private Ubicacion Bernal;
    private Ubicacion Quilmes;

    @BeforeEach
    void setUp(){

        Bernal = new Cementerio("Bernal", 100);
        Quilmes = new Cementerio("Quilmes", 100);
        ubicacionService.crear(Bernal);
        ubicacionService.crear(Quilmes);

        Casper = new Angel("Casper");
        Oni = new Demonio("Otakemaru");
        Oni.setNivelConexion(95);
        Jinn = new Demonio("Marids");
        Jinn.setNivelConexion(100);
        Anabelle = new Demonio("Anabelle");
        Anabelle.setNivelConexion(48);
        Volac = new Demonio("Volac");
        Volac.setNivelConexion(55);

        medium = new Medium("Lala", 100, 50);
        medium2 = new Medium("Lalo",100,100);

    }

    @Test
    void guardarEspiritu(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        assertNotNull(Casper.getId());
        assertNotNull(Oni.getId());
        assertNotNull(Jinn.getId());
    }

    @Test
    void actualizarEspiritu(){
        espirituService.crear(Casper);
        Espiritu sinActualizar = espirituService.recuperar(Casper.getId()).get();
        Casper.setNombre("Lala");
        espirituService.actualizar(Casper);
        Espiritu actualizado = espirituService.recuperar(Casper.getId()).get();
        assertEquals(sinActualizar.getId(), Casper.getId());
        assertEquals("Casper", sinActualizar.getNombre());
        assertEquals("Lala", actualizado.getNombre());
    }

    @Test
    void actualizarEspirituNoRegistrado(){
        assertThrows(IdNoValidoException.class, () -> {
            espirituService.actualizar(Casper);
        });
    }

    @Test
    void actualizarEspirituEliminado(){
        espirituService.crear(Casper);
        Casper.setNombre("Lala");
        espirituService.eliminar(Casper);
        assertThrows(EntidadEliminadaException.class, () -> {
            espirituService.actualizar(Casper);
        });
    }


    @Test
    void recuperarEspiritu(){
        espirituService.crear(Casper);
        Espiritu espirituRecuperado = espirituService.recuperar(Casper.getId()).get();
        assertEquals("Casper", espirituRecuperado.getNombre());
        assertEquals(TipoEspiritu.ANGELICAL, espirituRecuperado.getTipo());
        assertEquals(0, espirituRecuperado.getNivelConexion());
    }

    @Test
    void recuperarEspirituNoRegistrado() {
        assertThrows(IdNoValidoException.class, () -> {
            espirituService.recuperar(Casper.getId());
        });
    }


    @Test
    void recuperarEspirituEliminado() {
        espirituService.crear(Casper);
        espirituService.eliminar(Casper);
        assertThrows(EntidadEliminadaException.class, () -> {
            espirituService.recuperar(Casper.getId());
        });
    }

    @Test
    void recuperarTodosLosEspiritus(){
        //Se deben obtener alfabeticamente
        espirituService.crear(Oni);
        espirituService.crear(Casper);
        espirituService.crear(Jinn);
        List<Espiritu> espiritus = espirituService.recuperarTodos();
        assertEquals(3, espiritus.size());
        assertEquals("Casper", espiritus.get(0).getNombre());
        assertEquals("Marids", espiritus.get(1).getNombre());
        assertEquals("Otakemaru", espiritus.get(2).getNombre());
    }

    @Test
    void eliminarEspiritu(){
        espirituService.crear(Casper);
        Long espirituId = Casper.getId();
        assertNotNull(espirituService.recuperar(espirituId));
        espirituService.eliminar(Casper);
        assertThrows(RuntimeException.class, () -> {
            espirituService.recuperar(espirituId);
        });
    }


    @Test
    void obtenerEspiritusDemoniacos() {
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.DESCENDENTE, 1, 5);
        assertEquals(demonios.size(), 4);
    }

    @Test
    void obtenerLos3EspiritusDemoniacosConMayorNivel(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.DESCENDENTE, 1, 3);
        assertEquals(3, demonios.size());
        assertEquals("Marids", demonios.get(0).getNombre());
        assertEquals("Otakemaru", demonios.get(1).getNombre());
        assertEquals("Volac", demonios.get(2).getNombre());
    }

    @Test
    void obtenerLos3EspiritusDemoniacosConMenorNivel(){
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        espirituService.crear(Casper);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 1, 3);
        assertEquals(3, demonios.size());
        assertEquals("Anabelle", demonios.get(0).getNombre());
        assertEquals("Volac", demonios.get(1).getNombre());
        assertEquals("Otakemaru", demonios.get(2).getNombre());
    }

    @Test
    void obtener2EspiritusDemoniacosDeLaSegundPaginaOrdenAscendente(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 2, 2);
        assertEquals(2, demonios.size());
        assertEquals("Otakemaru", demonios.get(0).getNombre());
        assertEquals("Marids", demonios.get(1).getNombre());
    }

    @Test
    void obtenerEspiritusDemoniacosDePaginaVacia(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 4, 2);
        assertEquals(0, demonios.size());
    }

    @Test
    void obtenerEspiritusDemoniacosConMenosDeLaCantidadSolicitada(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        espirituService.crear(Jinn);
        espirituService.crear(Anabelle);
        espirituService.crear(Volac);
        List<Espiritu> demonios = espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 1, 10);
        assertEquals(4, demonios.size());
    }

    @Test
    void obtenerEspiritusDemoniacosDePaginaInvalida(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        assertThrows(PaginaInvalidaException.class,()->{
            espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, -3, 10);
        });
    }

    @Test
    void obtenerEspiritusDemoniacosDeCantidadPorPaginaInvalida(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        assertThrows(PaginaInvalidaException.class,()->{
            espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, 3, -10);
        });
    }

    @Test
    void obtenerEspiritusDemoniacosDeCantidadPorPaginaInvalidaYPaginaInvalida(){
        espirituService.crear(Casper);
        espirituService.crear(Oni);
        assertThrows(PaginaInvalidaException.class,()->{
            espirituService.espiritusDemoniacos(Direccion.ASCENDENTE, -1, -10);
        });
    }

    @Test
    void conectarConMediumExitoso(){
        mediumService.crear(medium);
        espirituService.crear(Casper);

        medium.setUbicacion(Bernal);
        mediumService.actualizar(medium);
        Casper.setUbicacion(Bernal);
        espirituService.actualizar(Casper);
//        mediumService.mover(medium.getId(),Quilmes.getId());

        assertEquals(0, medium.getEspiritus().size());
        Medium mediumConectado = espirituService.conectar(Casper.getId(), medium.getId()).get();
        Espiritu espirituConectado = espirituService.recuperar(Casper.getId()).get();
        assertEquals(mediumConectado.getId(), medium.getId());
        assertEquals(1, mediumConectado.getEspiritus().size());
        assertFalse(espirituConectado.estaLibre());
        assertEquals(10, espirituConectado.getNivelConexion());
    }

    @Test
    void conexionFallidaPorUbicacion(){
        mediumService.crear(medium);
        espirituService.crear(Casper);

        medium.setUbicacion(Quilmes);
        mediumService.actualizar(medium);
        Casper.setUbicacion(Bernal);
        espirituService.actualizar(Casper);
//        mediumService.mover(medium.getId(),Quilmes.getId());
//

        assertEquals(0, medium.getEspiritus().size());
        assertThrows(NoSePuedenConectarException.class, () -> {
            espirituService.conectar(Casper.getId(), medium.getId());
        });
    }

    @Test
    void conexionFallidaPorLibertadDeEspiritu(){

        mediumService.crear(medium);
        espirituService.crear(Casper);
        mediumService.crear(medium2);
        medium.setUbicacion(Bernal);
        medium2.setUbicacion(Bernal);
        mediumService.actualizar(medium);
        mediumService.actualizar(medium2);
        Casper.setUbicacion(Bernal);
        espirituService.actualizar(Casper);
//        mediumService.mover(medium.getId(),Bernal.getId());
//        mediumService.mover(medium2.getId(),Bernal.getId());

        espirituService.conectar(Casper.getId(), medium2.getId());
        assertEquals(0, medium.getEspiritus().size());
        assertThrows(NoSePuedenConectarException.class, () -> {
            espirituService.conectar(Casper.getId(), medium.getId());
        });
    }

    //test de soft delete
    @Test
    void creacionTimeStampUpdateAndNoDelete(){
        Espiritu angel = new Angel("azael");
        Espiritu demonio = new Demonio("belcebu");

        espirituService.crear(angel);
        espirituService.crear(demonio);

        Espiritu angelAct = espirituService.recuperar(angel.getId()).get();
        Espiritu demonioAct = espirituService.recuperar(demonio.getId()).get();

        assertNotNull(angelAct.getCreatedAt());
        assertNotNull(angelAct.getUpdatedAt());
        assertFalse(angelAct.getDeleted());
        assertNotNull(demonioAct.getCreatedAt());
        assertNotNull(demonioAct.getUpdatedAt());
        assertFalse(demonioAct.getDeleted());

    }

    //test de sauditoria de datos
    @Test
    void updateTimeStamp() throws InterruptedException {
        Espiritu angel = new Angel("azael");
        Espiritu demonio = new Demonio("belcebu");

        espirituService.crear(angel);
        espirituService.crear(demonio);

        Espiritu angelAct = espirituService.recuperar(angel.getId()).get();
        Espiritu demonioAct = espirituService.recuperar(demonio.getId()).get();

        Thread.sleep(1000);

        angelAct.setNombre("juancho");
        espirituService.actualizar(angelAct);
        angelAct = espirituService.recuperar(angelAct.getId()).get();

        demonioAct.setNombre("DobleDemonio");
        espirituService.actualizar(demonioAct);
        demonioAct = espirituService.recuperar(demonioAct.getId()).get();

        int comparison = angelAct.getUpdatedAt().compareTo(angelAct.getCreatedAt());
        int comparison2 = angelAct.getUpdatedAt().compareTo(demonioAct.getCreatedAt());

        assertTrue(comparison > 0);
        assertTrue(comparison2 > 0);

    }

    @Test
    void updateTimeStampDoble() throws InterruptedException {
        Espiritu angel = new Angel("azael");
        Espiritu demonio = new Demonio("belcebu");

        espirituService.crear(angel);
        espirituService.crear(demonio);

        Espiritu angelAct = espirituService.recuperar(angel.getId()).get();
        Espiritu demonioAct = espirituService.recuperar(demonio.getId()).get();

        Thread.sleep(1000);

        angelAct.setNombre("juancho");
        espirituService.actualizar(angelAct);
        angelAct = espirituService.recuperar(angelAct.getId()).get();
        Date lastUpdate = angelAct.getUpdatedAt();

        demonioAct.setNombre("DobleDemonio");
        espirituService.actualizar(demonioAct);
        demonioAct = espirituService.recuperar(demonioAct.getId()).get();
        Date lastUpdate2 = demonioAct.getUpdatedAt();

        Thread.sleep(1000);

        angelAct.setNombre("miphiel");
        espirituService.actualizar(angelAct);
        angelAct = espirituService.recuperar(angelAct.getId()).get();

        demonioAct.setNombre("sartorias");
        espirituService.actualizar(demonioAct);
        demonioAct = espirituService.recuperar(demonioAct.getId()).get();

        int comparison = angelAct.getUpdatedAt().compareTo(lastUpdate);
        int comparison2 = demonioAct.getUpdatedAt().compareTo(lastUpdate2);

        assertTrue(comparison > 0);
        assertTrue(comparison2 > 0);

    }

    @Test
    void softDeletion(){
        Espiritu angel = new Angel("azael");
        Espiritu demonio = new Demonio("belcebu");

        espirituService.crear(angel);
        espirituService.crear(demonio);

        Espiritu angelAct = espirituService.recuperar(angel.getId()).get();
        Espiritu demonioAct = espirituService.recuperar(demonio.getId()).get();

        espirituService.eliminar(angelAct);
        espirituService.eliminar(demonioAct);

        Espiritu angelBorrado = espirituService.recuperarAunConSoftDelete(angelAct.getId()).get();
        Espiritu demonioBorrado = espirituService.recuperarAunConSoftDelete(demonioAct.getId()).get();

        assertTrue(angelBorrado.getDeleted());
        assertTrue(demonioBorrado.getDeleted());
        assertThrows(EntidadEliminadaException.class, () -> {
            espirituService.recuperar(angelAct.getId());
        });
        assertThrows(EntidadEliminadaException.class, () -> {
            espirituService.recuperar(demonioAct.getId());
        });

    }

    @Test
    void noRecuperaTodosConSoftdelete(){
        espirituService.eliminarTodo();
        Espiritu angel = new Angel("azael");
        Espiritu demonio = new Demonio("belcebu");
        Espiritu demonio2 = new Demonio("miras");


        espirituService.crear(angel);
        espirituService.crear(demonio);
        espirituService.crear(demonio2);

        Espiritu angelAct = espirituService.recuperar(angel.getId()).get();
        Espiritu demonioAct = espirituService.recuperar(demonio.getId()).get();

        espirituService.eliminar(angelAct);
        espirituService.eliminar(demonioAct);


        List<Espiritu> todos = espirituService.recuperarTodos();

        Espiritu angelBorrado = espirituService.recuperarAunConSoftDelete(angelAct.getId()).get();
        Espiritu demonioBorrado = espirituService.recuperarAunConSoftDelete(demonioAct.getId()).get();

        assertTrue(angelBorrado.getDeleted());
        assertTrue(demonioBorrado.getDeleted());
        assertThrows(EntidadEliminadaException.class, () -> {
            espirituService.recuperar(angelAct.getId());
        });
        assertThrows(EntidadEliminadaException.class, () -> {
            espirituService.recuperar(demonioAct.getId());
        });
        assertEquals(todos.size(),1);

    }

    @Test
    void noRecuperaEspiritusDemoniacosConSoftDelete(){
        espirituService.eliminarTodo();
        Espiritu demonio = new Demonio("belcebu");
        Espiritu demonio2 = new Demonio("miras");

        espirituService.crear(demonio);
        espirituService.crear(demonio2);

        Espiritu demonioAct = espirituService.recuperar(demonio.getId()).get();

        espirituService.eliminar(demonioAct);

        List<Espiritu> todos = espirituService.espiritusDemoniacos(Direccion.DESCENDENTE, 1,2);

        Espiritu demonioBorrado = espirituService.recuperarAunConSoftDelete(demonioAct.getId()).get();

        assertTrue(demonioBorrado.getDeleted());
        assertThrows(EntidadEliminadaException.class, () -> {
            espirituService.recuperar(demonioAct.getId());
        });
        assertEquals(todos.size(),1);
    }

    @AfterEach
    void cleanUp() {
        dataService.eliminarTodo();


    }

}
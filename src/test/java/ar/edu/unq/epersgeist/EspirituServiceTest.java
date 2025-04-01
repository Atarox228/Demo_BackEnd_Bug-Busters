/*
package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.jdbc.JDBCConnector;
import ar.edu.unq.epersgeist.persistencia.dao.jdbc.exception.RecuperarException;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
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

    private EspirituService espirituService = new EspirituServiceImpl();
    private Espiritu Casper;
    private Espiritu Jinn;
    private Espiritu Oni;

    @BeforeEach
    void setUp(){
        Casper = new Espiritu("Poltergeist", 0, "Casper");
        Oni = new Espiritu("Oni", 95, "Otakemaru");
        Jinn = new Espiritu("Jinn", 100, "Marids");
    }


    @Test
    void crearEspiritu(){
        Espiritu espirituActualizado = espirituService.crear(Casper);
        assertNotEquals(espirituActualizado.getId(), null);
    }

    @Test
    void crearDosEspiritusIguales(){
        Espiritu esp1 = espirituService.crear(Casper);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            espirituService.crear(Casper);
        });
        assertTrue(exception.getMessage().contains("Ya existe un espiritu con el mismo nombre"));
    }

    @Test
    void recuperarEspiritu(){
        Espiritu espirituDelCielo = espirituService.crear(Casper);
        Espiritu espirituRecuperado = espirituService.recuperar(espirituDelCielo.getId());

        //Al ser Nombres unicos, si compruebo que tienen el mismo nombre se podria decir que son
        //el ¨mismo¨ objeto
        assertEquals(espirituDelCielo.getNombre(),espirituRecuperado.getNombre());
    }

    @Test
    void recuperarEspirituEliminado(){
        Espiritu espirituNuevo = espirituService.crear(Oni);
        long idEspN = espirituNuevo.getId();
        espirituService.eliminar(idEspN);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            espirituService.recuperar(idEspN);
        });
        assertTrue(exception.getMessage().contains("No se encontro el espiritu"));
    }


    @Test
    void eliminarEspiritu() {
        Espiritu espirituActualizado = espirituService.crear(Casper);
        long idEsp1 = espirituActualizado.getId();
        espirituService.eliminar(espirituActualizado.getId());
        Espiritu espiritu2 = espirituService.crear(Casper);
        long idEsp2 = espiritu2.getId();
        //Si existiera casper en la base de datos, no podria crear otro
        assertNotNull(idEsp2);
    }

    @Test
    void eliminarEspirituEliminado() {
        Espiritu espirituActualizado = espirituService.crear(Jinn);
        espirituService.eliminar(espirituActualizado.getId());
        espirituService.eliminar(espirituActualizado.getId());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            espirituService.recuperar(espirituActualizado.getId());
        });
        assertTrue(exception.getMessage().contains("No se encontro el espiritu"));
    }

    @Test
    void recuperarTodosLosEspiritus(){

        Espiritu esp1 = espirituService.crear(Jinn);
        Espiritu esp2 = espirituService.crear(Oni);
        Espiritu esp3 = espirituService.crear(Casper);

        List<Espiritu>espiritus = espirituService.recuperarTodos();
        List<String> nombresObtenidos = new ArrayList<>();
        for (Espiritu p : espiritus){
            nombresObtenidos.add(p.getNombre());
        }

        List<String> nombresEsperados = new ArrayList<>();
        nombresEsperados.add("Casper");
        nombresEsperados.add("Marids");
        nombresEsperados.add("Otakemaru");
        assertEquals(nombresEsperados, nombresObtenidos);
    }

    @Test
    void recuperarTodosSinEspiritus(){
        List<Espiritu>espiritus = espirituService.recuperarTodos();
        assertArrayEquals(espiritus.toArray(),new ArrayList<>().toArray());
    }

    @Test
    void actualizarEspiritu(){
        Medium marta = new Medium("marta",100,10);
        Espiritu esp1 = espirituService.crear(Casper);
        espirituService.conectar(esp1.getId(),marta);
        Espiritu espirituActualizado = espirituService.recuperar(esp1.getId());
        assertEquals(10,espirituActualizado.getNivelDeConexion());
    }

    @Test
    void actualizarEspirituNoExistente(){
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            espirituService.actualizar(Oni);
        });
        assertTrue(exception.getMessage().contains("Espiritu sin id asignada"));
    }

    @Test
    void actualizarEspirituEliminado(){
        Medium tita = new Medium("tita",100,10);
        Espiritu esp1 = espirituService.crear(Casper);
        espirituService.conectar(esp1.getId(), tita);
        espirituService.eliminar(esp1.getId());
        espirituService.actualizar(esp1);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            espirituService.recuperar(esp1.getId());
        });
        assertTrue(exception.getMessage().contains("No se encontro el espiritu"));
    }

    @Test
    void conexionEspirituMedium(){
        Medium lizzie = new Medium("Lizzie",100,10);
        Espiritu espiritu = espirituService.crear(Casper);
        espirituService.conectar(espiritu.getId(), lizzie);
        Espiritu espirituActualizado = espirituService.recuperar(espiritu.getId());
        assertEquals(espirituActualizado.getNivelDeConexion(), espiritu.getNivelDeConexion()+10);
    }

    @Test
    void conexionEspirituMediumYaHecha(){
        Medium sosa = new Medium("sosa",100,10);
        Espiritu espiritu = espirituService.crear(Casper);
        int elcoso = espiritu.getNivelDeConexion();
        espirituService.conectar(espiritu.getId(), sosa);
        espirituService.conectar(espiritu.getId(), sosa);
        Espiritu espirituActualizado = espirituService.recuperar(espiritu.getId());
        assertEquals(espirituActualizado.getNivelDeConexion(), elcoso+10);
    }

    @Test
    void conexionEspirituMediumLvlMax(){
        Medium micho = new Medium("micho",100,10);
        Espiritu espiritu = espirituService.crear(Oni);
        espirituService.conectar(espiritu.getId(), micho);
        Espiritu espirituActualizado = espirituService.recuperar(espiritu.getId());
        assertEquals(espirituActualizado.getNivelDeConexion(), 100);
    }

    @AfterEach
    void teardown(){
        List<Espiritu> espiritus = espirituService.recuperarTodos();
        for(Espiritu espiritu : espiritus){
            espirituService.eliminar(espiritu.getId());
        }
    }
}*/
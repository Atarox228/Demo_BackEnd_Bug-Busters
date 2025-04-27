package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.exception.EspirituNoLibreException;
import ar.edu.unq.epersgeist.modelo.exception.NoSePuedenConectarException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MediumModeloTest {
    private Espiritu Casper;
    private Demonio Demonio;
    private Medium medium;
    private Medium medium2;
    private Ubicacion Bernal;
    private Ubicacion Quilmes;


    @BeforeEach
    void setUp(){
        Casper = new Angel("Casper");
        Demonio = new Demonio("Demonio");

        medium = new Medium("lala", 100, 50);
        medium2 = new Medium("lolo", 100, 60);

        Bernal = new Cementerio("Bernal", 100);
        Quilmes = new Cementerio("Quilmes", 100);
    }


    @Test
    void puedenconectarse(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Bernal);
        assertTrue(medium.puedeConectarse(Casper));
    };

    @Test
    void noPuedenconectarsePorUbicacion(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Quilmes);
        assertFalse(medium.puedeConectarse(Casper));
    };

    @Test
    void noPuedenconectarsePorLibertad(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Bernal);
        Casper.setMedium(medium2);
        assertFalse(medium.puedeConectarse(Casper));
    };

    @Test
    void conexionConEspirituAngelicalExitosa(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Bernal);
        medium.conectarseAEspiritu(Casper);
        assertTrue(medium.getEspiritus().contains(Casper));
        //assertFalse(Casper.estaLibre());
        assertNotNull(Casper.getMedium());
        assertEquals(Casper.getNivelConexion(), medium.getMana() * 20 / 100);
    };

    @Test
    void conexionConEspirituFallidaPorLibertad(){
        medium.setUbicacion(Bernal);
        medium2.setUbicacion(Bernal);
        Casper.setUbicacion(Bernal);
        medium.conectarseAEspiritu(Casper);
        assertThrows(NoSePuedenConectarException.class, () -> medium2.conectarseAEspiritu(Casper));
    };

    @Test
    void conexionConEspirituFallidaPorUbicacion(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Quilmes);
        assertThrows(NoSePuedenConectarException.class, () -> medium.conectarseAEspiritu(Casper));
    };

    @Test
    void descansarAumentaMana() {
        medium.setUbicacion(Bernal);
        Integer mana = medium.getMana();
        medium.descansar();
        Integer nuevoMana = medium.getMana();
        assertEquals(100, nuevoMana);
    }

    @Test
    void descansarEnCementerioConDemonio() {
        medium.setUbicacion(Bernal);
        medium.invocar(Demonio);
        medium.conectarseAEspiritu(Demonio);
        medium.descansar();
        assertEquals(1, medium.getEspiritus().size());
        assertTrue(medium.getEspiritus().contains(Demonio));
        assertEquals(medium,  Demonio.getMedium());
        assertEquals(90, medium.getMana());
        assertEquals(100, Demonio.getNivelConexion());
    }

    @Test
    void descansarEnSantuarioConAngel() {
        Santuario santuario = new Santuario("santuario", 50);
        Casper.setNivelConexion(10);
        medium.setUbicacion(santuario);
        medium.invocar(Casper);
        medium.conectarseAEspiritu(Casper);
        medium.descansar();
        assertEquals(1, medium.getEspiritus().size());
        assertTrue(medium.getEspiritus().contains(Casper));
        assertEquals(medium,  Casper.getMedium());
        //assertEquals(90, medium.getMana());
        assertEquals(68, Casper.getNivelConexion());
    }

    @Test
    void descansarNoAumentaMana() {
        medium.setUbicacion(Bernal);
        medium.setMana(medium.getManaMax());
        Integer mana = medium.getMana();
        medium.descansar();
        Integer nuevoMana = medium.getMana();
        assertEquals(mana, nuevoMana);
    }

    @Test
    void invocacionExitosa() {
        medium.setUbicacion(Bernal);
        Integer mana = medium.getMana();
        medium.invocar(Demonio);
        Integer nuevoMana = medium.getMana();
        assertEquals(mana - 10, nuevoMana);
    }

    @Test
    void invocacionPorManaInsuficiente() {
        medium.setUbicacion(Bernal);
        medium.setMana(5);
        Integer mana = medium.getMana();
        medium.invocar(Casper);
        Integer nuevoMana = medium.getMana();
        assertEquals(mana, nuevoMana);
    }

    @Test
    void invocacionFallidaPorEspirituOcupado() {
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Bernal);
        medium.conectarseAEspiritu(Casper);
        assertThrows(NoSePuedenConectarException.class, () -> medium.conectarseAEspiritu(Casper));
    }


}

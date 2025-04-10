package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.exception.EspirituNoLibreException;
import ar.edu.unq.epersgeist.persistencia.dao.exception.NoSePuedenConectarException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MediumModeloTest {
    private Espiritu Casper;
    private Medium medium;
    private Medium medium2;
    private Ubicacion Bernal;
    private Ubicacion Quilmes;
    private Espiritu Lilith;


    @BeforeEach
    void setUp(){
        Casper = new Angel(0, "Casper");

        medium = new Medium("lala", 100, 50);
        medium2 = new Medium("lolo", 100, 60);

        Bernal = new Ubicacion("Bernal");
        Quilmes = new Ubicacion("Quilmes");
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
    void ConexionConEspirituAngelicalExitosa(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Bernal);
        medium.conectarseAEspiritu(Casper);
        assertTrue(medium.getEspiritus().contains(Casper));
        //assertFalse(Casper.estaLibre());
        assertNotNull(Casper.getMedium());
        assertEquals(Casper.getNivelDeConexion(), medium.getMana() * 20 / 100);
    }



    @Test
    void conexionConEspirituFallidaPorLibertad(){
        medium.setUbicacion(Bernal);
        medium2.setUbicacion(Bernal);
        Casper.setUbicacion(Bernal);
        medium.conectarseAEspiritu(Casper);
        assertThrows(NoSePuedenConectarException.class, () -> medium2.conectarseAEspiritu(Casper));
    };

    @Test
    void ConexionConEspirituFallidaPorUbicacion(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Quilmes);
        assertThrows(NoSePuedenConectarException.class, () -> medium.conectarseAEspiritu(Casper));
    };

    @Test
    void descansarAumentaMana() {
        Integer mana = medium.getMana();
        medium.descansar();
        Integer nuevoMana = medium.getMana();
        assertEquals(mana + 15, nuevoMana);
    }

    @Test
    void descansarNoAumentaMana() {
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
        medium.invocar(Casper);
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
        medium.setUbicacion(Quilmes);
        medium.invocar(Casper);
        assertThrows(EspirituNoLibreException.class, () -> medium.invocar(Casper));
    }


}

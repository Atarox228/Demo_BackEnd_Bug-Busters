package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Dado;
import ar.edu.unq.epersgeist.modelo.GeneradorNumeros;
import ar.edu.unq.epersgeist.modelo.ModoRandom;
import ar.edu.unq.epersgeist.modelo.ModoTrucado;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DadoTest {

    @Test
    void testMismaInsatncia() {
        GeneradorNumeros g = Dado.getInstance();
        GeneradorNumeros g2 = Dado.getInstance();

        assertEquals(g,g2);
    }

    @Test
    void testTrucado() {
        GeneradorNumeros g = Dado.getInstance();
        g.setModo(new ModoTrucado(6, 60));

        int numero1 = g.generarNumero(1, 10);
        int numero2 = g.generarNumero(1, 100);

        assertEquals(numero1, 6);
        assertEquals(numero2, 60);
    }


    @Test
    void testRandom(){
        GeneradorNumeros g = Dado.getInstance();
        g.setModo(new ModoRandom());

        int numero1 = g.generarNumero(1, 10);
        int numero2 = g.generarNumero(1, 100);


        assertTrue(1 <= numero1 & numero1 <= 10);
        assertTrue(1 <= numero1 & numero1 <= 100);
    }
}

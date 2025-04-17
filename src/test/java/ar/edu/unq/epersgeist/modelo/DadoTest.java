package ar.edu.unq.epersgeist.modelo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DadoTest {

    @Test
    void mismaInsatncia() {
        GeneradorNumeros g = Dado.getInstance();
        GeneradorNumeros g2 = Dado.getInstance();

        assertEquals(g,g2);
    }

    @Test
    void trucado() {
        GeneradorNumeros g = Dado.getInstance();
        g.setModo(new ModoTrucado(6, 60));

        int numero1 = g.generarNumero(1, 10);
        int numero2 = g.generarNumero(1, 100);

        assertEquals(numero1, 6);
        assertEquals(numero2, 60);
    }


    @Test
    void random(){
        GeneradorNumeros g = Dado.getInstance();
        g.setModo(new ModoRandom());

        int numero1 = g.generarNumero(1, 10);
        int numero2 = g.generarNumero(1, 100);


        assertTrue(1 <= numero1 & numero1 <= 10);
        assertTrue(1 <= numero1 & numero1 <= 100);
    }
}

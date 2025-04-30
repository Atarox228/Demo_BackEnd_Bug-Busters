package ar.edu.unq.epersgeist.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class EspirituModeloTest {

    private Espiritu angel;
    private Espiritu demonio;
    private Ubicacion cementerio;
    private Ubicacion santuario;

    @BeforeEach
    void setUp(){
        angel = new Angel("Gabriel");
        angel.setNivelConexion(15);

        demonio = new Demonio("Lucifer");
        demonio.setNivelConexion(15);

        santuario = new Santuario("santuario", 15);
        cementerio = new Cementerio("cementerio", 15);
    }


    @Test
    void verificarTipoEspirituAngelical(){
        assertEquals(TipoEspiritu.ANGELICAL, angel.getTipo());
    }

    @Test
    void verificarTipoEspirituDemoniaco(){
        assertEquals(TipoEspiritu.DEMONIACO, demonio.getTipo());
    }

    @Test
    void verificarPorDemonioCuandoEsAngel() {assertNotEquals(demonio.getTipo(), TipoEspiritu.ANGELICAL);}

//    @Test
//    void movimientoDeDemonioASantuarioPierde10DeConexion() {
//        demonio.moverseASantuario(santuario);
//        assertEquals(demonio.getNivelConexion(), 5);
//    }
//
//    @Test
//    void movimientoDeAngelACementerioPierde5DeConexion() {
//        angel.moverseACementerio(cementerio);
//        assertEquals(angel.getNivelConexion(), 10);
//    }
//
//    @Test
//    void movimientoDeDemonioACementerioNoPierdeConexion() {
//        demonio.moverseACementerio(cementerio);
//        assertEquals(demonio.getNivelConexion(), 15);
//    }
//
//    @Test
//    void movimientoDeAngelASantuarioNoPierdeConexion() {
//        angel.moverseASantuario(cementerio);
//        assertEquals(angel.getNivelConexion(), 15);
//    }
}

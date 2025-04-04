package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.TipoEspiritu;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class EspirituModeloTest {


    private Espiritu angel;
    private Espiritu demonio;

    @BeforeEach
    void setUp(){
        angel = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Gabriel");
        demonio = new Espiritu(TipoEspiritu.DEMONIACO, 0, "Lucifer");
    }

    @Test
    void verificarTipoEspirituAngelical(){
        assertEquals(angel.getTipo(), TipoEspiritu.ANGELICAL);
    }
    @Test
    void verificarTipoEspirituDemoniaco(){
        assertEquals(demonio.getTipo(), TipoEspiritu.DEMONIACO);
    }

}

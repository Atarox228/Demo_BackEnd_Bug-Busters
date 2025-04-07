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
    private Medium medium;
    private Ubicacion Bernal;

    @BeforeEach
    void setUp(){
        angel = new Espiritu(TipoEspiritu.ANGELICAL, 0, "Gabriel");
        demonio = new Espiritu(TipoEspiritu.DEMONIACO, 0, "Lucifer");

        medium = new Medium("lala", 100, 50);

        Bernal = new Ubicacion("Bernal");
    }

    @Test
    void testVerificarTipoEspirituAngelical(){
        assertEquals(angel.getTipo(), TipoEspiritu.ANGELICAL);
    }
    @Test
    void testVerificarTipoEspirituDemoniaco(){
        assertEquals(demonio.getTipo(), TipoEspiritu.DEMONIACO);
    }

    @Test
    void testInvocarme() {
        angel.setUbicacion(null);
        angel.setMedium(null);
        angel.invocarme(medium, Bernal);
        assertEquals(medium, angel.getMedium());
        assertEquals(Bernal, angel.getUbicacion());
  }

}

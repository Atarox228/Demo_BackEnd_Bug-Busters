package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.*;
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
        angel = new Angel(0, "Gabriel");
        demonio = new Demonio(0, "Lucifer");

        medium = new Medium("lala", 100, 50);

        Bernal = new Ubicacion("Bernal");
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
    @Test
    void invocarme() {
        angel.setUbicacion(null);
        angel.setMedium(null);
        angel.invocarme(medium, Bernal);
        assertEquals(medium, angel.getMedium());
        assertEquals(Bernal, angel.getUbicacion());
  }

}

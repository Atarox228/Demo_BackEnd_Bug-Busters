package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.exception.NoSePuedenConectarException;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateEspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.impl.HibernateMediumDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import ar.edu.unq.epersgeist.servicios.impl.MediumServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.BreakIterator;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MediumModeloTest {
    private EspirituService espirituService = new EspirituServiceImpl(new HibernateEspirituDAO(), new HibernateMediumDAO());
    private MediumService mediumService = new MediumServiceImpl(new HibernateMediumDAO());
    private Espiritu Casper;
    private Espiritu Jorge;
    private Medium medium;
    private Medium medium2;
    private Ubicacion Bernal;
    private Ubicacion Quilmes;



    @BeforeEach
    void setUp(){
        Casper = new Espiritu("Angelical", 0, "Casper");
        Jorge = new Espiritu("Humano", 20, "Jorge");

        medium = new Medium("lala", 100, 50);
        medium2 = new Medium("lolo", 100, 60);

        Bernal = new Ubicacion("Bernal");
        Quilmes = new Ubicacion("Quilmes");
    }


    @Test
    void Puedenconectarse(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Bernal);
        assertTrue(medium.puedeConectarse(Casper));
    };

    @Test
    void NoPuedenconectarsePorUbicacion(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Quilmes);
        assertFalse(medium.puedeConectarse(Casper));
    };

    @Test
    void NoPuedenconectarsePorLibertad(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Bernal);
        Casper.setMedium(medium2);
        assertFalse(medium.puedeConectarse(Casper));
    };

    @Test
    void ConectarseConEspiritu(){
        medium.setUbicacion(Bernal);
        Casper.setUbicacion(Bernal);
        medium.conectarseAEspiritu(Casper);
        assertTrue(medium.getEspiritus().contains(Casper));
        assertFalse(Casper.estaLibre());
        assertEquals(Casper.getNivelDeConexion(), medium.getMana() * 20 / 100);
    };

    @Test
    void ConexionConEspirituFallidaPorLibertad(){
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

}

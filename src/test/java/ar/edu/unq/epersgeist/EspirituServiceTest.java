package ar.edu.unq.epersgeist;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.impl.EspirituServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class EspirituServiceTest {



    @Test
    void crearEspiritu(){
        Espiritu espiritu = new Espiritu("Poltergeist", 0, "Caspy");
        EspirituService service = new EspirituServiceImpl();
        Espiritu espirituActualizado = service.crear(espiritu);
        assertNotEquals(espirituActualizado.getId(), null);

    }

}

package ar.edu.unq.epersgeist.service;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.service.dataService.DataService;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.exception.NoHaySantuariosConDemoniosException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EstadisticaServiceTest {

    @Autowired
    private DataService dataService;
    @Autowired
    private EstadisticaService estadisticaService;
    @Autowired
    private EspirituService espirituService;
    @Autowired
    private MediumService mediumService;
    @Autowired
    private UbicacionService ubicacionService;


    private Ubicacion fellwood;
    private Ubicacion ashenvale;
    private Espiritu demonio1;
    private Espiritu demonio2;
    private Medium medium1;
    private Medium medium2;
    private Espiritu demonio3;
    private Espiritu demonio4;
    private Ubicacion santuario;
    private Espiritu angel1;
    private Espiritu angel2;

    @BeforeEach
    public void prepare() {
        santuario = new Santuario("Catolistres", 50);
        fellwood = new Santuario("Fellwood", 100);
        ashenvale = new Cementerio("Ashenvale", 80);
        ubicacionService.crear(santuario);
        ubicacionService.crear(fellwood);
        ubicacionService.crear(ashenvale);

        demonio1 = new Demonio( "Casper");
        demonio2 = new Demonio("Marids");
        demonio3 = new Demonio("Julien");
        demonio4 = new Demonio("Fury");
        espirituService.crear(demonio1);
        espirituService.crear(demonio2);
        espirituService.crear(demonio3);
        espirituService.crear(demonio4);

        angel1 = new Angel( "Miguel");
        angel2 = new Angel("Gabriel");
        espirituService.crear(angel1);
        espirituService.crear(angel2);


        medium1 = new Medium("Fioren", 100, 50);
        medium2 = new Medium("Ernesto", 150, 30);
        mediumService.crear(medium1);
        mediumService.crear(medium2);
    }

    @Test
    void santuariosSinDemonios() {
        assertThrows(NoHaySantuariosConDemoniosException.class, () -> {
            estadisticaService.santuarioCorrupto();
        });
    }

    @Test
    void santuariosConDemoniosSinMedium() {
        demonio1.setUbicacion(santuario);
        demonio2.setUbicacion(santuario);
        espirituService.actualizar(demonio1);
        espirituService.actualizar(demonio2);
        assertThrows(NoSuchElementException.class, () -> {
            estadisticaService.santuarioCorrupto();
        });
    }

    @Test
    void santuariosConDemoniosConMediumSinDemonios() {
        demonio1.setUbicacion(santuario);
        demonio2.setUbicacion(santuario);
        espirituService.actualizar(demonio1);
        espirituService.actualizar(demonio2);
        medium1.setUbicacion(santuario);
        mediumService.actualizar(medium1);
        assertThrows(NoSuchElementException.class, () -> {
            estadisticaService.santuarioCorrupto();
        });
    }

    @Test
    @Transactional // DUDAS ACA, DE SI SE PUEDE PONER
    void santuarioConDemoniosConMediumConDemonios() {
        demonio1.setUbicacion(santuario);
        demonio2.setUbicacion(santuario);
        espirituService.actualizar(demonio1);
        espirituService.actualizar(demonio2);
        medium1.setUbicacion(santuario);
        mediumService.actualizar(medium1);
        espirituService.conectar(demonio1.getId(), medium1.getId());
        espirituService.conectar(demonio2.getId(), medium1.getId());
        ReporteSantuarioMasCorrupto reporteCorrupto = estadisticaService.santuarioCorrupto();
        assertEquals("Catolistres", reporteCorrupto.getSantuario());
        assertEquals(medium1, reporteCorrupto.getMedium());
        assertEquals(2, reporteCorrupto.getCantDemoniosTotal());
        assertEquals(0, reporteCorrupto.getCantDemoniosLibres());
    }

    @Test
    @Transactional
    void masDeUnSantuarioConMismaCantidadDeDemonios() {
        demonio1.setUbicacion(santuario);
        demonio2.setUbicacion(santuario);
        demonio3.setUbicacion(fellwood);
        demonio4.setUbicacion(fellwood);
        espirituService.actualizar(demonio1);
        espirituService.actualizar(demonio2);
        espirituService.actualizar(demonio3);
        espirituService.actualizar(demonio4);
        medium1.setUbicacion(santuario);
        medium2.setUbicacion(fellwood);
        mediumService.actualizar(medium1);
        mediumService.actualizar(medium2);
        espirituService.conectar(demonio1.getId(), medium1.getId());
        espirituService.conectar(demonio4.getId(), medium2.getId());
        espirituService.conectar(demonio3.getId(), medium2.getId());
        ReporteSantuarioMasCorrupto reporteCorrupto = estadisticaService.santuarioCorrupto();
        assertEquals("Fellwood", reporteCorrupto.getSantuario());
        // ACA SE DEMUESTRA QUE ELIJE POR ORDEN ALFABETICO YA QUE UNO ES F Y EL OTRO S
        assertEquals(medium2, reporteCorrupto.getMedium());
        assertEquals(2, reporteCorrupto.getCantDemoniosTotal());
        assertEquals(0, reporteCorrupto.getCantDemoniosLibres());
    }

    @Test
    @Transactional
    void masDeUnSantuarioConMediumsDeMismaCantDemonios() {
        demonio1.setUbicacion(santuario);
        demonio2.setUbicacion(santuario);
        demonio3.setUbicacion(fellwood);
        demonio4.setUbicacion(fellwood);
        espirituService.actualizar(demonio1);
        espirituService.actualizar(demonio2);
        espirituService.actualizar(demonio3);
        espirituService.actualizar(demonio4);
        medium1.setUbicacion(santuario);
        medium2.setUbicacion(fellwood);
        mediumService.actualizar(medium1);
        mediumService.actualizar(medium2);
        espirituService.conectar(demonio1.getId(), medium1.getId());
        espirituService.conectar(demonio3.getId(), medium2.getId());
        ReporteSantuarioMasCorrupto reporteCorrupto = estadisticaService.santuarioCorrupto();
        assertEquals("Fellwood", reporteCorrupto.getSantuario());
        assertEquals(medium2, reporteCorrupto.getMedium());
        assertEquals(2, reporteCorrupto.getCantDemoniosTotal());
        assertEquals(1, reporteCorrupto.getCantDemoniosLibres());
    }

    @Test
    @Transactional
    void santuarioCon2Demonios2AngelesyOtroCon1Demonio() {
        demonio1.setUbicacion(santuario);
        demonio2.setUbicacion(fellwood);
        demonio3.setUbicacion(fellwood);
        espirituService.actualizar(demonio1);
        espirituService.actualizar(demonio2);
        espirituService.actualizar(demonio3);
        angel1.setUbicacion(fellwood);
        angel2.setUbicacion(fellwood);
        espirituService.actualizar(angel1);
        espirituService.actualizar(angel2);
        medium1.setUbicacion(santuario);
        medium2.setUbicacion(fellwood);
        mediumService.actualizar(medium1);
        mediumService.actualizar(medium2);
        espirituService.conectar(demonio1.getId(), medium1.getId());
        espirituService.conectar(demonio3.getId(), medium2.getId());
        ReporteSantuarioMasCorrupto reporteCorrupto = estadisticaService.santuarioCorrupto();
        assertEquals("Catolistres", reporteCorrupto.getSantuario());
        assertEquals(medium1, reporteCorrupto.getMedium());
        assertEquals(1, reporteCorrupto.getCantDemoniosTotal());
        assertEquals(0, reporteCorrupto.getCantDemoniosLibres());
    }

    @Test
    @Transactional
    void santuarioCon1Angely1Demonio() {
        demonio1.setUbicacion(santuario);
        angel1.setUbicacion(santuario);
        espirituService.actualizar(demonio1);
        espirituService.actualizar(angel1);
        medium1.setUbicacion(santuario);
        mediumService.actualizar(medium1);
        espirituService.conectar(demonio1.getId(), medium1.getId());
        ReporteSantuarioMasCorrupto reporteCorrupto = estadisticaService.santuarioCorrupto();
        assertEquals("Catolistres", reporteCorrupto.getSantuario());
        assertEquals(medium1, reporteCorrupto.getMedium());
        assertEquals(1, reporteCorrupto.getCantDemoniosTotal());
        assertEquals(0, reporteCorrupto.getCantDemoniosLibres());

    }

    @AfterEach
    void cleanUp() {
        dataService.eliminarTodo();
    }
}

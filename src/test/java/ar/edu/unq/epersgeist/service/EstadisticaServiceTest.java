package ar.edu.unq.epersgeist.service;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.service.dataService.DataService;
import ar.edu.unq.epersgeist.servicios.*;
import ar.edu.unq.epersgeist.servicios.exception.NoHaySantuariosConDemoniosException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
    private Ubicacion cementerio;
    private Espiritu demonio1;
    private Espiritu demonio2;
    private Medium medium1;
    private Medium medium2;
    private Espiritu demonio3;
    private Espiritu demonio4;
    private Ubicacion santuario;
    private Espiritu angel1;
    private Espiritu angel2;
    private GeoJsonPolygon areaSantuario;
    private GeoJsonPolygon areaCemeterio;
    private GeoJsonPolygon areaFellwood;

    @BeforeEach
    public void prepare() {
        List<Point> area1 = List.of(
            new Point(-58.2730, -34.7210),
            new Point(-58.2700, -34.7230),
            new Point(-58.2680, -34.7200),
            new Point(-58.2730, -34.7210)
    );
        areaSantuario = new GeoJsonPolygon(area1);

        List<Point> area2 = List.of(
                new Point(-58.2630, -34.7070),
                new Point(-58.2600, -34.7090),
                new Point(-58.2580, -34.7060),
                new Point(-58.2630, -34.7070)
        );
        areaFellwood = new GeoJsonPolygon(area2);

        List<Point> area3 = List.of(
                new Point(-58.3610, -34.6600),
                new Point(-58.3590, -34.6620),
                new Point(-58.3570, -34.6590),
                new Point(-58.3610, -34.6600)
        );
        areaCemeterio = new GeoJsonPolygon(area3);

        santuario = new Santuario("Catolistres", 50);
        fellwood = new Santuario("Fellwood", 100);
        cementerio = new Cementerio("Ashenvale", 80);
        ubicacionService.crear(santuario, areaSantuario);
        ubicacionService.crear(fellwood, areaFellwood);
        ubicacionService.crear(cementerio, areaCemeterio);

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

    //Para no repetir codigo
    private void ubicacionesDeEspiritusYMediums() {
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
    void santuarioConDemoniosConMediumConDemonios() {
        ubicacionesDeEspiritusYMediums();
        espirituService.conectar(demonio1.getId(), medium1.getId());
        espirituService.conectar(demonio2.getId(), medium1.getId());
        Medium mediumEsperado = mediumService.recuperar(medium1.getId());
        ReporteSantuarioMasCorrupto reporteCorrupto = estadisticaService.santuarioCorrupto();
        assertEquals("Catolistres", reporteCorrupto.getSantuario());
        assertEquals(mediumEsperado.getId(), reporteCorrupto.getMedium().getId());
        assertEquals(mediumEsperado.getEspiritus().size(), reporteCorrupto.getMedium().getEspiritus().size());
        assertEquals(2, reporteCorrupto.getCantDemoniosTotal());
        assertEquals(0, reporteCorrupto.getCantDemoniosLibres());
    }

    @Test
    void masDeUnSantuarioConMismaCantidadDeDemonios() {
        ubicacionesDeEspiritusYMediums();
        espirituService.conectar(demonio1.getId(), medium1.getId());
        espirituService.conectar(demonio4.getId(), medium2.getId());
        espirituService.conectar(demonio3.getId(), medium2.getId());
        Medium mediumEsperado = mediumService.recuperar(medium1.getId());
        ReporteSantuarioMasCorrupto reporteCorrupto = estadisticaService.santuarioCorrupto();
        assertEquals("Catolistres", reporteCorrupto.getSantuario());
        // ACA SE DEMUESTRA QUE ELIJE POR ORDEN ALFABETICO YA QUE UNO ES C Y EL OTRO F
        assertEquals(mediumEsperado.getId(), reporteCorrupto.getMedium().getId());
        assertEquals(mediumEsperado.getEspiritus().size(), reporteCorrupto.getMedium().getEspiritus().size());
        assertEquals(2, reporteCorrupto.getCantDemoniosTotal());
        assertEquals(1, reporteCorrupto.getCantDemoniosLibres());
    }



    @Test
    void masDeUnSantuarioConMediumsDeMismaCantDemonios() {
        ubicacionesDeEspiritusYMediums();
        espirituService.conectar(demonio1.getId(), medium1.getId());
        espirituService.conectar(demonio3.getId(), medium2.getId());
        Medium mediumEsperado = mediumService.recuperar(medium1.getId());
        ReporteSantuarioMasCorrupto reporteCorrupto = estadisticaService.santuarioCorrupto();
        assertEquals("Catolistres", reporteCorrupto.getSantuario());
        assertEquals(mediumEsperado.getId(), reporteCorrupto.getMedium().getId());
        assertEquals(mediumEsperado.getEspiritus().size(), reporteCorrupto.getMedium().getEspiritus().size());
        assertEquals(2, reporteCorrupto.getCantDemoniosTotal());
        assertEquals(1, reporteCorrupto.getCantDemoniosLibres());
    }

    @Test
    void santuarioCon2Demonios2AngelesyOtroCon1Demonio() {
        ubicacionesDeEspiritusYMediums();
        demonio2.setUbicacion(fellwood);
        demonio4.setUbicacion(cementerio);
        espirituService.actualizar(demonio2);
        espirituService.actualizar(demonio4);
        angel1.setUbicacion(fellwood);
        angel2.setUbicacion(fellwood);
        espirituService.actualizar(angel1);
        espirituService.actualizar(angel2);
        espirituService.conectar(demonio1.getId(), medium1.getId());
        espirituService.conectar(demonio3.getId(), medium2.getId());
        Medium mediumEsperado = mediumService.recuperar(medium1.getId());
        ReporteSantuarioMasCorrupto reporteCorrupto = estadisticaService.santuarioCorrupto();
        assertEquals("Catolistres", reporteCorrupto.getSantuario());
        assertEquals(mediumEsperado.getId(), reporteCorrupto.getMedium().getId());
        assertEquals(mediumEsperado.getEspiritus().size(), reporteCorrupto.getMedium().getEspiritus().size());
        assertEquals(1, reporteCorrupto.getCantDemoniosTotal());
        assertEquals(0, reporteCorrupto.getCantDemoniosLibres());
    }

    @Test
    void santuarioCon1Angely1Demonio() {
        demonio1.setUbicacion(santuario);
        angel1.setUbicacion(santuario);
        espirituService.actualizar(demonio1);
        espirituService.actualizar(angel1);
        medium1.setUbicacion(santuario);
        mediumService.actualizar(medium1);
        espirituService.conectar(demonio1.getId(), medium1.getId());
        Medium mediumEsperado = mediumService.recuperar(medium1.getId());
        ReporteSantuarioMasCorrupto reporteCorrupto = estadisticaService.santuarioCorrupto();
        assertEquals("Catolistres", reporteCorrupto.getSantuario());
        assertEquals(mediumEsperado.getId(), reporteCorrupto.getMedium().getId());
        assertEquals(mediumEsperado.getEspiritus().size(), reporteCorrupto.getMedium().getEspiritus().size());
        assertEquals(1, reporteCorrupto.getCantDemoniosTotal());
        assertEquals(0, reporteCorrupto.getCantDemoniosLibres());
    }

    @Test 
    void cementerioCon2DemoniosySantuarioCon1Demonio() {
        demonio1.setUbicacion(cementerio);
        demonio2.setUbicacion(cementerio);
        demonio3.setUbicacion(santuario);
        espirituService.actualizar(demonio1);
        espirituService.actualizar(demonio2);
        espirituService.actualizar(demonio3);
        medium1.setUbicacion(cementerio);
        medium2.setUbicacion(santuario);
        mediumService.actualizar(medium1);
        mediumService.actualizar(medium2);
        espirituService.conectar(demonio1.getId(), medium1.getId());
        espirituService.conectar(demonio3.getId(), medium2.getId());
        Medium mediumEsperado = mediumService.recuperar(medium2.getId());
        ReporteSantuarioMasCorrupto reporteCorrupto = estadisticaService.santuarioCorrupto();
        assertEquals("Catolistres", reporteCorrupto.getSantuario());
        assertEquals(mediumEsperado.getId(), reporteCorrupto.getMedium().getId());
        assertEquals(mediumEsperado.getEspiritus().size(), reporteCorrupto.getMedium().getEspiritus().size());
        assertEquals(1, reporteCorrupto.getCantDemoniosTotal());
        assertEquals(0, reporteCorrupto.getCantDemoniosLibres());
    }

    @Test
    void ubicacionCementerioSinSantuario() {
        demonio1.setUbicacion(cementerio);
        espirituService.actualizar(demonio1);
        medium1.setUbicacion(cementerio);
        mediumService.actualizar(medium1);
        espirituService.conectar(demonio1.getId(), medium1.getId());
        assertThrows(NoHaySantuariosConDemoniosException.class, () -> {
            estadisticaService.santuarioCorrupto();
        });
    }

    @AfterEach
    void cleanUp() {
        dataService.eliminarTodo();
    }
}

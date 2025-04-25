package ar.edu.unq.epersgeist.modelo;

import ar.edu.unq.epersgeist.modelo.exception.NoEsSantuarioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReporteTest {
    private ReporteSantuarioMasCorrupto reporteCorrupto;
    private Espiritu casper;
    private Medium medium;
    private Medium medium2;
    private Ubicacion santuario;
    private Ubicacion cementerio;
    private Espiritu yokusa;
    private Espiritu lucief;

    @BeforeEach
    public void prepare() {
        reporteCorrupto = new ReporteSantuarioMasCorrupto();

        casper = new Angel("Casper");
        yokusa = new Demonio("Yokusa");
        lucief = new Demonio("Lucief");

        medium = new Medium("Francis", 120, 50);
        medium2 = new Medium("Christofer", 100, 60);

        santuario = new Ubicacion("Santuario");
        cementerio = new Ubicacion("Cementerio");

    }

    @Test
    void obtenerNombreUbicacion() {
        assertEquals(reporteCorrupto.nombreUbicacion(), "Santuario");
    }

    @Test
    void insertarNoSantuarioEnReporteSantuarioCorrupto() {
        assertThrows(NoEsSantuarioException.class, () -> new ReporteSantuarioMasCorrupto(cementerio));
    }
    // ESTO SEGURO ROMPA CUANDO LAS UBICACIONES TENGAN TIPO

    @Test
    void cantidadTotalDeDemonios() {
        lucief.setUbicacion(santuario);
        casper.setUbicacion(santuario);
        yokusa.setUbicacion(santuario);
        List<Espiritu> espiritus = List.of(casper,lucief,yokusa);
        assertEquals(2, reporteCorrupto.obtenerCantDemonios());
    }
//
//    @Test
//    void cantidadDemoniosLibres() {
//        lucief.setUbicacion(santuario);
//        casper.setUbicacion(santuario);
//        yokusa.setUbicacion(santuario);
//        lucief.setMedium(medium);
//        List<Espiritu> espiritus = List.of(casper,lucief,yokusa);
//        assertEquals(1, reporteCorrupto.obtenerDemoniosLibres(espiritus));
//    }


}

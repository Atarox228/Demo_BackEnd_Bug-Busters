package ar.edu.unq.epersgeist.service;

import ar.edu.unq.epersgeist.modelo.ReporteSantuarioMasCorrupto;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class EstadisticaServiceTest {

    @Autowired
    private EstadisticaService estadisticaService;

    @Autowired
    private EspirituService espirituService;

    @Autowired
    private MediumService mediumService;

    @Autowired
    private UbicacionService ubicacionService;

    @BeforeEach
    public void prepare() {

    }

//    @Test
//    void santuarioCorrupto() {
//        ReporteSantuarioMasCorrupto reporteCorrupto = estadisticaService.santuarioCorrupto();
//        assertEquals("Santuario", reporteCorrupto.nombreUbicacion());
//        assertEquals(medium, reporteCorrupto.getMedium());
//        assertEquals(4, reporteCorrupto.obtenerCantDemonios());
//        assertEquals(1, reporteCorrupto.obtenerDemoniosLibres());
//    }

}

package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.ReporteSanturarioMasCorruptoDTO;
import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import org.springframework.web.bind.annotation.GetMapping;

public class EstadisicaticaControllerREST {

    public final EstadisticaService estadisticaService;

    public EstadisicaticaControllerREST(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    @GetMapping("/santuario-corrupto")
    public ReporteSanturarioMasCorruptoDTO reporteSantuarioCorrupto() {
        return ReporteSanturarioMasCorruptoDTO.desdeModelo(estadisticaService.santuarioCorrupto());
    }
}

package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.ReporteSanturarioMasCorruptoDTO;
import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import ar.edu.unq.epersgeist.servicios.exception.NoHaySantuariosConDemoniosException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/estadistica")
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

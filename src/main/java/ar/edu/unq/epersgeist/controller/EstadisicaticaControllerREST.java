package ar.edu.unq.epersgeist.controller;

import ar.edu.unq.epersgeist.controller.dto.ReporteSanturarioMasCorruptoDTO;
import ar.edu.unq.epersgeist.controller.dto.SnapshotDTO;
import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    @PostMapping("/snapshot")
    public ResponseEntity<Void> crearSnapshot() {
        estadisticaService.snapshot();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/snapshot/{fecha}")
    public SnapshotDTO obtenerSnapshot(@PathVariable LocalDate fecha) {
        return SnapshotDTO.desdeModelo(estadisticaService.obtenerSnapshot(fecha));
    }


}

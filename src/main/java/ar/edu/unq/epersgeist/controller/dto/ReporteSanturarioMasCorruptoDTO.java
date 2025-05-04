package ar.edu.unq.epersgeist.controller.dto;

import ar.edu.unq.epersgeist.modelo.ReporteSantuarioMasCorrupto;

public record ReporteSanturarioMasCorruptoDTO(Long id, String santuario, MediumDTO medium, Integer cantDemonios, Integer cantDemoniosLibres) {

    public static ReporteSanturarioMasCorruptoDTO desdeModelo(ReporteSantuarioMasCorrupto reporte){
        return new ReporteSanturarioMasCorruptoDTO(
                reporte.getId(),
                reporte.getSantuario(),
                reporte.getMedium() != null ? MediumDTO.desdeModelo(reporte.getMedium()) : null,
                reporte.getCantDemoniosTotal(),
                reporte.getCantDemoniosLibres()
        );
    }
}

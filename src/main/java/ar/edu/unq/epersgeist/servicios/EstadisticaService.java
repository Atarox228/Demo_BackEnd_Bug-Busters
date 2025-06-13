package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.*;

import java.time.LocalDate;

public interface EstadisticaService {
    ReporteSantuarioMasCorrupto santuarioCorrupto();

    void snapshot();

    SnapShot obtenerSnapshot(LocalDate date);
}

package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.ReporteSantuarioMasCorrupto;

public interface EstadisticaService {
    ReporteSantuarioMasCorrupto santuarioCorrupto();
    boolean sinSanturariosCorruptos();
}

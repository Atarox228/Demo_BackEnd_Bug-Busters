package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.CoordenadaMongo;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

public interface CoordenadaService {
    void actualizarOCrearCoordenada(String entityType, Long entityId, GeoJsonPoint punto);
    void actualizarCoordenadas(String entityType, List<Long> entityId, GeoJsonPoint punto);
    CoordenadaMongo findByEntityIdAndEntityType(String entityType, Long entityId);
}

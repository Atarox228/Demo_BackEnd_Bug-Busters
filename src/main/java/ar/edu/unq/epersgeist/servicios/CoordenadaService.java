package ar.edu.unq.epersgeist.servicios;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

public interface CoordenadaService {
    void actualizarCoordenada(String entityType, Long entityId, GeoJsonPoint punto);

    void actualizarCoordenadas(String entityType, List<Long> entityId, GeoJsonPoint punto);
}

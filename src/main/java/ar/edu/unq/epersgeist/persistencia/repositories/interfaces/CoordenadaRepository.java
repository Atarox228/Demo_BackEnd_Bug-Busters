package ar.edu.unq.epersgeist.persistencia.repositories.interfaces;

import ar.edu.unq.epersgeist.modelo.CoordenadaMongo;
import ar.edu.unq.epersgeist.modelo.Medium;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;
import java.util.Optional;

public interface CoordenadaRepository {
    void actualizarOCrearCoordenada(String entityType, Long entityId, GeoJsonPoint punto);
    void actualizarCoordenadas(String entityType, List<Long> entityIds, GeoJsonPoint punto);
    void actualizarCoordenada(CoordenadaMongo coordenadaMongo);
    CoordenadaMongo findByEntityIdAndEntityType(String entityType, Long entityId);
    Optional<CoordenadaMongo> findCoordenadaEnRangoInvocar(Double longitud, Double latitud, Long id, String entityType);
    boolean estaEnRangoDeInvocar(CoordenadaMongo coordenadaEspiritu, Medium medium);
    boolean estaEnRangoDeMover(Long id, Double longitud, Double latitud);
}

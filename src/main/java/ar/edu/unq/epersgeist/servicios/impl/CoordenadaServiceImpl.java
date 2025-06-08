package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.CoordenadaMongo;
import ar.edu.unq.epersgeist.persistencia.dao.CoordenadaDAOMongo;
import ar.edu.unq.epersgeist.servicios.CoordenadaService;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CoordenadaServiceImpl implements CoordenadaService {

    private final CoordenadaDAOMongo coordenadaDAOMongo;

    public CoordenadaServiceImpl(CoordenadaDAOMongo coordenadaDAOMongo) {
        this.coordenadaDAOMongo = coordenadaDAOMongo;
    }

    @Override
    public void actualizarCoordenada(String entityType, Long entityId, GeoJsonPoint punto) {
        CoordenadaMongo coordenada = coordenadaDAOMongo.findByEntityTypeAndEntityId(entityType, entityId)
                .orElseGet(() -> new CoordenadaMongo(punto, entityType, entityId));
        coordenada.setPunto(punto);
        coordenadaDAOMongo.save(coordenada);
    }

    @Override
    public void actualizarCoordenadas(String entityType, List<Long> entityIds, GeoJsonPoint punto) {
        List<CoordenadaMongo> coordenadas = coordenadaDAOMongo.findByEntityTypeAndEntityIdIn(entityType, entityIds);
        if (coordenadas.isEmpty()) {
            coordenadas = entityIds.stream()
                    .map(id -> new CoordenadaMongo(punto, entityType, id))
                    .collect(Collectors.toList());
        } else {
            coordenadas.forEach(c -> c.setPunto(punto));
        }
        coordenadaDAOMongo.saveAll(coordenadas);
    }
}

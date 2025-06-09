package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.CoordenadaMongo;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.CoordenadaRepository;
import ar.edu.unq.epersgeist.servicios.CoordenadaService;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CoordenadaServiceImpl implements CoordenadaService {

    private final CoordenadaRepository  coordenadaRepository;

    public CoordenadaServiceImpl(CoordenadaRepository coordenadaRepository) {
        this.coordenadaRepository = coordenadaRepository;
    }

    @Override
    public void actualizarOCrearCoordenada(String entityType, Long entityId, GeoJsonPoint punto) {
        coordenadaRepository.actualizarOCrearCoordenada(entityType, entityId, punto);
    }

    @Override
    public void actualizarCoordenadas(String entityType, List<Long> entityIds, GeoJsonPoint punto) {
        coordenadaRepository.actualizarCoordenadas(entityType, entityIds, punto);
    }

}

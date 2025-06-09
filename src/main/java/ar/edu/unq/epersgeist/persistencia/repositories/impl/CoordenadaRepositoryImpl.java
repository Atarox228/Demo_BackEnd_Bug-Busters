package ar.edu.unq.epersgeist.persistencia.repositories.impl;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.CoordenadaMongo;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.CoordenadaDAOMongo;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.CoordenadaRepository;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CoordenadaRepositoryImpl implements CoordenadaRepository {

    private final CoordenadaDAOMongo coordenadaDAOMongo;

    public CoordenadaRepositoryImpl(CoordenadaDAOMongo coordenadaDAOMongo){
        this.coordenadaDAOMongo = coordenadaDAOMongo;
    }

    @Override
    public void actualizarOCrearCoordenada(String entityType, Long entityId, GeoJsonPoint punto) {
        CoordenadaMongo coordenada = coordenadaDAOMongo.findByEntityTypeAndEntityId(entityType, entityId)
                .orElseGet(() -> new CoordenadaMongo(punto, entityType, entityId));
        coordenada.setPunto(punto);
        coordenadaDAOMongo.save(coordenada);
    }

    @Override
    public void actualizarCoordenadas(String entityType, List<Long> entityIds, GeoJsonPoint punto) {
        List<CoordenadaMongo> coordenadas = coordenadaDAOMongo.findByEntityTypeAndEntityIdIn(entityType, entityIds);
        coordenadas.forEach(c -> c.setPunto(punto));
        coordenadaDAOMongo.saveAll(coordenadas);
    }

    @Override
    public void actualizarCoordenada(CoordenadaMongo coordenadaMongo) {
        coordenadaDAOMongo.save(coordenadaMongo);
    }

    @Override
    public CoordenadaMongo findByEntityIdAndEntityType(String entityType, Long entityId){
        return coordenadaDAOMongo.findByEntityIdAndEntityType(entityId, entityType)
                .orElseThrow(() -> new RecursoNoEncontradoException("Coordenada no encontrada"));
    }

    @Override
    public Optional<CoordenadaMongo> findCoordenadaEnRangoInvocar(Double longitud, Double latitud, Long id, String entityType){
        return coordenadaDAOMongo.findCoordenadaEnRangoInvocar(longitud, latitud, id, entityType);
    }

    @Override
    public boolean estaEnRangoDeInvocar(CoordenadaMongo coordenadaEspiritu, Medium medium){
        Double latitud = coordenadaEspiritu.getLatitud();
        Double longitud = coordenadaEspiritu.getLongitud();

        Optional<CoordenadaMongo> coordenadaEnRango = this.findCoordenadaEnRangoInvocar(
                longitud, latitud, medium.getId(), medium.getClass().toString());

        return coordenadaEnRango.isPresent();
    }

    @Override
    public boolean estaEnRangoDeMover(Long id, Double longitud, Double latitud){
        Optional<CoordenadaMongo> coordenadasDeMedium = coordenadaDAOMongo.findCercana("MEDIUM", id, longitud,  latitud, 30000D);
        return !coordenadasDeMedium.isEmpty();
    }
}

package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.AreaMongo;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AreaDAOMongo extends MongoRepository<AreaMongo, String>{

    @Query("{ 'poligono': { $geoIntersects: { $geometry: ?0 } } }")
    List<AreaMongo> recuperarPorInterseccion(GeoJsonPolygon poligono);


    @Query("{ 'poligono': { $geoIntersects: { $geometry: ?0 } } }")
    Optional<AreaMongo> recuperarPorCoordenada(GeoJsonPoint coordenada);
}

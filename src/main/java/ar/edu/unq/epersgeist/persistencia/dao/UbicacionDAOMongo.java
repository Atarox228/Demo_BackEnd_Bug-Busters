package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.UbicacionMongo;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UbicacionDAOMongo extends MongoRepository<UbicacionMongo, String>{

    @Query("{ 'area': { $geoIntersects: { $geometry: ?0 } } }")
    List<UbicacionMongo> recuperarPorInterseccion(GeoJsonPolygon area);

    @Query("{ 'area': { $geoIntersects: { $geometry: ?0 } } }")
    Optional<UbicacionMongo> recuperarPorCoordenada(GeoJsonPoint coordenada);

    @Query("{ 'nombre': ?0 }")
    Optional<UbicacionMongo> findByNombreMongo(String nombre);
}
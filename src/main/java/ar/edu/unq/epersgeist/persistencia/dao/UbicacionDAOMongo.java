package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.UbicacionMongo;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

public interface UbicacionDAOMongo extends MongoRepository<UbicacionMongo, String>{

    @Query("{ 'area': { $geoIntersects: { $geometry: ?0 } } }")
    List<UbicacionMongo> recuperarPorInterseccion(GeoJsonPolygon area);

    @Query("{ 'area': { $geoIntersects: { $geometry: ?0 } } }")
    Optional<UbicacionMongo> recuperarPorCoordenada(GeoJsonPoint coordenada);

    @Query("{ 'nombre': ?0, 'area': { $geoIntersects: { $geometry: ?1 } } }")
    Optional<UbicacionMongo> estaDentroDelArea(String nombreUbicacion, GeoJsonPoint coordenadaDestino);

    @Aggregation(pipeline = {
            "{ $geoNear: {",
            "   near: ?0,",
            "   distanceField: 'distancia',",
            "   spherical: true",
            "}}",
            "{ $match: { 'coordenada': ?1 } }",
            "{ $project: { _id: 0, distancia: 1 } }"
    })
    Optional<Double> distanciaEntre(GeoJsonPoint punto1, GeoJsonPoint punto2);
}
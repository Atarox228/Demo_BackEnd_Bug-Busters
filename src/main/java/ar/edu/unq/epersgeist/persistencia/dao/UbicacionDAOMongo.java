package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.UbicacionMongo;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface UbicacionDAOMongo extends MongoRepository<UbicacionMongo, String>{

    @Query("{ 'area': { $geoIntersects: { $geometry: ?0 } } }")
    List<UbicacionMongo> recuperarPorInterseccion(GeoJsonPolygon area);
}
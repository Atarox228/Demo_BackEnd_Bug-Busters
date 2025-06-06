package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.AreaMongo;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface AreaDAOMongo extends MongoRepository<AreaMongo, String>{

    @Query("{ 'poligono': { $geoIntersects: { $geometry: ?0 } } }")
    Optional<AreaMongo> recuperarPorCoordenada(GeoJsonPoint coordenada);
}

package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.EspirituMongo;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface EspirituDAOMongo extends MongoRepository<EspirituMongo, String> {

    @Query("""
    {
      'coordenada': {
        $near: {
          $geometry: {
            type: 'Point',
            coordinates: [?0, ?1]
          },
          $minDistance: ?2,
          $maxDistance: ?3
        }
      }
    }
    """)
    List<EspirituMongo> findEspirituEnRango(double x, double y, double i, double i1);
}

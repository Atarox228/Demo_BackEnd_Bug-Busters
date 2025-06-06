package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.EspirituMongo;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

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
      },
      '_id': ?4
    }
    """)
    Optional<EspirituMongo> findEspirituEnRango(double x, double y, double i, double i1, String idDominador);
}

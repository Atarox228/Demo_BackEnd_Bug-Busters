package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.CoordenadaMongo;
import ar.edu.unq.epersgeist.modelo.EspirituMongo;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.MediumMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CoordenadaDAOMongo extends MongoRepository<CoordenadaMongo, String> {


    Optional<CoordenadaMongo> findByEntityIdAndEntityType(Long entityId, String entityType);

    @Query("""
    {
      'punto': {
        $near: {
          $geometry: {
            type: 'Point',
            coordinates: [?0, ?1]
          },
          $maxDistance: ?3
        }
      },
      'entityId': ?2,
      'entityType': 'Medium'
    }
    """)
    Optional<CoordenadaMongo> findMediumEntreLosKms(Double longitud, Double latitud, Long id, Double distance);

    @Query("""
    {
      'punto': {
        $near: {
          $geometry: {
            type: 'Point',
            coordinates: [?0, ?1]
          },
          $minDistance: 2000,
          $maxDistance: 5000
        }
      },
      'entityId': ?2,
      'entityType': ?3
    }
    """)
    Optional<EspirituMongo> findEspirituEnRango(Double longitud, Double latitud, Long id, String entityType);

    @Query("""
    {
      'punto': {
        $near: {
          $geometry: {
            type: 'Point',
            coordinates: [?0, ?1]
          },
          $maxDistance: 50000
        }
      },
      'entityId': ?2,
      'entityType': ?3
    }
    """)
    Optional<EspirituMongo> findEspirituEnRangoInvocar(Double longitud, Double latitud, Long id, String entityType);
}

package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.CoordenadaMongo;
import ar.edu.unq.epersgeist.modelo.EspirituMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

public interface CoordenadaDAOMongo extends MongoRepository<CoordenadaMongo, String> {

    Optional<CoordenadaMongo> findByEntityIdAndEntityType(Long entityId, String entityType);
    Optional<CoordenadaMongo> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<CoordenadaMongo> findByEntityTypeAndEntityIdIn(String entityType, List<Long> entityIds);

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
    Optional<CoordenadaMongo> findEspirituEnRango(Double longitud, Double latitud, Long id, String entityType);

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
    Optional<CoordenadaMongo> findCoordenadaEnRangoInvocar(Double longitud, Double latitud, Long id, String entityType);

    @Query("""
    {
      "punto": {
        "$near": {
          "$geometry": {
            "type": "Point",
            "coordinates": [?2, ?3]
          },
          "$maxDistance": ?4
        }
      },
      "entityId": ?1,
      "entityType": ?0
    }
    """)
    Optional<CoordenadaMongo> findCercana(String entityType, Long entityId, Double longitud, Double latitud, Double maxDistance);

    List<CoordenadaMongo> findAll();
}

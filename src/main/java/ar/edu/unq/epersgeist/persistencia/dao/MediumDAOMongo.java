package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.MediumMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface MediumDAOMongo extends MongoRepository<MediumMongo, String> {

    Optional<MediumMongo> findByMediumIdSQL(Long mediumIdSQL);

    @Query(value = "{ 'mediumIdSQL' : ?0 }", fields = "{ 'coordenada' : 1 }")
    Optional<MediumMongo> recuperarSoloCoordenada(Long mediumIdSQL);
}

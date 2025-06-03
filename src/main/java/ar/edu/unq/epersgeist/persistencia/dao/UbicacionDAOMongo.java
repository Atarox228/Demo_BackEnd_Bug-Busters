package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.UbicacionMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UbicacionDAOMongo extends MongoRepository<UbicacionMongo, String>{

    @Query("{nombre:'?0'}")
    List<Ubicacion> recuperarPorNombre(String nombre);

}

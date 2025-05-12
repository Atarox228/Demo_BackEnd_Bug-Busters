package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.UbicacionNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.Optional;

public interface UbicacionDAONeo4j extends Neo4jRepository<UbicacionNeo4J, Long> {
    @Query("MATCH(u: Ubicacion) DETACH DELETE g")
    void detachDelete();

    @Query("MATCH(u: Ubicacion {nombre: $nombre }) RETURN u")
    Optional<UbicacionNeo4J> findByNombre(String nombre);
}

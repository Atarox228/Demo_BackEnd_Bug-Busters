package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.UbicacionNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UbicacionDAONeo4j extends Neo4jRepository<UbicacionNeo4J, Long> {
    @Query("MATCH(u: UbicacionNeo4J) DETACH DELETE u")
    void detachDelete();

    @Query("MATCH(u: UbicacionNeo4J {nombre: $nombre }) RETURN u")
    Optional<UbicacionNeo4J> findByNombre(@Param("nombre") String nombre);
}
package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.UbicacionNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface UbicacionDAONeo4j extends Neo4jRepository<UbicacionNeo4J, Long> {
    @Query("MATCH(u: Ubicacion) DETACH DELETE u")
    void detachDelete();

    @Query("MATCH(u: Ubicacion {nombre: $nombre }) RETURN u")
    Optional<UbicacionNeo4J> findByNombre(String nombre);

    @Query("MATCH (u:Ubicacion) WHERE u.flujoEnergia > $umbralDeEnergia RETURN u")
    List<UbicacionNeo4J> ubicacionesSobrecargadas(Integer umbralDeEnergia);
}

package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.UbicacionNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface UbicacionDAONeo4j extends Neo4jRepository<UbicacionNeo4J, Long> {
    @Query("MATCH(u: UbicacionNeo4J) DETACH DELETE u")
    void detachDelete();

    @Query("MATCH(u: UbicacionNeo4J {nombre: $nombre }) RETURN u")
    Optional<UbicacionNeo4J> findByNombre(@Param("nombre") String nombre);
    //No comento la de arriba ya que la utiliza Eliminar

    @Query("MATCH (u:UbicacionNeo4J {nombre: $nombre}) OPTIONAL MATCH (u)-[r]->(n) RETURN u, collect(r), collect(n)")
    Optional<UbicacionNeo4J> findByNombreWithRelations(String nombre);

    @Query("""
        MATCH(p: UbicacionNeo4J {nombre: $nombre })
        MATCH(p)-[CONECTADA]->(p2)
        RETURN p2
    """)
    Collection<UbicacionNeo4J> ubicacionesConectadas(@Param("nombre") String nombre);

}
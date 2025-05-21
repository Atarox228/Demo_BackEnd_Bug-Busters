package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.UbicacionNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UbicacionDAONeo4j extends Neo4jRepository<UbicacionNeo4J, Long> {
    @Query("MATCH(u: UbicacionNeo4J) DETACH DELETE u")
    void detachDelete();
    Optional<UbicacionNeo4J> findByNombre(String nombre);

    @Query("""
        MATCH (a:UbicacionNeo4J {nombre: $origenNombre})
        MATCH (b:UbicacionNeo4J {nombre: $destinoNombre})
        MERGE (a)-[:CONECTADA]->(b)
    """)
    void conectarUbicaciones(@Param("origenNombre") String origenNombre, @Param("destinoNombre") String destinoNombre);

    @Query("""
        MATCH (a:UbicacionNeo4J {nombre: $origen})
        MATCH (b:UbicacionNeo4J {nombre: $destino})
        RETURN COUNT { (a)-[:CONECTADA]->(b) } > 0
    """)
    boolean estanConectadasDirecta(@Param("origen") String origen, @Param("destino") String destino);

    @Query("MATCH (u:UbicacionNeo4J) WHERE u.flujoEnergia > $umbralDeEnergia RETURN u")
    List<UbicacionNeo4J> ubicacionesSobrecargadas(@Param("umbralDeEnergia") Integer umbralDeEnergia);

    @Query("""
        MATCH (a:UbicacionNeo4J {nombre: $origen})
        MATCH (b:UbicacionNeo4J {nombre: $destino})
        MATCH camino = shortestPath((a)-[:CONECTADA*1..]->(b))
        RETURN nodes(camino)
    """)
    List<UbicacionNeo4J> encontrarCaminoMasCorto(@Param("origen") String origen, @Param("destino") String destino);

    @Query("MATCH (u:UbicacionNeo4J {nombre: $nombre}) RETURN u")
    UbicacionNeo4J recuperarPorNombre(@Param("nombre") String nombre);
}
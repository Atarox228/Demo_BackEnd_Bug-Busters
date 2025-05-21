package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.UbicacionNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UbicacionDAONeo4j extends Neo4jRepository<UbicacionNeo4J, Long> {
    @Query("MATCH(u: Ubicacion) DETACH DELETE u")
    void detachDelete();
    Optional<UbicacionNeo4J> findByNombre(String nombre);

    @Query("""
        MATCH (a:Ubicacion {nombre: $origenNombre})
        MATCH (b:Ubicacion {nombre: $destinoNombre})
        MERGE (a)-[:CONECTADA]->(b)
    """)
    void conectarUbicaciones(@Param("origenNombre") String origenNombre, @Param("destinoNombre") String destinoNombre);

    @Query("""
        MATCH (a:Ubicacion {nombre: $origen})
        MATCH (b:Ubicacion {nombre: $destino})
        RETURN COUNT { (a)-[:CONECTADA]->(b) } > 0
    """)
    boolean estanConectadasDirecta(@Param("origen") String origen, @Param("destino") String destino);

    @Query("MATCH (u:Ubicacion) WHERE u.flujoEnergia > $umbralDeEnergia RETURN u")
    List<UbicacionNeo4J> ubicacionesSobrecargadas(@Param("umbralDeEnergia") Integer umbralDeEnergia);

    @Query("""
        MATCH (a:Ubicacion {nombre: $origen})
        MATCH (b:Ubicacion {nombre: $destino})
        MATCH camino = shortestPath((a)-[:CONECTADA*1..]->(b))
        RETURN nodes(camino)
    """)
    List<UbicacionNeo4J> encontrarCaminoMasCorto(@Param("origen") String origen, @Param("destino") String destino);

    @Query("MATCH (u:Ubicacion {nombre: $nombre}) RETURN u")
    UbicacionNeo4J recuperarPorNombre(@Param("nombre") String nombre);
}



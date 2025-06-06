package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.DegreeQuery;
import ar.edu.unq.epersgeist.modelo.ClosenessResult;
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
            MATCH (u:Ubicacion {nombre: $nombreViejo})
            SET u.nombre = $nombreNuevo,
                u.flujoEnergia = $energia
        """)
        void actualizarNeo(@Param("nombreViejo")String nombreViejo,@Param("nombreNuevo")String nombre, @Param("energia") Integer energia);

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

    //    @Query("""
    //         MATCH (u:Ubicacion)
    //         WHERE u.flujoEnergia > $umbralDeEnergia
    //         RETURN u
    //    """)
    @Query("""
         MATCH (u:Ubicacion)
         WHERE u.flujoEnergia > $umbralDeEnergia
         OPTIONAL MATCH (u)-[r:CONECTADA]->(destino:Ubicacion)
         RETURN u, collect(r), collect(destino)
    """)
    List<UbicacionNeo4J> ubicacionesSobrecargadas(@Param("umbralDeEnergia") Integer umbralDeEnergia);

    @Query("""
        MATCH (a:Ubicacion {nombre: $origen})
        MATCH (b:Ubicacion {nombre: $destino})
        MATCH camino = shortestPath((a)-[:CONECTADA*1..]->(b))
        WITH nodes(camino) AS nodos, relationships(camino) AS relaciones
        UNWIND nodos AS u
        OPTIONAL MATCH (u)-[r:CONECTADA]->(destino:Ubicacion)
        RETURN u, collect(r), collect(destino)
    """)
    List<UbicacionNeo4J> encontrarCaminoMasCorto(@Param("origen") String origen, @Param("destino") String destino);

    @Query ("""
        MATCH (a:Ubicacion {nombre: $nombre}), (b:Ubicacion)
        WHERE a <> b
        OPTIONAL MATCH p = shortestPath((a)-[:CONECTADA*1..]->(b))
        RETURN a AS ubicacion, (1.0 / (sum(CASE WHEN p IS NULL THEN 0 ELSE length(p) END) + (count(b) - count(p)) * 10)) AS closeness
    """)
    ClosenessResult closenessResult(@Param("nombre") String nombre);

    @Query("""
            MATCH (n:Ubicacion) -[r:CONECTADA]-> ()
            RETURN count(r)
            """)
    double relationships();

    @Query("""
            MATCH (n:Ubicacion) -[r:CONECTADA]-> ()
            WHERE n.nombre IN $names
            RETURN n AS node, count(r) AS degree
            ORDER BY count(r) DESC
            LIMIT 1
            """)
    DegreeQuery degreeOutcommingOf(@Param("names") List<String> names);

    @Query("""
            MATCH (n:Ubicacion) <-[r:CONECTADA]- ()
            WHERE n.nombre IN $names
            RETURN n AS node, count(r) AS degree
            ORDER BY count(r) DESC
            LIMIT 1
            """)
    DegreeQuery degreeIncommingOf(@Param("names") List<String> names);

    @Query("""
            MATCH (n:Ubicacion) -[r:CONECTADA]- ()
            WHERE n.nombre IN $names
            RETURN n AS node, count(r) AS degree
            ORDER BY count(r) DESC
            LIMIT 1
            """)
    DegreeQuery degreeAllOf(@Param("names") List<String> names);
}
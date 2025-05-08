package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediumDAO extends JpaRepository<Medium, Long> {

    @Query("from Espiritu e where e.medium.id = :mediumId AND e.deleted = false order by e.nombre asc")
    List<Espiritu> obtenerEspiritus(@Param("mediumId") Long mediumId);

    @Query(
            "SELECT m FROM Medium m where m.id NOT IN (SELECT e.medium.id FROM Espiritu e WHERE e.medium.id IS NOT NULL) AND m.deleted = false AND m.ubicacion.id = :ubicacionId AND m.ubicacion.deleted = false")
    List<Medium> mediumsSinEspiritusEn(@Param("ubicacionId")Long ubicacionId);

    @Query(
            "SELECT m FROM Medium m JOIN m.espiritus e WHERE TYPE(e) = Demonio AND m.ubicacion.id = :ubicacionId AND e.deleted = false GROUP BY m ORDER BY COUNT(e) DESC LIMIT 1"
    )
    Optional<Medium> mediumConMasDemoniosEn(@Param("ubicacionId")Long ubicacionId);

    @Query("SELECT m FROM Medium m WHERE m.deleted = false ORDER BY m.nombre ASC")
    List<Medium> recuperarTodosNoEliminados();

    @Query("SELECT m FROM Medium m WHERE m.deleted = false and m.ubicacion.id = :ubicacionId ORDER BY m.nombre ASC")
    List<Medium> mediumsEn(@Param("ubicacionId")Long ubicacionId);
}

package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EspirituDAO extends JpaRepository<Espiritu, Long> {
    @Query("SELECT e FROM Espiritu e WHERE TYPE(e) = Demonio AND e.deleted = false")
    Page<Espiritu> findDemonios(Pageable pageable);

    @Query(
            "SELECT e FROM Espiritu e WHERE e.ubicacion IS NOT NULL AND e.ubicacion.id = :ubicacionId AND e.deleted = false"
    )
    List<Espiritu> espiritusEn(@Param("ubicacionId") Long ubicacionId);

    @Query("from Espiritu e where e.medium.id = :id and type(e) = :tipo AND e.deleted = false")
    List<Espiritu> recuperarEspiritusDeTipo(@Param("id") Long id, @Param("tipo") Class<? extends Espiritu> tipo);

    // DEBO CAMBIAR CUANDO UBICACION TENGA TIPO
    @Query("SELECT e.ubicacion FROM Espiritu e " +
            "WHERE TYPE(e.ubicacion) = Santuario and e.ubicacion.deleted = FALSE GROUP BY e.ubicacion " +
            "ORDER BY  (SUM(CASE WHEN TYPE(e) = Demonio THEN 1 ELSE 0 END) - SUM(CASE WHEN TYPE(e) = Angel THEN 1 ELSE 0 END)) DESC," +
            " e.ubicacion.nombre ASC")
    List<Ubicacion> santuariosCorruptos();

    @Query(
            "SELECT e FROM Espiritu e WHERE e.ubicacion IS NOT NULL AND e.ubicacion.id = :ubicacionId AND TYPE(e) = Demonio AND e.deleted = false"
    )
    List<Object> demoniosEn(@Param("ubicacionId")Long ubicacionId);

    @Query(
            "SELECT e FROM Espiritu e WHERE e.ubicacion IS NOT NULL AND e.ubicacion.id = :ubicacionId AND e.deleted = false AND TYPE(e) = Demonio AND e.medium = NULL"
    )
    List<Object> demoniosLibresEn(@Param("ubicacionId") Long ubicacionId);

    @Query("SELECT e FROM Espiritu e WHERE e.deleted = false ORDER BY e.nombre ASC")
    List<Espiritu> recuperarTodosNoEliminados();
}


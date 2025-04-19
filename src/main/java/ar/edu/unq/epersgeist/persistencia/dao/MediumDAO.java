package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Collection;

@Repository
public interface MediumDAO extends JpaRepository<Medium, Long> {

    @Query("from Espiritu e where e.medium.id = :mediumId")
    List<Espiritu> obtenerEspiritus(@Param("mediumId") Long mediumId);

}

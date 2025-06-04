package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UbicacionDAO extends JpaRepository<Ubicacion, Long> {

    @Query("SELECT u FROM Ubicacion u WHERE u.deleted = false ORDER BY u.nombre ASC")
    List<Ubicacion> recuperarTodosNoEliminados();


    @Query("SELECT u FROM Ubicacion u WHERE u.deleted = false AND u.nombre = :nombreUbicacion")
    Ubicacion existeUbicacionConNombre(@Param("nombreUbicacion")String nombre);

    @Query("SELECT u FROM Ubicacion u WHERE u.nombre = :nombreUbicacion")
    Ubicacion recuperarPorNombre(@Param("nombreUbicacion")String nombre);
}

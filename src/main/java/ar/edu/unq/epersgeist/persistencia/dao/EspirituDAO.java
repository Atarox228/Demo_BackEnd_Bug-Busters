package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

//public interface EspirituDAO {
//
//
//    void guardar(Espiritu espiritu);
//    Espiritu recuperar(Long idDelEspiritu);
//    List<Espiritu> recuperarTodos();
//    void actualizar(Espiritu espiritu);
//    void eliminar(Espiritu espiritu);
//    void eliminarTodo();
//    List<Espiritu> obtenerDemonios(Direccion direccion, Integer pagina, Integer cantidadPorPagina);
//    List<Espiritu> espiritusEn(Long ubicacionId);
//    void actualizarEspiritus(List<Espiritu> espiritus);
//    List<Espiritu> recuperarEspiritusDeTipo(Long id, Class<? extends Espiritu> tipoEspiritu);
//
//}
@Repository
public interface EspirituDAO extends JpaRepository<Espiritu, Long> {
    @Query("SELECT e FROM Espiritu e WHERE TYPE(e) = Demonio")
    Page<Espiritu> findDemonios(Pageable pageable);

    @Query(
            "SELECT e FROM Espiritu e WHERE e.ubicacion IS NOT NULL AND e.ubicacion.id = :ubicacionId"
    )
    List<Espiritu> espiritusEn(@Param("ubicacionId") Long ubicacionId);

    @Query("from Espiritu e where e.medium.id = :id and type(e) = :tipo")
    List<Espiritu> recuperarEspiritusDeTipo(@Param("id") Long id, @Param("tipo") Class<? extends Espiritu> tipo);

    // DEBO CAMBIAR CUANDO UBICACION TENGA TIPO
    @Query("SELECT e.ubicacion FROM Espiritu e " +
            "WHERE TYPE(e.ubicacion) = Santuario GROUP BY e.ubicacion " +
            "ORDER BY  (SUM(CASE WHEN TYPE(e) = Demonio THEN 1 ELSE 0 END) - SUM(CASE WHEN TYPE(e) = Angel THEN 1 ELSE 0 END)) DESC," +
            " e.ubicacion.nombre ASC")
    List<Ubicacion> santuariosCorruptos();

    @Query(
            "SELECT e FROM Espiritu e WHERE e.ubicacion IS NOT NULL AND e.ubicacion.id = :ubicacionId AND TYPE(e) = Demonio"
    )
    List<Object> demoniosEn(@Param("ubicacionId")Long ubicacionId);

    @Query(
            "SELECT e FROM Espiritu e WHERE e.ubicacion IS NOT NULL AND e.ubicacion.id = :ubicacionId AND TYPE(e) = Demonio AND e.medium = NULL"
    )
    List<Object> demoniosLibresEn(@Param("ubicacionId") Long ubicacionId);
}


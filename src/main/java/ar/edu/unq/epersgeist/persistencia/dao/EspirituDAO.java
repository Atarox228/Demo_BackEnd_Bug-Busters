package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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
            "SELECT e FROM Espiritu e WHERE e.ubicacion.id = :ubicacionId"
    )
    List<Espiritu> espiritusEn(@Param("ubicacionId")Long ubicacionId);
}


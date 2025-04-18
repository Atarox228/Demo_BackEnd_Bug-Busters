package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import java.util.List;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MediumDAO extends JpaRepository<Medium, Long> {

//    void guardar(Medium medium);
//    Medium recuperar(Long id);
//    void actualizar(Medium medium);
//    Collection<Medium> recuperarTodos();
//    void eliminar(Medium medium);
//    void eliminarTodo();
//    List<Medium> mediumsSinEspiritusEn(Long ubicacionId);
//
//    List<Espiritu> obtenerEspiritus(Long idMedium);


}

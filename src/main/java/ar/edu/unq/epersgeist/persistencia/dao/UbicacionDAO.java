package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface UbicacionDAO extends JpaRepository<Ubicacion, Long> {
//    void guardar(Ubicacion ubicacion);
//    Ubicacion recuperar(Long ubicacionId);
//    void eliminar(Ubicacion ubicacion);
//    Collection<Ubicacion> recuperarTodos();
//    void eliminarTodo();
//
//    void actualizar(Ubicacion ubicacion);
}

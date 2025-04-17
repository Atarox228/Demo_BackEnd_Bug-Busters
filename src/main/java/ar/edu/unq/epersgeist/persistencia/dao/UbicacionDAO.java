package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import java.util.Collection;

public interface UbicacionDAO {
    void guardar(Ubicacion ubicacion);
    Ubicacion recuperar(Long ubicacionId);
    void eliminar(Ubicacion ubicacion);
    Collection<Ubicacion> recuperarTodos();
    void eliminarTodo();

    void actualizar(Ubicacion ubicacion);
}

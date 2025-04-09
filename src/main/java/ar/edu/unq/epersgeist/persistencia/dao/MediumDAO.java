package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.Collection;
import java.util.List;

public interface MediumDAO {

    void guardar(Medium medium);
    Medium recuperar(Long id);
    void actualizar(Medium medium);
    Collection<Medium> recuperarTodos();
    void eliminar(Medium medium);
    void eliminarTodo();
    List<Medium> mediumsSinEspiritusEn(Long ubicacionId);
}

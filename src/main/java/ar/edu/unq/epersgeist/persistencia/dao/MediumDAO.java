package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.Collection;

public interface MediumDAO {

    void guardar(Medium medium);
    Medium recuperar(Long id);
    Collection<Medium> recuperarTodos();
    void eliminar(Medium medium);
    //void actualizar(Medium medium);
}

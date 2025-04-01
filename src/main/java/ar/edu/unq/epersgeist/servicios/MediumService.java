package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.Collection;

public interface MediumService {

    void guardar(Medium medium);
    Medium recuperar(Long id);
    Collection<Medium> recuperarTodos();
    void eliminar(Medium medium);
    //void actualizar(Medium medium);
}

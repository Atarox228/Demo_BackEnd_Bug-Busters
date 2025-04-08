package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.Collection;

public interface MediumService {

    void guardar(Medium medium);
    Medium recuperar(Long id);
    Collection<Medium> recuperarTodos();
    void eliminar(Medium medium);
    void actualizar(Medium medium);
    void eliminarTodo();
    void descansar(Long idMedium);
    void exorcizar(long idMedium, long idMedium2);
    Espiritu invocar(Long mediumId, Long espirituId);
}

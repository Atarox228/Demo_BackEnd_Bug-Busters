package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MediumService {

    void crear(Medium medium);
    Optional <Medium> recuperar(Long id);
    Collection<Medium> recuperarTodos();
    void eliminar(Medium medium);
    void actualizar(Medium medium);
    void descansar(Long idMedium);
    void exorcizar(long idMedium, long idMedium2);
    Optional<Espiritu> invocar(Long mediumId, Long espirituId);
    List<Espiritu> espiritus(Long idMedium);
    void eliminarTodo();
    void mover(Long mediumId, Long ubicacionId);
    Optional<Medium> recuperarAunConSoftDelete(Long mediumId);
}

package ar.edu.unq.epersgeist.persistencia.repositories.interfaces;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.MediumMongo;

import java.util.Collection;
import java.util.List;

public interface MediumRepository {

    void crear(Medium medium);
    void actualizar(Medium medium);
    void eliminar(Medium medium);
    void eliminarTodos();

    boolean existsById(Long id);

    Medium recuperar(long idMedium);

    MediumMongo recuperarPorIdSQL(Long idMedium);

    List<Espiritu> obtenerEspiritus(Long mediumId);

    Collection<Medium> recuperarTodosNoEliminados();
}
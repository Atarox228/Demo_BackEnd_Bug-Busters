package ar.edu.unq.epersgeist.persistencia.repositorys.interfaces;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.UbicacionNeo4J;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UbicacionRepository {

    void crear(Ubicacion ubicacion);
    Optional<Ubicacion> recuperar(long ubicacionId);
    void actualizar(Ubicacion ubicacion);

    void actualizar(Ubicacion ubicacion, UbicacionNeo4J ubicacionNeo);

    void eliminar(Ubicacion ubicacion);

//    void eliminar(Ubicacion ubicacion, UbicacionNeo4J ubicacionNeo);

    Collection<Ubicacion> recuperarTodos();
    Optional<Ubicacion> recuperarAunConSoftDelete(Long ubicacionId);
    boolean existsById(Long id);
    void eliminarTodos();
    Ubicacion existeUbicacionConNombre(String nombre);
}

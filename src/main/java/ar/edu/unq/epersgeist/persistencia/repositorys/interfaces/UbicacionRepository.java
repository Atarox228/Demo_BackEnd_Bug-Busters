package ar.edu.unq.epersgeist.persistencia.repositorys.interfaces;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.UbicacionNeo4J;

import java.util.Collection;
import java.util.Optional;

public interface UbicacionRepository {

    void crear(Ubicacion ubicacion);
    Ubicacion recuperar(Long ubicacionId);
    UbicacionNeo4J recuperarNeo4J(String nombre);
    void actualizar(Ubicacion ubicacion);
    void actualizar(UbicacionNeo4J ubicacion);

    void eliminar(Ubicacion ubicacion);

//    void eliminar(Ubicacion ubicacion, UbicacionNeo4J ubicacionNeo);

    Collection<Ubicacion> recuperarTodos();
    Optional<Ubicacion> recuperarAunConSoftDelete(Long ubicacionId);
    boolean existsById(Long id);
    void eliminarTodos();
    Ubicacion existeUbicacionConNombre(String nombre);

    Collection<UbicacionNeo4J> ubicacionesConectadas(String nombre);
}

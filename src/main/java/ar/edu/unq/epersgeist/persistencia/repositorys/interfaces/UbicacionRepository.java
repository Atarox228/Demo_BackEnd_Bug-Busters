package ar.edu.unq.epersgeist.persistencia.repositorys.interfaces;

import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.UbicacionNeo4J;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UbicacionRepository {

    void crear(Ubicacion ubicacion);
    Ubicacion recuperar(Long ubicacionId);
    UbicacionNeo4J findByNombre(String nombre);
    void actualizar(Ubicacion ubicacion);

    void actualizarNeo4J(UbicacionNeo4J ubicacion);

    void eliminar(Ubicacion ubicacion);

    Collection<Ubicacion> recuperarTodos();
    Optional<Ubicacion> recuperarAunConSoftDelete(Long ubicacionId);
    boolean existsById(Long id);
    void eliminarTodos();
    Ubicacion existeUbicacionConNombre(String nombre);

    Collection<UbicacionNeo4J> ubicacionesConectadas(String nombre);

    void conectarUbicaciones(String origen, String destino);
    List<UbicacionNeo4J> ubicacionesSobrecargadas(Integer umbralDeEnergia);
}

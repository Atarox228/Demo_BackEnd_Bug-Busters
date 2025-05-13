package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UbicacionService {
    void crear(Ubicacion ubicacion);
    Optional<Ubicacion> recuperar(Long ubicacionId);
    Optional<UbicacionNeo4J> recuperarNeo4J(Long ubicacionId);
    void eliminar(Ubicacion ubicacion);
    void actualizar(Ubicacion ubicacion);
    Collection<Ubicacion> recuperarTodos();
    List<Espiritu> espiritusEn(Long ubicacionId);
    List<Medium> mediumsSinEspiritusEn(Long ubicacionId);
    void clearAll();
    Optional<Ubicacion> recuperarAunConSoftDelete(Long ubicacionId);
    void conectar (Long idOrigen, Long idDestino);
}

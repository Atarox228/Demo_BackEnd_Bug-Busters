package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UbicacionService {
    void crear(Ubicacion ubicacion);
    Optional<Ubicacion> recuperar(Long ubicacionId);
    void eliminar(Ubicacion ubicacion);
    void actualizar(Ubicacion ubicacion);
    Collection<Ubicacion> recuperarTodos();
    List<Espiritu> espiritusEn(Long ubicacionId);
    List<Medium> mediumsSinEspiritusEn(Long ubicacionId);
    void clearAll();
}

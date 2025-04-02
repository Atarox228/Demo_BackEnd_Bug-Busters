package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

import java.util.Collection;
import java.util.List;

public interface UbicacionService {
    void crear(Ubicacion ubicacion);
    Ubicacion recuperar(Long ubicacionId);
    void eliminar(Ubicacion ubicacion);
    void eliminarTodo();

    void actualizar(Ubicacion ubicacion);

    Collection<Ubicacion> recuperarTodos();
    List<Espiritu> espiritusEn(Long ubicacionId);
    List<Medium> mediumsSinEspiritusEn(Long ubicacionId);
}

package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.List;

public interface EspirituService {
    void crear(Espiritu espiritu);
    Espiritu recuperar(Long espirituId);
    List<Espiritu> recuperarTodos();
    void eliminar(Espiritu espiritu);
    void eliminarTodo();
    void actualizar(Espiritu espiritu);
    Medium conectar(Long espirituId, Medium medium);

    List<Espiritu> espiritusDemoniacos();
}

package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.List;

public interface EspirituService {
    Espiritu crear(Espiritu espiritu);
    Espiritu recuperar(Long espirituId);
    List<Espiritu> recuperarTodos();
    void actualizar(Espiritu espiritu);
    void eliminar(Long espirituId);
    Medium conectar(Long espirituId, Medium medium);
}

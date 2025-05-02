package ar.edu.unq.epersgeist.servicios;

import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;

import java.util.List;
import java.util.Optional;

public interface EspirituService {
    void crear(Espiritu espiritu);
    Optional<Espiritu> recuperar(Long espirituId);
    List<Espiritu> recuperarTodos();
    void eliminar(Espiritu espiritu);
    void eliminarTodo();
    void actualizar(Espiritu espiritu);
    Optional<Medium> conectar(Long espirituId, Long mediumId);
    List<Espiritu> espiritusDemoniacos(Direccion direccion, Integer pagina, Integer cantidadPorPagina);
}

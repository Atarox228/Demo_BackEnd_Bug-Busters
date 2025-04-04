package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.TipoEspiritu;

import java.util.List;

public interface EspirituDAO {
    void guardar(Espiritu espiritu);
    Espiritu recuperar(Long idDelEspiritu);
    List<Espiritu> recuperarTodos();
    void actualizar(Espiritu espiritu);
    void eliminar(Espiritu espiritu);
    void eliminarTodo();

    List<Espiritu> espiritusTipo(TipoEspiritu tipoDeEspiritu);
}
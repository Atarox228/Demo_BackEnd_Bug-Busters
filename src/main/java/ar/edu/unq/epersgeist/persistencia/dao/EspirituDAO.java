package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.servicios.enums.Direccion;
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
    List<Espiritu> todosLosEspiritusDeTipo(TipoEspiritu tipoDeEspiritu);
    List<Espiritu> obtenerEspiritus(Direccion direccion, Integer pagina, Integer cantidadPorPagina, TipoEspiritu tipoEspiritu);
    List<Espiritu> espiritusEn(Long ubicacionId);
}
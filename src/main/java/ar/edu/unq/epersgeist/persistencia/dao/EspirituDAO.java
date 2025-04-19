package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import ar.edu.unq.epersgeist.modelo.Espiritu;

import java.util.List;

public interface EspirituDAO {


    void guardar(Espiritu espiritu);
    Espiritu recuperar(Long idDelEspiritu);
    List<Espiritu> recuperarTodos();
    void actualizar(Espiritu espiritu);
    void eliminar(Espiritu espiritu);
    void eliminarTodo();
    List<Espiritu> obtenerDemonios(Direccion direccion, Integer pagina, Integer cantidadPorPagina);
    List<Espiritu> espiritusEn(Long ubicacionId);
    void actualizarEspiritus(List<Espiritu> espiritus);
    List<Espiritu> recuperarEspiritusDeTipo(Long id, Class<? extends Espiritu> tipoEspiritu);

}
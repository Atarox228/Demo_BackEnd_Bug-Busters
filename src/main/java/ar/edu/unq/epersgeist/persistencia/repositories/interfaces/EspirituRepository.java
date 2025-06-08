package ar.edu.unq.epersgeist.persistencia.repositories.interfaces;

import ar.edu.unq.epersgeist.modelo.Angel;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.EspirituMongo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.List;

public interface EspirituRepository {


    void crear(Espiritu espiritu);
    Espiritu recuperar(Long espirituId);
    EspirituMongo recuperarMongo(String string);
    List<Espiritu> recuperarTodosNoEliminados();
    boolean existsById(Long id);
    Page<Espiritu> findDemonios(Pageable pageable);
    void actualizar(Espiritu espiritu);
    void actualizarMongo(EspirituMongo espiritu);
    void eliminarTodos();
    boolean estaEnRango(Espiritu dominator, Espiritu dominated);
    List<Espiritu> recuperarEspiritusDeTipo(Long id, Class<? extends Espiritu> tipo);
}

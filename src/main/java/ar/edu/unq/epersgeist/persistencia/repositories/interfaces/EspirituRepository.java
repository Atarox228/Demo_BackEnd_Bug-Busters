package ar.edu.unq.epersgeist.persistencia.repositories.interfaces;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.EspirituMongo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EspirituRepository {


    void crear(Espiritu espiritu);
    Espiritu recuperar(Long espirituId);
    List<Espiritu> recuperarTodosNoEliminados();
    boolean existsById(Long id);
    Page<Espiritu> findDemonios(Pageable pageable);
    void actualizar(Espiritu espiritu);
    void eliminarTodos();
    EspirituMongo recuperarMongo(String string);
}

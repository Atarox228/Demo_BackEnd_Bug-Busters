package ar.edu.unq.epersgeist.persistencia.repositories.impl;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.CoordenadaMongo;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.EspirituMongo;
import ar.edu.unq.epersgeist.persistencia.dao.CoordenadaDAOMongo;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.EspirituRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EspirituRepositoryImpl implements EspirituRepository {

    private final EspirituDAO espirituDAO;
    private final CoordenadaDAOMongo coordenadaDAOMongo;

    public EspirituRepositoryImpl(EspirituDAO espirituDAO, CoordenadaDAOMongo coordenadaDAOMongo) {
        this.espirituDAO = espirituDAO;
        this.coordenadaDAOMongo = coordenadaDAOMongo;
    }

    @Override
    public void crear(Espiritu espiritu) {
        espirituDAO.save(espiritu);
    }

    @Override
    public Espiritu recuperar(Long espirituId) {
        return espirituDAO.findById(espirituId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espiritu con ID " + espirituId + " no encontrada"));
    }

    @Override
    public List<Espiritu> recuperarTodosNoEliminados() {
        return espirituDAO.recuperarTodosNoEliminados();
    }

    @Override
    public boolean existsById(Long id) {
        return espirituDAO.existsById(id);
    }

    @Override
    public Page<Espiritu> findDemonios(Pageable pageable) {
        return espirituDAO.findDemonios(pageable);
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        espirituDAO.save(espiritu);
    }

    @Override
    public void eliminarTodos() {
        espirituDAO.deleteAll();
    }

    @Override
    public boolean estaEnRango(Espiritu dominator, Espiritu dominated) {
        Optional<CoordenadaMongo> coordenadasDeEspiritu = coordenadaDAOMongo.findByEntityIdAndEntityType(dominated.getId(), dominated.getClass().toString());
            if (!coordenadasDeEspiritu.isPresent()) {
                return true;   // Medio que es un miedo al booleano reeVER EN REFACTOR
            }
        Double latitud = coordenadasDeEspiritu.get().getLatitud();
        Double longitud = coordenadasDeEspiritu.get().getLongitud();
        Optional<EspirituMongo> espirituDominator = coordenadaDAOMongo.findEspirituEnRango(longitud, latitud, dominator.getId(), dominated.getClass().toString());
        return !espirituDominator.isEmpty();
    }

    @Override
    public List<Espiritu> recuperarEspiritusDeTipo(Long id, Class<? extends Espiritu> tipo) {
        return espirituDAO.recuperarEspiritusDeTipo(id, tipo);
    }
}

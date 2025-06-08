package ar.edu.unq.epersgeist.persistencia.repositories.impl;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.Angel;
import ar.edu.unq.epersgeist.modelo.CoordenadaMongo;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.EspirituMongo;
import ar.edu.unq.epersgeist.persistencia.dao.CoordenadaDAOMongo;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAOMongo;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.EspirituRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EspirituRepositoryImpl implements EspirituRepository {

    private final EspirituDAO espirituDAO;
    private final EspirituDAOMongo espirituDAOMongo;
    private final CoordenadaDAOMongo coordenadaDAOMongo;

    public EspirituRepositoryImpl(EspirituDAO espirituDAO, EspirituDAOMongo espirituDAOMongo, CoordenadaDAOMongo coordenadaDAOMongo) {
        this.espirituDAO = espirituDAO;
        this.espirituDAOMongo = espirituDAOMongo;
        this.coordenadaDAOMongo = coordenadaDAOMongo;
    }

    @Override
    public void crear(Espiritu espiritu) {
        espirituDAO.save(espiritu);
        EspirituMongo mongo = new EspirituMongo(espiritu.getId());
        espirituDAOMongo.save(mongo);
    }

    @Override
    public Espiritu recuperar(Long espirituId) {
        return espirituDAO.findById(espirituId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espiritu con ID " + espirituId + " no encontrada"));
    }

    @Override
    public EspirituMongo recuperarMongo(String id) {
        return espirituDAOMongo.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Espiritu con ID " + id + " no encontrada"));
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
    public void actualizarMongo(EspirituMongo espiritu) {
        espirituDAOMongo.save(espiritu);
    }

    @Override
    public void eliminarTodos() {
        espirituDAO.deleteAll();
        espirituDAOMongo.deleteAll();
    }

    @Override
    public boolean estaEnRango(Espiritu dominator, Espiritu dominated) {
        Optional<CoordenadaMongo> coordenadasDeEspiritu = coordenadaDAOMongo.findByEntityIdAndEntityType(dominated.getId(), dominated.getClass().toString());
            if (!coordenadasDeEspiritu.isPresent()) {
                return true;   // Medio que es un miedo al booleano reeVER EN REFACTOR
            }
        Double latitud = coordenadasDeEspiritu.get().getLatitud();
        Double longitud = coordenadasDeEspiritu.get().getLongitud();
        Optional<CoordenadaMongo> coordenadEspiritu = coordenadaDAOMongo.findEspirituEnRango(longitud, latitud, dominator.getId(), dominated.getClass().toString());
        return !coordenadEspiritu.isEmpty();
    }

    @Override
    public void actualizarCoordenadasDe(List<Espiritu> espiritus, GeoJsonPoint destino) {
        for (Espiritu espiritu : espiritus) {
            Optional<CoordenadaMongo> coordenada = coordenadaDAOMongo.findByEntityIdAndEntityType(espiritu.getId(), espiritu.getClass().toString());
            if (coordenada.isPresent()) {
                CoordenadaMongo coordenadaMongo = coordenada.get();
                coordenadaMongo.setPunto(destino);
                coordenadaDAOMongo.save(coordenadaMongo);
            } else {
                CoordenadaMongo coordenadaNueva = new CoordenadaMongo(destino, espiritu.getClass().toString(), espiritu.getId());
                coordenadaDAOMongo.save(coordenadaNueva);
            }
        }
    }

    @Override
    public List<Espiritu> recuperarEspiritusDeTipo(Long id, Class<? extends Espiritu> tipo) {
        return espirituDAO.recuperarEspiritusDeTipo(id, tipo);
    }


}

package ar.edu.unq.epersgeist.persistencia.repositories.impl;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.EspirituMongo;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAOMongo;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.EspirituRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EspirituRepositoryImpl implements EspirituRepository {

    private final EspirituDAO espirituDAO;
    private final EspirituDAOMongo espirituDAOMongo;

    public EspirituRepositoryImpl(EspirituDAO espirituDAO, EspirituDAOMongo espirituDAOMongo) {
        this.espirituDAO = espirituDAO;
        this.espirituDAOMongo = espirituDAOMongo;
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
        espirituDAOMongo.deleteAll();
    }

    @Override
    public EspirituMongo recuperarMongo(String id) {
        return espirituDAOMongo.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Espiritu con ID " + id + " no encontrada"));
    }
}

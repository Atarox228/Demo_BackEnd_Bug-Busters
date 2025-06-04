package ar.edu.unq.epersgeist.persistencia.repositories.impl;
import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.MediumRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class MediumRepositoryImpl implements MediumRepository {

    private final MediumDAO mediumDAO;
    private final MediumDAOMongo mediumDAOMongo;

    public MediumRepositoryImpl(MediumDAO mediumDAO, MediumDAOMongo mediumDAOMongo) {
        this.mediumDAO = mediumDAO;
        this.mediumDAOMongo = mediumDAOMongo;
    }

    @Override
    public void crear(Medium medium){
        MediumMongo mediumMongo = new MediumMongo(medium.getId());
        mediumDAO.save(medium);
        mediumDAOMongo.save(mediumMongo);
    }

    @Override
    public void actualizar(Medium medium) {
        MediumMongo mediumMongo = mediumDAOMongo.findByMediumIdSQL(medium.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con id SQL " + medium.getId() + " no encontrada"));
        mediumDAO.save(medium);
        mediumDAOMongo.save(mediumMongo);
    }

    @Override
    public void eliminar(Medium medium) {
        MediumMongo mediumMongo = mediumDAOMongo.findByMediumIdSQL(medium.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con id SQL " + medium.getId() + " no encontrada"));
        mediumDAOMongo.delete(mediumMongo);
    }

    @Override
    public void eliminarTodos() {
        mediumDAO.deleteAll();
        mediumDAOMongo.deleteAll();
    }

    @Override
    public boolean existsById(Long id) {
        return mediumDAO.existsById(id);
    }


    @Override
    public Medium recuperar(long idMedium) {
        return mediumDAO.findById(idMedium)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con id " + idMedium + " no encontrada"));
    }

    @Override
    public MediumMongo recuperarPorIdSQL(Long idMedium) {
        return mediumDAOMongo.findByMediumIdSQL(idMedium)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con id SQL" + idMedium + " no encontrada"));
    }

    @Override
    public List<Espiritu> obtenerEspiritus(Long mediumId) {
        return mediumDAO.obtenerEspiritus(mediumId);
    }

    @Override
    public Collection<Medium> recuperarTodosNoEliminados() {
        return mediumDAO.recuperarTodosNoEliminados();
    }
}

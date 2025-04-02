package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

import java.util.List;

public class EspirituServiceImpl implements EspirituService {

    private final EspirituDAO espirituDAO;
    private final MediumDAO mediumDAO;

//    public EspirituServiceImpl(EspirituDAO espirituDAO) {
//        this.espirituDAO = espirituDAO;
//    }

    public EspirituServiceImpl(EspirituDAO espirituDAO, MediumDAO mediumDao) {
        this.espirituDAO = espirituDAO;
        this.mediumDAO = mediumDao;
    }

    @Override
    public void crear(Espiritu espiritu) {
        HibernateTransactionRunner.runTrx(() -> {
            espirituDAO.guardar(espiritu);
            return null;
        });
    }

    @Override
    public Espiritu recuperar(Long espirituId) {
        return HibernateTransactionRunner.runTrx(() -> espirituDAO.recuperar(espirituId));
    }

    @Override
    public List<Espiritu> recuperarTodos() {
        return HibernateTransactionRunner.runTrx(() -> espirituDAO.recuperarTodos());
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        HibernateTransactionRunner.runTrx(() -> {
            espirituDAO.actualizar(espiritu);
            return null;
        });
    }

    @Override
    public void eliminar(Espiritu espiritu) {
        HibernateTransactionRunner.runTrx(() -> {
            espirituDAO.eliminar(espiritu);
            return null;
        });
    }

    public Medium conectar(Long espirituId, Long mediumId) {
        return HibernateTransactionRunner.runTrx(() -> {
            Espiritu espiritu = this.espirituDAO.recuperar(espirituId);
            Medium medium = this.mediumDAO.recuperar(mediumId);
            medium.conectarseAEspiritu(espiritu);
            this.espirituDAO.actualizar(espiritu);
            this.mediumDAO.actualizar(medium);
            return this.mediumDAO.recuperar(medium.getId());
        });
    }

    @Override
    public List<Espiritu> espiritusDemoniacos() {
        return HibernateTransactionRunner.runTrx(() -> espirituDAO.espiritusTipo("Demoniaco"));
    }

    public void eliminarTodo(){
        HibernateTransactionRunner.runTrx(() -> {
            espirituDAO.eliminarTodo();
            return null;
        });
    }

}

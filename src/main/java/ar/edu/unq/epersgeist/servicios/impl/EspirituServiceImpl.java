package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

import java.util.List;

public class EspirituServiceImpl implements EspirituService {

    private final EspirituDAO espirituDAO;

    public EspirituServiceImpl(EspirituDAO espirituDAO) {
        this.espirituDAO = espirituDAO;
    }

    @Override
    public void guardar(Espiritu espiritu) {
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

    @Override
    public Medium conectar(Long espirituId, Medium medium) {
        Espiritu espi = this.recuperar(espirituId);
        medium.conectarseAEspiritu(espi);
        this.actualizar(espi);
        return medium;
    }

}

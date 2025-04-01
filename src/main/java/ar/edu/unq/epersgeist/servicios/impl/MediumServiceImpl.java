package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

import java.util.Collection;
import java.util.List;

public class MediumServiceImpl implements MediumService {

    private MediumDAO dao;


    public MediumServiceImpl(MediumDAO dao) {
        this.dao = dao;
    }

    @Override
    public void guardar(Medium medium) {
        HibernateTransactionRunner.runTrx(() -> {
            dao.guardar(medium);
            return null;
        });
    }

    @Override
    public Medium recuperar(Long id) {
        return HibernateTransactionRunner.runTrx(() -> dao.recuperar(id));
    }

    @Override
    public Collection<Medium> recuperarTodos() {
        return HibernateTransactionRunner.runTrx(dao::recuperarTodos);
    }

    public void eliminar(Medium medium) {
        HibernateTransactionRunner.runTrx(() -> {
            dao.eliminar(medium);
            return null;
        });
    }

}

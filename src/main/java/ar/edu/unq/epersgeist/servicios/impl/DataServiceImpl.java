package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.DataService;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

public class DataServiceImpl implements DataService {

    private MediumDAO mediumDAO;
    private EspirituDAO espirituDAO;
    private UbicacionDAO ubicacionDAO;

    public DataServiceImpl (EspirituDAO espirituDAO, MediumDAO mediumDao, UbicacionDAO ubicacionDAO) {
        this.espirituDAO = espirituDAO;
        this.mediumDAO = mediumDao;
        this.ubicacionDAO = ubicacionDAO;
    }
    public void eliminarTodo(){
        HibernateTransactionRunner.runTrx(() -> {
            espirituDAO.eliminarTodo();
            mediumDAO.eliminarTodo();
            ubicacionDAO.eliminarTodo();
            return null;
        });
    }
}

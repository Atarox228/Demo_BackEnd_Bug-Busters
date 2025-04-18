package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import org.springframework.stereotype.Service;


import java.util.Collection;
import java.util.List;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionDAO ubicacionDAO;
    private final MediumDAO mediumDAO;
    private final EspirituDAO espirituDAO;

    public UbicacionServiceImpl(UbicacionDAO ubicacionDAO, MediumDAO mediumDAO, EspirituDAO espirituDAO){
        this.ubicacionDAO = ubicacionDAO;
        this.mediumDAO = mediumDAO;
        this.espirituDAO = espirituDAO;
    }

    @Override
    public void crear(Ubicacion ubicacion) {
//        HibernateTransactionRunner.runTrx(() -> {
//            ubicacionDAO.guardar(ubicacion);
//            return null;
//        });
    }

    @Override
    public Ubicacion recuperar(Long ubicacionId) {
//        if (ubicacionId == null) {
//            throw new IdNoValidoException(ubicacionId);
//        }
//        return HibernateTransactionRunner.runTrx(() -> ubicacionDAO.recuperar(ubicacionId));
        return null;
    }

    @Override
    public void eliminar(Ubicacion ubicacion) {
//
//        HibernateTransactionRunner.runTrx(() -> {
//            ubicacionDAO.eliminar(ubicacion);
//            return null;
//        });
    }

    @Override
    public void actualizar(Ubicacion ubicacion) {
//        if(ubicacion.getId() == null){
//            throw new IdNoValidoException(null);
//        }
//        HibernateTransactionRunner.runTrx(() -> {
//            ubicacionDAO.actualizar(ubicacion);
//            return null;
//        });
    }

    @Override
    public Collection<Ubicacion> recuperarTodos() {
//        return HibernateTransactionRunner.runTrx(() -> ubicacionDAO.recuperarTodos());
        return null;
    }

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
//        return HibernateTransactionRunner.runTrx(() -> espirituDAO.espiritusEn(ubicacionId));
        return null;
    }

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
//        return HibernateTransactionRunner.runTrx(() -> mediumDAO.mediumsSinEspiritusEn(ubicacionId));
        return null;
    }


}

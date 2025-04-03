package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.exception.UbicacionEnlazadaConEntidadesException;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;


import java.util.Collection;
import java.util.List;

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
        HibernateTransactionRunner.runTrx(() -> {
            ubicacionDAO.guardar(ubicacion);
            return null;
        });
    }

    @Override
    public Ubicacion recuperar(Long ubicacionId) {
        if (ubicacionId == null) {
            throw new IdNoValidoException(ubicacionId);
        }
        return HibernateTransactionRunner.runTrx(() -> ubicacionDAO.recuperar(ubicacionId));
    }

    @Override
    public void eliminar(Ubicacion ubicacion) {
        if(ubicacion.getEspiritus().size() > 0 || ubicacion.getMediums().size() > 0){
            throw new UbicacionEnlazadaConEntidadesException(ubicacion);
        }

        HibernateTransactionRunner.runTrx(() -> {
            ubicacionDAO.eliminar(ubicacion);
            return null;
        });
    }

    @Override
    public void eliminarTodo(){
        HibernateTransactionRunner.runTrx(() -> {
            ubicacionDAO.eliminarTodo();
            return null;
        });
    }

    @Override
    public void actualizar(Ubicacion ubicacion) {
        HibernateTransactionRunner.runTrx(() -> {
            ubicacionDAO.actualizar(ubicacion);
            return null;
        });
    }

    @Override
    public Collection<Ubicacion> recuperarTodos() {
        return HibernateTransactionRunner.runTrx(() -> ubicacionDAO.recuperarTodos());
    }

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        return HibernateTransactionRunner.runTrx(() -> espirituDAO.espiritusEn(ubicacionId));
    }

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
        return List.of();
    }

    public void agregarEspiritu(Long ubicacionId,Long espirituId){
        HibernateTransactionRunner.runTrx(() -> {
            Ubicacion ubicacion = ubicacionDAO.recuperar(ubicacionId);
            Espiritu espiritu = espirituDAO.recuperar(espirituId);
            ubicacion.agregarEspiritu(espiritu);
            return null;
        });

    }
}

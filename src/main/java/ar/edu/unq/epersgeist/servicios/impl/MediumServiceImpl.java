package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

import java.util.Collection;
import java.util.List;

public class MediumServiceImpl implements MediumService {

    private final EspirituDAO espirituDao;
    private final UbicacionDAO ubicacionDao;
    private MediumDAO mediumDao;

    public MediumServiceImpl(MediumDAO mediumDao, EspirituDAO espirituDao, UbicacionDAO ubicacionDao) {
        this.mediumDao = mediumDao;
        this.espirituDao = espirituDao;
        this.ubicacionDao = ubicacionDao;
    }

    @Override
    public void guardar(Medium medium) {
        HibernateTransactionRunner.runTrx(() -> {
            mediumDao.guardar(medium);
            return null;
        });
    }

    @Override
    public Medium recuperar(Long id) {
        if (id == null) {
            throw new IdNoValidoException(id);
        }
        return HibernateTransactionRunner.runTrx(() -> mediumDao.recuperar(id));
    }

    @Override
    public Collection<Medium> recuperarTodos() {
        return HibernateTransactionRunner.runTrx(mediumDao::recuperarTodos);
    }

    public void eliminar(Medium medium) {
        HibernateTransactionRunner.runTrx(() -> {
            mediumDao.eliminar(medium);
            return null;
        });
    }

    @Override
    public void actualizar(Medium medium) {
        HibernateTransactionRunner.runTrx(() -> {
            mediumDao.actualizar(medium);
            return null;
        });
    }

    public void eliminarTodo(){
        HibernateTransactionRunner.runTrx(() -> {
            mediumDao.eliminarTodo();
            return null;
        });
    }

    public void descansar(Long mediumId){
        if (mediumId == null) {throw new IdNoValidoException(mediumId);}
        HibernateTransactionRunner.runTrx(() -> {
            Medium medium = mediumDao.recuperar(mediumId);
            if (medium == null) {throw new IdNoValidoException(mediumId);}
            medium.descansar();
            mediumDao.actualizar(medium);
            return null;
        });
    }

    @Override
    public Espiritu invocar(Long mediumId, Long espirituId) {
        return HibernateTransactionRunner.runTrx(() -> {
            Medium medium = this.mediumDao.recuperar(mediumId);
            Espiritu espiritu = this.espirituDao.recuperar(espirituId);
            medium.invocar(espiritu);
            this.espirituDao.actualizar(espiritu);
            this.mediumDao.actualizar(medium);
            return espirituDao.recuperar(espirituId);
        });
    }

    @Override
    public List<Espiritu> espiritus(Long idMedium) {
        return HibernateTransactionRunner.runTrx(() -> mediumDao.obtenerEspiritus(idMedium));
    }


    public void ubicarseEn(Long mediumId, Long ubicacionId) {
        HibernateTransactionRunner.runTrx(() -> {
            Medium medium = mediumDao.recuperar(mediumId);
            Ubicacion ubicacion = ubicacionDao.recuperar(ubicacionId);
            medium.setUbicacion(ubicacion);
            return null;
        });
    }
}

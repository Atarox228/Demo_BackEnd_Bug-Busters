package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;

import java.util.Collection;
import java.util.List;

public class MediumServiceImpl implements MediumService {

    private final EspirituDAO espirituDao;
    private final MediumDAO mediumDao;

    public MediumServiceImpl(MediumDAO mediumDao, EspirituDAO espirituDao) {
        this.mediumDao = mediumDao;
        this.espirituDao = espirituDao;
    }

    @Override
    public void crear(Medium medium) {
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
        if (mediumId == null || espirituId == null){
            throw new IdNoValidoException();
        }
        return HibernateTransactionRunner.runTrx(() -> {
            Medium medium = this.mediumDao.recuperar(mediumId);
            Espiritu espiritu = this.espirituDao.recuperar(espirituId);
            if (medium == null || espiritu == null) {throw new IdNoValidoException(espirituId);}
            medium.invocar(espiritu);
            this.espirituDao.actualizar(espiritu);
            this.mediumDao.actualizar(medium);
            return espirituDao.recuperar(espirituId);
        });
    }

    @Override
    public List<Espiritu> espiritus(Long idMedium) {
        if (idMedium == null) {throw new IdNoValidoException();}
        return HibernateTransactionRunner.runTrx(() -> {
                Medium medium = this.mediumDao.recuperar(idMedium);
                if (medium == null) {throw new IdNoValidoException();}
                return mediumDao.obtenerEspiritus(idMedium);
        });
    }


//    public void mover(Long mediumId, Long ubicacionId) {
//        HibernateTransactionRunner.runTrx(() -> {
//            Medium medium = mediumDao.recuperar(mediumId);
//            Ubicacion ubicacion = ubicacionDao.recuperar(ubicacionId);
//            medium.setUbicacion(ubicacion);
//            return null;
//        });
//    }
    // Comento mover a que podriamos utilizarlo en el TP3


   public void exorcizar(long idMedium, long idMedium2){
        HibernateTransactionRunner.runTrx(() -> {
            Medium medium = mediumDao.recuperar(idMedium);
            Medium medium2 = mediumDao.recuperar(idMedium2);
            List<Espiritu> angeles = espirituDao.recuperarEspiritusDeTipo(medium.getId(), Angel.class);
            List<Espiritu> demonios = espirituDao.recuperarEspiritusDeTipo(medium2.getId(), Demonio.class);
            medium.exorcizar(medium2, angeles, demonios);
            mediumDao.actualizar(medium);
            mediumDao.actualizar(medium2);
            return null;
        });

    }
}

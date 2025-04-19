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
import org.springframework.transaction.annotation.Transactional;


import java.util.Collection;
import java.util.List;


@Service
@Transactional
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionDAO ubicacionDAO;
    private final MediumDAO mediumDAO;
    private final EspirituDAO espirituDAO;

    public UbicacionServiceImpl(UbicacionDAO ubicacionDAO, MediumDAO mediumDAO, EspirituDAO espirituDAO) {
        this.ubicacionDAO = ubicacionDAO;
        this.mediumDAO = mediumDAO;
        this.espirituDAO = espirituDAO;
    }

    @Override
    public void crear(Ubicacion ubicacion) {
        if (ubicacion.getId() != null) {
            throw new IdNoValidoException(null);
        }
        ubicacionDAO.save(ubicacion);
    }

    @Override
    public Ubicacion recuperar(Long ubicacionId) {
        if (ubicacionId == null) {
            throw new IdNoValidoException(null);
        }
        return ubicacionDAO.findById(ubicacionId).orElseThrow(() -> new IdNoValidoException(ubicacionId));
    }

    @Override
    public void eliminar(Ubicacion ubicacion) {
        ubicacionDAO.delete(ubicacion);
    }

    @Override
    public void actualizar(Ubicacion ubicacion) {
        if(ubicacion.getId() == null){
            throw new IdNoValidoException(null); //
        }
//        ubicacionDAO.findById(ubicacion.getId()).orElseThrow(() -> new IdNoValidoException(ubicacion.getId()));
        ubicacionDAO.save(ubicacion);
    }

    @Override
    public Collection<Ubicacion> recuperarTodos() {
        return ubicacionDAO.findAll();
    }

    @Override
    public void clearAll() {
        ubicacionDAO.deleteAll();
    }

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        return espirituDAO.espiritusEn(ubicacionId);
    }

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
        return mediumDAO.mediumsSinEspiritusEn(ubicacionId);
    }


}

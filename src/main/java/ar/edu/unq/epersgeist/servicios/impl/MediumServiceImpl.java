package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MediumServiceImpl implements MediumService {
    public final MediumDAO mediumDAO;
    public final EspirituDAO espirituDAO;
    public final UbicacionDAO ubicacionDAO;

    public MediumServiceImpl(MediumDAO mediumDAO, EspirituDAO espirituDAO, UbicacionDAO ubicacionDAO) {
        this.mediumDAO = mediumDAO;
        this.espirituDAO = espirituDAO;
        this.ubicacionDAO = ubicacionDAO;
    }

    @Override
    public void crear(Medium medium) {
        if (medium.getId() != null) {
            throw new IdNoValidoException(null);
        }
        mediumDAO.save(medium);
    }

    @Override
    public Medium recuperar(Long id) {
        return mediumDAO.findById(id)
                .orElseThrow(() -> new IdNoValidoException(id));
    }

    @Override
    public Collection<Medium> recuperarTodos() {
        return mediumDAO.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
    }

    @Override
    public void eliminar(Medium medium) {
        mediumDAO.delete(medium);
    }

    @Override
    public void eliminarTodo() {
        mediumDAO.deleteAll();
    }

    @Override
    public void actualizar(Medium medium) {
        if (medium.getId() == null || !mediumDAO.existsById(medium.getId())) {
            throw new IdNoValidoException(medium.getId());
        }
        mediumDAO.save(medium);
    }


    @Override
    public void exorcizar(long idMedium, long idMedium2) {
        Medium medium = mediumDAO.findById(idMedium)
                .orElseThrow(() -> new IdNoValidoException(idMedium));
        Medium medium2 = mediumDAO.findById(idMedium2)
                .orElseThrow(() -> new IdNoValidoException(idMedium2));
        //List<Espiritu> angeles = espirituDao.recuperarEspiritusDeTipo(medium.getId(), Angel.class);
        //List<Espiritu> demonios = espirituDao.recuperarEspiritusDeTipo(medium2.getId(), Demonio.class);
        //medium.exorcizar(medium2, angeles, demonios);
        mediumDAO.save(medium);
        mediumDAO.save(medium2);
    }

    @Override
    public Optional<Espiritu> invocar(Long mediumId, Long espirituId) {
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new IdNoValidoException(mediumId));
        Espiritu espiritu = espirituDAO.findById(mediumId)
                .orElseThrow(() -> new IdNoValidoException(mediumId));

        medium.invocar(espiritu);

        //necesario?
        espirituDAO.save(espiritu);
        mediumDAO.save(medium);

        return espirituDAO.findById(mediumId);
    }

    @Override
    public List<Espiritu> espiritus(Long idMedium) {
        mediumDAO.findById(idMedium)
                .orElseThrow(() -> new IdNoValidoException(idMedium));
        return mediumDAO.obtenerEspiritus(idMedium);
    }

    @Override
    public void descansar(Long mediumId){
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new IdNoValidoException(mediumId));
        medium.descansar();
        mediumDAO.save(medium);
    }












/*
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
*/



//    public void mover(Long mediumId, Long ubicacionId) {
//        HibernateTransactionRunner.runTrx(() -> {
//            Medium medium = mediumDao.recuperar(mediumId);
//            Ubicacion ubicacion = ubicacionDao.recuperar(ubicacionId);
//            medium.setUbicacion(ubicacion);
//            return null;
//        });
//    }
    // Comento mover a que podriamos utilizarlo en el TP3

}
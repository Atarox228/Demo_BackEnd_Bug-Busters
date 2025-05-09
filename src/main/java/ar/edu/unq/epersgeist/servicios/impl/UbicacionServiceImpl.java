package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


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
        if(ubicacionDAO.existeUbicacionConNombre(ubicacion.getNombre()) != null){

            throw new UbicacionYaCreadaException(ubicacion.getNombre());
        }
        ubicacionDAO.save(ubicacion);
    }

    @Override
    public Optional<Ubicacion> recuperar(Long ubicacionId) {
        RevisarId(ubicacionId);
        Ubicacion ubicacion = ubicacionDAO.findById(ubicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + ubicacionId + " no encontrada"));
        RevisarEntidadEliminado(ubicacion.getDeleted(),ubicacion);
        return Optional.of(ubicacion);
    }

    @Override
    public void eliminar(Ubicacion ubicacion) {
        if (!ubicacionDAO.existsById(ubicacion.getId())) {
            throw new RecursoNoEncontradoException("Ubicación con ID " + ubicacion.getId() + " no encontrada");
        }
        RevisarEntidadEliminado(ubicacion.getDeleted(),ubicacion);
        RevisarUbicacionConEntidades(ubicacion.getId(),ubicacion);
        ubicacion.setDeleted(true);
        ubicacionDAO.save(ubicacion);
    }

    @Override
    public void actualizar(Ubicacion ubicacion) {
        RevisarId (ubicacion.getId());
        if (!ubicacionDAO.existsById(ubicacion.getId())) {
            throw new RecursoNoEncontradoException("Ubicacion con ID " + ubicacion.getId() + " no encontrado");
        }
        RevisarEntidadEliminado(ubicacion.getDeleted(),ubicacion);
        ubicacionDAO.save(ubicacion);
    }

    @Override
    public Collection<Ubicacion> recuperarTodos() {
        return ubicacionDAO.recuperarTodosNoEliminados();
    }

    @Override
    public void clearAll() {
        ubicacionDAO.deleteAll();
    }

    @Override
    public Optional<Ubicacion> recuperarAunConSoftDelete(Long ubicacionId) {
        RevisarId(ubicacionId);
        Ubicacion ubicacion = ubicacionDAO.findById(ubicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + ubicacionId + " no encontrada"));
        return Optional.of(ubicacion);
    }

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        RevisarId(ubicacionId);
        return espirituDAO.espiritusEn(ubicacionId);
    }

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
        RevisarId(ubicacionId);
        return mediumDAO.mediumsSinEspiritusEn(ubicacionId);
    }

    private <T> void RevisarEntidadEliminado(Boolean condicion,T entidad) {
        if(condicion){
            throw new EntidadEliminadaException(entidad);
        }
    }
    private void RevisarId(Long id){
        if (id == null || id <= 0) {
            throw new IdNoValidoException();
        }
    }

    private <T> void RevisarUbicacionConEntidades(Long id, T entidad){
        if (!espiritusEn(id).isEmpty() || !mediumsEn(id).isEmpty()){
            throw new EntidadConEntidadesConectadasException(entidad);
        }
    }

    private List<Medium> mediumsEn(Long id){
        return mediumDAO.mediumsEn(id);
    }

}

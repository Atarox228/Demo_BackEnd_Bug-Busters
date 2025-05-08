package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.EntidadSinUbicacionException;
import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.exception.EntidadConEntidadesConectadasException;
import ar.edu.unq.epersgeist.servicios.exception.EntidadEliminadaException;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.exception.MovimientoInvalidoException;
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
        mediumDAO.save(medium);
    }

    @Override
    public Optional <Medium> recuperar(Long id) {
        RevisarId(id);
        Medium medium = mediumDAO.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + id + " no encontrado"));
        RevisarEntidadEliminado(medium.getDeleted(),medium);

        return Optional.of(medium);
    }

    @Override
    public Collection<Medium> recuperarTodos() {
        return mediumDAO.recuperarTodosNoEliminados();
    }

    @Override
    public void eliminar(Medium medium) {
        if (!mediumDAO.existsById(medium.getId())) {
            throw new RecursoNoEncontradoException("Medium con ID " + medium.getId() + " no encontrado");
        }
        RevisarEntidadEliminado(medium.getDeleted(),medium);
        RevisarUbicacionConEntidades(medium.getId(),medium);
        medium.setDeleted(true);
        mediumDAO.save(medium);
    }

    @Override
    public void eliminarTodo() {
        mediumDAO.deleteAll();
    }

    @Override
    public void actualizar(Medium medium) {
        RevisarId(medium.getId());
        if (!mediumDAO.existsById(medium.getId())) {
            throw new RecursoNoEncontradoException("Medium con ID " + medium.getId() + " no encontrado");
        }
        RevisarEntidadEliminado(medium.getDeleted(),medium);
        mediumDAO.save(medium);
    }

    @Override
    public void exorcizar(long idMedium, long idMedium2) {
        RevisarId(idMedium);
        RevisarId(idMedium2);
        Medium medium = mediumDAO.findById(idMedium)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + idMedium + " no encontrado"));
        Medium medium2 = mediumDAO.findById(idMedium2)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + idMedium2 + " no encontrado"));

        RevisarUbicacionNoNula(medium.getUbicacion(),medium,idMedium);
        RevisarUbicacionNoNula(medium2.getUbicacion(),medium2,idMedium2);
        
        List<Espiritu> angeles = espirituDAO.recuperarEspiritusDeTipo(medium.getId(), Angel.class);
        List<Espiritu> demonios = espirituDAO.recuperarEspiritusDeTipo(medium2.getId(), Demonio.class);
        medium.exorcizar(medium2, angeles, demonios);
        mediumDAO.save(medium);
        mediumDAO.save(medium2);
    }

    @Override
    public Optional<Espiritu> invocar(Long mediumId, Long espirituId) {
        RevisarId(mediumId);
        RevisarId(espirituId);
        
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + mediumId + " no encontrada"));
        Espiritu espiritu = espirituDAO.findById(espirituId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espiritu con ID " + espirituId + " no encontrada"));

        RevisarEntidadEliminado(medium.getDeleted(),medium);
        RevisarEntidadEliminado(espiritu.getDeleted(),espiritu);

        RevisarUbicacionNoNula(medium.getUbicacion(),medium,mediumId);
        
        medium.invocar(espiritu);
        mediumDAO.save(medium);
        return espirituDAO.findById(espirituId);
    }

    @Override
    public List<Espiritu> espiritus(Long idMedium) {
        RevisarId(idMedium);
        Medium medium = mediumDAO.findById(idMedium)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + idMedium + " no encontrada"));
        RevisarEntidadEliminado(medium.getDeleted(),medium);
        return mediumDAO.obtenerEspiritus(idMedium);
    }

    @Override
    public void descansar(Long mediumId){
        RevisarId(mediumId);
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + mediumId + " no encontrada"));
        RevisarUbicacionNoNula(medium.getUbicacion(),medium,mediumId);
        medium.descansar();
        mediumDAO.save(medium);
    }

    @Override
    public void mover(Long mediumId, Long ubicacionId) {
        RevisarId(mediumId);
        RevisarId(mediumId);
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + mediumId + " no encontrada"));
        Ubicacion ubicacion = ubicacionDAO.findById(ubicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicacion con ID " + ubicacionId + " no encontrada"));
        RevisarEntidadEliminado(medium.getDeleted(),medium);
        RevisarEntidadEliminado(ubicacion.getDeleted(),ubicacion);
        
        if (medium.getUbicacion() != null && medium.getUbicacion().getId().equals(ubicacion.getId())) throw new MovimientoInvalidoException();
        medium.moverseA(ubicacion);
        ubicacionDAO.save(ubicacion);
        mediumDAO.save(medium);
    }

    @Override
    public Optional<Medium> recuperarAunConSoftDelete(Long mediumId) {
        RevisarId(mediumId);
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + mediumId + " no encontrado"));
        return Optional.of(medium);
    }

    private <T> void RevisarEntidadEliminado(Boolean condicion,T entidad) {
        if(condicion){
            throw new EntidadEliminadaException(entidad);
        }
    }
    private <T> void RevisarUbicacionNoNula(Ubicacion ubicacion, T entidad, Long id) {
        if(ubicacion == null){
            throw new EntidadSinUbicacionException(entidad,id);
        }
    }

    private <T> void RevisarUbicacionConEntidades(Long id, T entidad){
        if (!espiritus(id).isEmpty()){
            throw new EntidadConEntidadesConectadasException(entidad);
        }
    }

    private void RevisarId(Long id){
        if (id == null || id <= 0) {
            throw new IdNoValidoException();
        }
    }
}
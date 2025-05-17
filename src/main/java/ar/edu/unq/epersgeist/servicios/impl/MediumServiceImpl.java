package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.exception.UbicacionLejanaException;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.persistencia.repositorys.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.exception.*;
import jakarta.transaction.Transactional;
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
    private final UbicacionRepository ubicacionRepository;

    public MediumServiceImpl(MediumDAO mediumDAO, EspirituDAO espirituDAO, UbicacionDAO ubicacionDAO, UbicacionRepository ubicacionRepository) {
        this.mediumDAO = mediumDAO;
        this.espirituDAO = espirituDAO;
        this.ubicacionDAO = ubicacionDAO;
        this.ubicacionRepository = ubicacionRepository;
    }

    @Override
    public void crear(Medium medium) {
        mediumDAO.save(medium);
    }

    @Override
    public Optional <Medium> recuperar(Long id) {
        revisarId(id);
        Medium medium = mediumDAO.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + id + " no encontrado"));
        revisarEntidadEliminado(medium.getDeleted(),medium);

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
        revisarEntidadEliminado(medium.getDeleted(),medium);
        revisarUbicacionConEntidades(medium.getId(),medium);
        medium.setDeleted(true);
        mediumDAO.save(medium);
    }

    @Override
    public void eliminarTodo() {
        mediumDAO.deleteAll();
    }

    @Override
    public void actualizar(Medium medium) {
        revisarId(medium.getId());
        if (!mediumDAO.existsById(medium.getId())) {
            throw new RecursoNoEncontradoException("Medium con ID " + medium.getId() + " no encontrado");
        }
        revisarEntidadEliminado(medium.getDeleted(),medium);
        mediumDAO.save(medium);
    }

    @Override
    public void exorcizar(long idMedium, long idMedium2) {
        revisarId(idMedium);
        revisarId(idMedium2);
        Medium medium = mediumDAO.findById(idMedium)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + idMedium + " no encontrado"));
        Medium medium2 = mediumDAO.findById(idMedium2)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + idMedium2 + " no encontrado"));

        revisarUbicacionNoNula(medium.getUbicacion(),medium,idMedium);
        revisarUbicacionNoNula(medium2.getUbicacion(),medium2,idMedium2);
        
        List<Espiritu> angeles = espirituDAO.recuperarEspiritusDeTipo(medium.getId(), Angel.class);
        List<Espiritu> demonios = espirituDAO.recuperarEspiritusDeTipo(medium2.getId(), Demonio.class);
        medium.exorcizar(medium2, angeles, demonios);
        mediumDAO.save(medium);
        mediumDAO.save(medium2);
    }

    @Override
    public Optional<Espiritu> invocar(Long mediumId, Long espirituId) {
        revisarId(mediumId);
        revisarId(espirituId);
        
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + mediumId + " no encontrada"));
        Espiritu espiritu = espirituDAO.findById(espirituId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espiritu con ID " + espirituId + " no encontrada"));

        revisarEntidadEliminado(medium.getDeleted(),medium);
        revisarEntidadEliminado(espiritu.getDeleted(),espiritu);

        revisarUbicacionNoNula(medium.getUbicacion(),medium,mediumId);
        
        medium.invocar(espiritu);
        mediumDAO.save(medium);
        return espirituDAO.findById(espirituId);
    }

    @Override
    public List<Espiritu> espiritus(Long idMedium) {
        revisarId(idMedium);
        Medium medium = mediumDAO.findById(idMedium)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + idMedium + " no encontrada"));
        revisarEntidadEliminado(medium.getDeleted(),medium);
        return mediumDAO.obtenerEspiritus(idMedium);
    }

    @Override
    public void descansar(Long mediumId){
        revisarId(mediumId);
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + mediumId + " no encontrada"));
        revisarUbicacionNoNula(medium.getUbicacion(),medium,mediumId);
        medium.descansar();
        mediumDAO.save(medium);
    }

    @Override
    public void mover(Long mediumId, Long ubicacionId) {
        revisarId(mediumId);
        revisarId(mediumId);
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + mediumId + " no encontrada"));
        Ubicacion ubicacion = ubicacionDAO.findById(ubicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicacion con ID " + ubicacionId + " no encontrada"));
        revisarEntidadEliminado(medium.getDeleted(),medium);
        revisarEntidadEliminado(ubicacion.getDeleted(),ubicacion);

        
        if (medium.getUbicacion() != null && medium.getUbicacion().getId().equals(ubicacion.getId())) throw new MovimientoInvalidoException();
        if (! ubicacionRepository.estanConectadasDirecta(medium.getUbicacion().getNombre(), ubicacion.getNombre())) throw new UbicacionLejanaException();
        medium.moverseA(ubicacion);
        mediumDAO.save(medium);
    }

    @Override
    public Optional<Medium> recuperarAunConSoftDelete(Long mediumId) {
        revisarId(mediumId);
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + mediumId + " no encontrado"));
        return Optional.of(medium);
    }

    private <T> void revisarEntidadEliminado(Boolean condicion, T entidad) {
        if(condicion){
            throw new EntidadEliminadaException(entidad);
        }
    }
    private <T> void revisarUbicacionNoNula(Ubicacion ubicacion, T entidad, Long id) {
        if(ubicacion == null){
            throw new EntidadSinUbicacionException(entidad,id);
        }
    }

    private <T> void revisarUbicacionConEntidades(Long id, T entidad){
        if (!espiritus(id).isEmpty()){
            throw new EntidadConEntidadesConectadasException(entidad);
        }
    }

    private void revisarId(Long id){
        if (id == null || id <= 0) {
            throw new IdNoValidoException();
        }
    }
}
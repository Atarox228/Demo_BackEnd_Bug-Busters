package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.persistencia.repositorys.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
public class UbicacionServiceImpl implements UbicacionService {

    private final UbicacionRepository ubicacionRepository;
    private final MediumDAO mediumDAO;
    private final EspirituDAO espirituDAO;

    public UbicacionServiceImpl(UbicacionRepository ubicacionRepository, MediumDAO mediumDAO, EspirituDAO espirituDAO) {
        this.ubicacionRepository = ubicacionRepository;
        this.mediumDAO = mediumDAO;
        this.espirituDAO = espirituDAO;
    }

    @Override
    public void crear(Ubicacion ubicacion) {
        if(ubicacionRepository.existeUbicacionConNombre(ubicacion.getNombre()) != null){

            throw new UbicacionYaCreadaException(ubicacion.getNombre());
        }
        ubicacionRepository.crear(ubicacion);
    }

    @Override
    public Optional<Ubicacion> recuperar(Long ubicacionId) {
        revisarId(ubicacionId);
        Ubicacion ubicacion = ubicacionRepository.recuperar(ubicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + ubicacionId + " no encontrada"));
        revisarEntidadEliminado(ubicacion.getDeleted(),ubicacion);
        return Optional.of(ubicacion);
    }

    @Override
    public UbicacionNeo4J recuperarNeo4J(Long ubicacionId) {
        return ubicacionRepository.recuperarNeo4J(ubicacionId);
    }

    @Override
    public void eliminar(Ubicacion ubicacion) {
        if (!ubicacionRepository.existsById(ubicacion.getId())) {
            throw new RecursoNoEncontradoException("Ubicación con ID " + ubicacion.getId() + " no encontrada");
        }
        revisarEntidadEliminado(ubicacion.getDeleted(),ubicacion);
        revisarUbicacionConEntidades(ubicacion.getId(),ubicacion);
        ubicacion.setDeleted(true);
        ubicacionRepository.actualizar(ubicacion);
    }

    @Override
    public void actualizar(Ubicacion ubicacion) {
        revisarId(ubicacion.getId());
        if (!ubicacionRepository.existsById(ubicacion.getId())) {
            throw new RecursoNoEncontradoException("Ubicacion con ID " + ubicacion.getId() + " no encontrado");
        }
        revisarEntidadEliminado(ubicacion.getDeleted(),ubicacion);
        ubicacionRepository.actualizar(ubicacion);
    }

    @Override
    public Collection<Ubicacion> recuperarTodos() {
        return ubicacionRepository.recuperarTodos();
    }

    @Override
    public void clearAll() {
        ubicacionRepository.eliminarTodos();
    }

    @Override
    public Optional<Ubicacion> recuperarAunConSoftDelete(Long ubicacionId) {
        revisarId(ubicacionId);
        Ubicacion ubicacion = ubicacionRepository.recuperar(ubicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + ubicacionId + " no encontrada"));
        return Optional.of(ubicacion);
    }

    @Override
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        revisarId(ubicacionId);
        return espirituDAO.espiritusEn(ubicacionId);
    }

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
        revisarId(ubicacionId);
        return mediumDAO.mediumsSinEspiritusEn(ubicacionId);
    }

    private <T> void revisarEntidadEliminado(Boolean condicion, T entidad) {
        if(condicion){
            throw new EntidadEliminadaException(entidad);
        }
    }
    private void revisarId(Long id){
        if (id == null || id <= 0) {
            throw new IdNoValidoException();
        }
    }

    private <T> void revisarUbicacionConEntidades(Long id, T entidad){
        if (!espiritusEn(id).isEmpty() || !mediumsEn(id).isEmpty()){
            throw new EntidadConEntidadesConectadasException(entidad);
        }
    }

    private List<Medium> mediumsEn(Long id){
        return mediumDAO.mediumsEn(id);
    }

    @Override
    public void conectar(Long idOrigen, Long idDestino) {
        UbicacionNeo4J origen = ubicacionRepository.recuperarNeo4J(idOrigen);
        UbicacionNeo4J destino = ubicacionRepository.recuperarNeo4J(idOrigen);
        origen.conectarse(destino);
        ubicacionRepository.actualizar(origen);
    }
}

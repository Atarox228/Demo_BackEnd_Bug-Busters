package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.persistencia.repositorys.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.exception.*;
import org.springframework.stereotype.Service;
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
        Ubicacion ubicacion = ubicacionRepository.recuperar(ubicacionId);
        revisarEntidadEliminado(ubicacion.getDeleted(),ubicacion);
        return Optional.of(ubicacion);
    }

    @Override
    public UbicacionNeo4J recuperarPorNombre(String nombre) {
        return ubicacionRepository.findByNombre(nombre);
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
        Ubicacion ubicacion = ubicacionRepository.recuperar(ubicacionId);
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



    @Override
    public Collection<UbicacionNeo4J> ubicacionesConectadas(String nombre) {

        return ubicacionRepository.ubicacionesConectadas(nombre);
    }

    @Override
    public void conectar(Long idOrigen, Long idDestino){
        revisarId(idOrigen);
        revisarId(idDestino);
        Ubicacion ubi1 = ubicacionRepository.recuperar(idOrigen);
        Ubicacion ubi2 = ubicacionRepository.recuperar(idDestino);
        if(idOrigen.equals(idDestino)){
            throw new MismaUbicacionException();
        }
        ubicacionRepository.conectarUbicaciones(ubi1.getNombre(), ubi2.getNombre());
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
    public List<UbicacionNeo4J> ubicacionesSobrecargadas(Integer umbralDeEnergia){
        return ubicacionRepository.ubicacionesSobrecargadas(umbralDeEnergia);
    }

    @Override
    public Boolean estanConectadas(Long idOrigen, Long idDestino) {
        revisarId(idOrigen);
        revisarId(idDestino);
        Ubicacion ubi1 = ubicacionRepository.recuperar(idOrigen);
        Ubicacion ubi2 = ubicacionRepository.recuperar(idDestino);
        return ubicacionRepository.estanConectadas(ubi1.getNombre(), ubi2.getNombre());
    }

    @Override
    public List<UbicacionNeo4J> caminoMasCorto(Long idOrigen, Long idDestino) {
        revisarId(idOrigen);
        revisarId(idDestino);
        Ubicacion ubi1 = ubicacionRepository.recuperar(idOrigen);
        Ubicacion ubi2 = ubicacionRepository.recuperar(idDestino);

        List<UbicacionNeo4J> camino = ubicacionRepository.encontrarCaminoMasCorto(ubi1.getNombre(), ubi2.getNombre());
        if (camino.isEmpty()) {
            // exception
        }
        return camino;
    }
}

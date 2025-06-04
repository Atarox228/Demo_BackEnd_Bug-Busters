package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.DegreeQuery;
import ar.edu.unq.epersgeist.modelo.enums.DegreeType;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.servicios.exception.sinResultadosException;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.servicios.UbicacionService;
import ar.edu.unq.epersgeist.servicios.exception.*;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public void crear(Ubicacion ubicacion, List<Coordenada> area) {
        if(ubicacionRepository.existeUbicacionConNombre(ubicacion.getNombre()) != null){
            throw new UbicacionYaCreadaException(ubicacion.getNombre());
        }
        ubicacionRepository.crear(ubicacion, area);
    }

    @Override
    public Optional<Ubicacion> recuperar(Long ubicacionId) {
        validacionesGenerales.revisarId(ubicacionId);
        Ubicacion ubicacion = ubicacionRepository.recuperar(ubicacionId);
        validacionesGenerales.revisarEntidadEliminado(ubicacion.getDeleted(),ubicacion);
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
        validacionesGenerales.revisarEntidadEliminado(ubicacion.getDeleted(),ubicacion);
        validacionesGenerales.revisarUbicacionConEntidades(ubicacion,!espiritusEn(ubicacion.getId()).isEmpty() || !mediumsEn(ubicacion.getId()).isEmpty());
        ubicacion.setDeleted(true);
        ubicacionRepository.actualizar(ubicacion);
        ubicacionRepository.eliminar(ubicacion);
    }

    @Override
    public void actualizar(Ubicacion ubicacion, String nombreViejo) {
        validacionesGenerales.revisarId(ubicacion.getId());
        if (!ubicacionRepository.existsById(ubicacion.getId())) {
            throw new RecursoNoEncontradoException("Ubicacion con ID " + ubicacion.getId() + " no encontrado");
        }
        validacionesGenerales.revisarEntidadEliminado(ubicacion.getDeleted(),ubicacion);
        ubicacionRepository.actualizarNeo4J(ubicacion,nombreViejo);
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
    public List<Espiritu> espiritusEn(Long ubicacionId) {
        validacionesGenerales.revisarId(ubicacionId);
        return espirituDAO.espiritusEn(ubicacionId);
    }

    @Override
    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId) {
        validacionesGenerales.revisarId(ubicacionId);
        return mediumDAO.mediumsSinEspiritusEn(ubicacionId);
    }

    @Override
    public void conectar(Long idOrigen, Long idDestino){
        validacionesGenerales.revisarId(idOrigen);
        validacionesGenerales.revisarId(idDestino);
        Ubicacion ubi1 = ubicacionRepository.recuperar(idOrigen);
        Ubicacion ubi2 = ubicacionRepository.recuperar(idDestino);
        if(idOrigen.equals(idDestino)){
            throw new MismaUbicacionException();
        }
        ubicacionRepository.conectarUbicaciones(ubi1.getNombre(), ubi2.getNombre());
    }

    @Override
    public List<UbicacionNeo4J> ubicacionesSobrecargadas(Integer umbralDeEnergia){
        return ubicacionRepository.ubicacionesSobrecargadas(umbralDeEnergia);
    }

    @Override
    public Boolean estanConectadas(Long idOrigen, Long idDestino) {
        validacionesGenerales.revisarId(idOrigen);
        validacionesGenerales.revisarId(idDestino);
        Ubicacion ubi1 = ubicacionRepository.recuperar(idOrigen);
        Ubicacion ubi2 = ubicacionRepository.recuperar(idDestino);
        return ubicacionRepository.estanConectadasDirecta(ubi1.getNombre(), ubi2.getNombre());
    }

    @Override
    public List<UbicacionNeo4J> caminoMasCorto(Long idOrigen, Long idDestino) {
        validacionesGenerales.revisarId(idOrigen);
        validacionesGenerales.revisarId(idDestino);
        Ubicacion ubi1 = ubicacionRepository.recuperar(idOrigen);
        Ubicacion ubi2 = ubicacionRepository.recuperar(idDestino);

        List<UbicacionNeo4J> camino = ubicacionRepository.encontrarCaminoMasCorto(ubi1.getNombre(), ubi2.getNombre());
        if (camino.isEmpty()) {
            throw new UbicacionesNoConectadasException();
        }
        return camino;
    }

    @Override
    public List<ClosenessResult> closenessOf(List<Long> ids) {
        List<ClosenessResult> closeness = new ArrayList<>();
        for (Long id : ids) {
            ClosenessResult recordCloseness = ubicacionRepository.definirCentralidad(ubicacionRepository.recuperar(id).getNombre());
            closeness.add(recordCloseness);
        }
        return closeness;
    }

    private List<Medium> mediumsEn(Long id){
        return mediumDAO.mediumsEn(id);
    }

    @Override
    public DegreeResult degreeOf(List<Long> ids, DegreeType type) {
        List<String> names = ubicacionRepository.namesOf(ids);
        DegreeQuery query = ubicacionRepository.DegreeOf(names, type);
        double cantRelationships = ubicacionRepository.relationships();
        if (query == null) throw new sinResultadosException();
        DegreeResult result = new DegreeResult(query.node(), query.degree() / cantRelationships, type);
        return result;
    }

}
package ar.edu.unq.epersgeist.persistencia.repositorys.impl;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.ClosenessResult;
import ar.edu.unq.epersgeist.modelo.DegreeQuery;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.modelo.UbicacionNeo4J;
import ar.edu.unq.epersgeist.modelo.enums.DegreeType;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAONeo4j;
import ar.edu.unq.epersgeist.persistencia.repositorys.interfaces.UbicacionRepository;
import org.springframework.stereotype.Repository;


import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UbicacionRepositoryImpl implements UbicacionRepository {

    private final UbicacionDAO ubicacionDAO;
    private final UbicacionDAONeo4j ubicacionDAONeo4J;

    public UbicacionRepositoryImpl(UbicacionDAO ubicacionDAO, UbicacionDAONeo4j ubicacionDAONeo4J) {
        this.ubicacionDAO = ubicacionDAO;
        this.ubicacionDAONeo4J = ubicacionDAONeo4J;
    }

    @Override
    public void crear(Ubicacion ubicacion){
        UbicacionNeo4J ubicacionNeo = new UbicacionNeo4J(ubicacion.getNombre(),ubicacion.getTipo(),ubicacion.getFlujoEnergia());
        ubicacionDAO.save(ubicacion);
        ubicacionDAONeo4J.save(ubicacionNeo);
    }

    @Override
    public Ubicacion recuperar(Long ubicacionId) {
        return ubicacionDAO.findById(ubicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + ubicacionId + " no encontrada"));
    }


    @Override
    public UbicacionNeo4J findByNombre(String nombre) {
        return ubicacionDAONeo4J.findByNombre(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con nombre " + nombre + " no encontrada"));
    }

    @Override
    public void actualizar(Ubicacion ubicacion) {
        ubicacionDAO.save(ubicacion);
    }

    @Override
    public void actualizarNeo4J(UbicacionNeo4J ubicacion) {
        ubicacionDAONeo4J.save(ubicacion);
    }

    @Override
    public void eliminar(Ubicacion ubicacion) {
        UbicacionNeo4J ubicacionNeo = ubicacionDAONeo4J.findByNombre(ubicacion.getNombre()).get();
        ubicacionDAO.save(ubicacion);
        ubicacionDAONeo4J.delete(ubicacionNeo);
    }

    @Override
    public Collection<Ubicacion> recuperarTodos() {
        return ubicacionDAO.recuperarTodosNoEliminados();
    }


    @Override
    public boolean existsById(Long id) {
        return ubicacionDAO.existsById(id);
    }

    @Override
    public void eliminarTodos() {
        ubicacionDAO.deleteAll();
        ubicacionDAONeo4J.detachDelete();
    }

    @Override
    public Ubicacion existeUbicacionConNombre(String nombre) {
        return ubicacionDAO.existeUbicacionConNombre(nombre);
    }

    @Override
    public List<UbicacionNeo4J> ubicacionesSobrecargadas(Integer umbralDeEnergia){
        return ubicacionDAONeo4J.ubicacionesSobrecargadas(umbralDeEnergia);
    }

    @Override
    public void conectarUbicaciones(String origen, String destino){
        ubicacionDAONeo4J.conectarUbicaciones(origen, destino);
    }

    @Override
    public Boolean estanConectadasDirecta(String origen, String destino) {
        return ubicacionDAONeo4J.estanConectadasDirecta(origen, destino);
    }

    @Override
    public List<UbicacionNeo4J> encontrarCaminoMasCorto(String origen, String destino) {
        return ubicacionDAONeo4J.encontrarCaminoMasCorto(origen, destino);
    }

    @Override
    public ClosenessResult definirCentralidad(String nombre) {
        List<Ubicacion> todasLasUbicaciones = ubicacionDAO.recuperarTodosNoEliminados();
        Double sumatoriaDistancia = 0.0;
        for (Ubicacion ubi : todasLasUbicaciones){
            if (! nombre.equals(ubi.getNombre())) {
                sumatoriaDistancia += cantidadSaltos(nombre, ubi.getNombre());
            }
        }
        Double centralidad = 1.0 / sumatoriaDistancia;
        return new ClosenessResult(ubicacionDAONeo4J.recuperarPorNombre(nombre), centralidad);
    }

    @Override
    public double relationships() {
        return ubicacionDAONeo4J.relationships();
    }

    @Override
    public DegreeQuery DegreeOf(List<String> names, DegreeType type) {
        DegreeQuery query;
        if (type == DegreeType.OUTCOMMING){
            query = ubicacionDAONeo4J.degreeOutcommingOf(names);
        } else if (type == DegreeType.INCOMMING) {
            query = ubicacionDAONeo4J.degreeIncommingOf(names);
        } else {
            query = ubicacionDAONeo4J.degreeAllOf(names);
        }
        return query;
    }

    @Override
    public List<String> namesOf(List<Long> ids) {
        return ubicacionDAO.findAllById(ids).stream().map(Ubicacion::getNombre).collect(Collectors.toList());
    }

    private double cantidadSaltos(String origen, String destino) {
        List <UbicacionNeo4J> saltos = ubicacionDAONeo4J.encontrarCaminoMasCorto(origen, destino);
        if (saltos.isEmpty()) {
            return 10.0; // ESTO ES PARA PROBAR, PODRIA HACER UN METODO QUE TOMA EL MAS LARGO DE LOS CAMINOS Y SUMARSELO
        } else {
            return saltos.size() -1;
        }
    }
}

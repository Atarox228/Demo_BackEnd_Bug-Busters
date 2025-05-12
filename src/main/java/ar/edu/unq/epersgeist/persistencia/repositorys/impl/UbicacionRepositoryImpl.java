package ar.edu.unq.epersgeist.persistencia.repositorys.impl;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.Ubicacion;

import ar.edu.unq.epersgeist.modelo.UbicacionNeo4J;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAONeo4j;
import ar.edu.unq.epersgeist.persistencia.repositorys.interfaces.UbicacionRepository;
import org.springframework.stereotype.Component;


import java.util.Collection;
import java.util.Optional;

@Component
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
    public Optional<Ubicacion> recuperar(long ubicacionId) {
        Ubicacion ubicacion = ubicacionDAO.findById(ubicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + ubicacionId + " no encontrada"));
        return Optional.of(ubicacion);
    }

    @Override
    public void actualizar(Ubicacion ubicacion) {
        ubicacionDAO.save(ubicacion);
    }

    @Override
    public void actualizar(Ubicacion ubicacion, UbicacionNeo4J ubicacionNeo) {
        ubicacionDAO.save(ubicacion);
        ubicacionDAONeo4J.save(ubicacionNeo);
    }

    @Override
    public void eliminar(Ubicacion ubicacion) {
        UbicacionNeo4J ubicacionNeo = ubicacionDAONeo4J.findByNombre(ubicacion.getNombre()).get();
        ubicacionDAO.save(ubicacion);
        ubicacionDAONeo4J.delete(ubicacionNeo);
    }

//    @Override
//    public void eliminar(Ubicacion ubicacion, UbicacionNeo4J ubicacionNeo) {
//        ubicacionDAO.save(ubicacion);
//        ubicacionDAONeo4J.delete(ubicacionNeo);
//    }

    @Override
    public Collection<Ubicacion> recuperarTodos() {
        return ubicacionDAO.recuperarTodosNoEliminados();
    }

    @Override
    public Optional<Ubicacion> recuperarAunConSoftDelete(Long ubicacionId) {
        Ubicacion ubicacion = ubicacionDAO.findById(ubicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + ubicacionId + " no encontrada"));
        return Optional.of(ubicacion);
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


}

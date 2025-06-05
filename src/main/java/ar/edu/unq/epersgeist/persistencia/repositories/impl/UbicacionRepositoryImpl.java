package ar.edu.unq.epersgeist.persistencia.repositories.impl;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.modelo.enums.DegreeType;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAOMongo;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAONeo4j;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.UbicacionRepository;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UbicacionRepositoryImpl implements UbicacionRepository {

    private final UbicacionDAO ubicacionDAO;
    private final UbicacionDAONeo4j ubicacionDAONeo4J;
    private final UbicacionDAOMongo ubicacionDAOMongo;

    public UbicacionRepositoryImpl(UbicacionDAO ubicacionDAO, UbicacionDAONeo4j ubicacionDAONeo4J, UbicacionDAOMongo ubicacionDAOMongo) {
        this.ubicacionDAO = ubicacionDAO;
        this.ubicacionDAONeo4J = ubicacionDAONeo4J;
        this.ubicacionDAOMongo = ubicacionDAOMongo;
    }

    @Override
    public void crear(Ubicacion ubicacion, GeoJsonPolygon area){
        UbicacionNeo4J ubicacionNeo = new UbicacionNeo4J(ubicacion.getNombre(),ubicacion.getTipo(),ubicacion.getFlujoEnergia());
        UbicacionMongo ubicacionMongo = new UbicacionMongo(ubicacion.getNombre(),ubicacion.getTipo(),ubicacion.getFlujoEnergia(), area);
        ubicacionDAO.save(ubicacion);
        ubicacionDAONeo4J.save(ubicacionNeo);
        ubicacionDAOMongo.save(ubicacionMongo);
    }

    @Override
    public Ubicacion recuperar(Long ubicacionId) {
        return ubicacionDAO.findById(ubicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con ID " + ubicacionId + " no encontrada"));
    }

    @Override
    public Ubicacion recupoerarPorNombre(String nombre) {
        return ubicacionDAO.recuperarPorNombre(nombre);
    }

    @Override
    public UbicacionNeo4J findByNombre(String nombre) {
        return ubicacionDAONeo4J.findByNombre(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con nombre " + nombre + " no encontrada"));
    }

    @Override
    public UbicacionMongo findByNombreMongo(String nombre) {
        return ubicacionDAOMongo.findByNombreMongo(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación con nombre " + nombre + " no encontrada"));
    }

    @Override
    public void actualizar(Ubicacion ubicacion) {
        ubicacionDAO.save(ubicacion);
    }

    @Override
    public void actualizarNeo4J(Ubicacion ubicacion, String nombreViejo) {
        ubicacionDAONeo4J.actualizarNeo(nombreViejo,ubicacion.getNombre(),ubicacion.getFlujoEnergia());
    }

    @Override
    public void eliminar(Ubicacion ubicacion) {
        UbicacionNeo4J ubicacionNeo = ubicacionDAONeo4J.findByNombre(ubicacion.getNombre()).get();
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
        ubicacionDAOMongo.deleteAll();
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
        return ubicacionDAONeo4J.closenessResult(nombre);
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

    @Override
    public List<UbicacionMongo> recuperarPorInterseccion(GeoJsonPolygon area) {
        return ubicacionDAOMongo.recuperarPorInterseccion(area);
    }

    @Override
    public UbicacionMongo recuperarPorCoordenada(Point coordenada) {
        GeoJsonPoint geoJsonPoint = new GeoJsonPoint(coordenada.getX(), coordenada.getY());
        return ubicacionDAOMongo.recuperarPorCoordenada(geoJsonPoint)
                .orElseThrow(() -> new RecursoNoEncontradoException("Ubicación no encontrada"));
    }
}
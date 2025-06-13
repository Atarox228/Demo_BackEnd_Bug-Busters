package ar.edu.unq.epersgeist.persistencia.repositories.impl;

import ar.edu.unq.epersgeist.controller.excepciones.RecursoNoEncontradoException;
import ar.edu.unq.epersgeist.modelo.SnapShot;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.SnapShotMongoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


@Repository
public class SnapShotMongoRepositoryImpl implements SnapShotMongoRepository {

    private final UbicacionDAONeo4j ubicacionDAONeo4j;
    private SnapShotDAOMongo snapshotMongoDAO;
    private AreaDAOMongo areaDAOMongo;
    private CoordenadaDAOMongo coordenadaDAOMongo;
    private MediumDAO mediumDAO;
    private EspirituDAO espirituDAO;
    private UbicacionDAO ubicacionDAO;

    public SnapShotMongoRepositoryImpl(
            SnapShotDAOMongo snapshotMongoDAO,
            MediumDAO mediumDAO,
            CoordenadaDAOMongo coordenadaDAO,
            EspirituDAO espirituDAO,
            UbicacionDAO ubicacionDAO,
            AreaDAOMongo areaDAOMongo,
            UbicacionDAONeo4j ubicacionDAONeo4j) {
        this.snapshotMongoDAO = snapshotMongoDAO;
        this.mediumDAO = mediumDAO;
        this.coordenadaDAOMongo = coordenadaDAO;
        this.espirituDAO = espirituDAO;
        this.ubicacionDAO = ubicacionDAO;
        this.areaDAOMongo = areaDAOMongo;
        this.ubicacionDAONeo4j = ubicacionDAONeo4j;
    }


    @Override
    public void crearSnapShot() {
        LocalDate date = LocalDate.now();

        Map<String, Object> datosSQL = obtenerDatosDePostgres();
        Map<String, Object> datosMongo = obtenerDatosDeMongo();
        Map<String, Object> datosNeo4j = obtenerDatosDeNeo4j();

        SnapShot snapshot = new SnapShot(date,datosSQL,datosMongo,datosNeo4j);
        this.snapshotMongoDAO.save(snapshot);
    }



    @Override
    public SnapShot getSnapshot(LocalDate date) {

        return this.snapshotMongoDAO.findTop1ByDate(date)
                .orElseThrow(() -> new RecursoNoEncontradoException("Snapshot con fecha:" + date.toString() + " no encontrada"));
    }

    @Override
    public void eliminarTodo() {
        this.snapshotMongoDAO.deleteAll();
    }

    private Map<String, Object> obtenerDatosDePostgres() {

        Map<String, Object> datos = new HashMap<>();
        datos.put("Espiritus", espirituDAO.recuperarTodosNoEliminados());
        datos.put("Mediums", mediumDAO.recuperarTodosNoEliminados());
        datos.put("Ubicaciones", ubicacionDAO.recuperarTodosNoEliminados());
        return datos;
    }

    private Map<String, Object> obtenerDatosDeNeo4j() {
        Map<String, Object> datos = new HashMap<>();
        datos.put("Ubicaciones", ubicacionDAONeo4j.findAll());
        return datos;
    }

    private Map<String, Object> obtenerDatosDeMongo() {
        Map<String, Object> datos = new HashMap<>();
        datos.put("Areas",areaDAOMongo.findAll());
        datos.put("Coordenadas",coordenadaDAOMongo.findAll());
        return datos;
    }
}

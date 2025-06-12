package ar.edu.unq.epersgeist.persistencia.repositories.impl;

import ar.edu.unq.epersgeist.modelo.SnapShot;
import ar.edu.unq.epersgeist.persistencia.dao.CoordenadaDAOMongo;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.SnapShotDAOMongo;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.SnapShotMongoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;


@Repository
public class SnapShotMongoRepositoryImpl implements SnapShotMongoRepository {

    private SnapShotDAOMongo snapshotMongoDAO;

    public SnapShotMongoRepositoryImpl(SnapShotDAOMongo snapshotMongoDAO) {
        this.snapshotMongoDAO = snapshotMongoDAO;
    }


    @Override
    public void crearSnapShot() {
        LocalDate date = LocalDate.now();
        SnapShot snapshot = new SnapShot(date);
        this.snapshotMongoDAO.save(snapshot);
    }

    @Override
    public SnapShot getSnapshot(LocalDate date) {
        SnapShot snap = this.snapshotMongoDAO.findTop1ByDate(date);
        return snap;
    }
}

package ar.edu.unq.epersgeist.persistencia.repositories.interfaces;

import ar.edu.unq.epersgeist.modelo.SnapShot;

import java.time.LocalDate;

public interface SnapShotMongoRepository {
    void crearSnapShot();
    SnapShot getSnapshot(LocalDate date);
}

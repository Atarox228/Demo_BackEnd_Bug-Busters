package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.SnapShot;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SnapShotDAOMongo extends MongoRepository<SnapShot, String> {




    Optional<SnapShot> findTop1ByDate(@NotNull LocalDate date);
}

package ar.edu.unq.epersgeist.servicios.impl;


import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.SnapShotMongoRepository;
import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import ar.edu.unq.epersgeist.servicios.exception.NoHaySantuariosConDemoniosException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.scheduler.Schedulers;


import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@Transactional
public class EstadisticaServiceImpl implements EstadisticaService {

    private final MediumDAO mediumDAO;
    private final EspirituDAO espirituDAO;
    private final SnapShotMongoRepository snapshotMongoRepository;

    public EstadisticaServiceImpl(EspirituDAO espirituDAO, MediumDAO mediumDAO, SnapShotMongoRepository snapshotMongoRepository) {
        this.espirituDAO = espirituDAO;
        this.mediumDAO = mediumDAO;
        this.snapshotMongoRepository = snapshotMongoRepository;
    }

    @Override
    public ReporteSantuarioMasCorrupto santuarioCorrupto() {
        List<Ubicacion> santuarios  = espirituDAO.santuariosCorruptos();
            if (santuarios.isEmpty() || sinSanturariosCorruptos(santuarios)) {
                throw new NoHaySantuariosConDemoniosException();
            }
        Ubicacion santuarioCorrupto = espirituDAO.santuariosCorruptos().getFirst();
        Medium mediumEndemoniado    = mediumDAO.mediumConMasDemoniosEn(santuarioCorrupto.getId()).orElseThrow(() -> new NoSuchElementException("No se encuentran mediums con demonios en este santuario"));
        Integer cantDemoniosTotal   = espirituDAO.demoniosEn(santuarioCorrupto.getId()).size();
        Integer cantDemoniosLibres  = espirituDAO.demoniosLibresEn(santuarioCorrupto.getId()).size();
        return new ReporteSantuarioMasCorrupto(santuarioCorrupto.getNombre(), mediumEndemoniado, cantDemoniosTotal, cantDemoniosLibres);
    }

    @Override
    public void snapshot() {
        this.snapshotMongoRepository.crearSnapShot();
    }

    @Override
    public SnapShot obtenerSnapshot(LocalDate date) {
        SnapShot snapshot = this.snapshotMongoRepository.getSnapshot(date);
        return snapshot;
    }


    private boolean sinSanturariosCorruptos(List<Ubicacion> santuarios){

        return espirituDAO.demoniosEn(santuarios.getFirst().getId()).isEmpty();
    }


}

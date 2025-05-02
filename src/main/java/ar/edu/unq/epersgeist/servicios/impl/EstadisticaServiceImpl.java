package ar.edu.unq.epersgeist.servicios.impl;


import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.modelo.ReporteSantuarioMasCorrupto;
import ar.edu.unq.epersgeist.modelo.Ubicacion;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.EstadisticaService;
import ar.edu.unq.epersgeist.servicios.exception.NoHaySantuariosConDemoniosException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.NoSuchElementException;


@Service
@Transactional
public class EstadisticaServiceImpl implements EstadisticaService {

    private final UbicacionDAO ubicacionDAO;
    private final MediumDAO mediumDAO;
    private final EspirituDAO espirituDAO;

    public EstadisticaServiceImpl(EspirituDAO espirituDAO, MediumDAO mediumDAO, UbicacionDAO ubicacionDAO) {
        this.espirituDAO = espirituDAO;
        this.mediumDAO = mediumDAO;
        this.ubicacionDAO = ubicacionDAO;
    }

    @Override
    public ReporteSantuarioMasCorrupto santuarioCorrupto() {
        List<Ubicacion> santuarios  = espirituDAO.santuariosCorruptos();
            if (santuarios.isEmpty()) {
                throw new NoHaySantuariosConDemoniosException();
            }
        Ubicacion santuarioCorrupto = espirituDAO.santuariosCorruptos().get(0);
        Medium mediumEndemoniado    = mediumDAO.mediumConMasDemoniosEn(santuarioCorrupto.getId()).orElseThrow(() -> new NoSuchElementException("No se encuentran mediums con demonios en este santuario"));
        Integer cantDemoniosTotal   = espirituDAO.demoniosEn(santuarioCorrupto.getId()).size();
        Integer cantDemoniosLibres  = espirituDAO.demoniosLibresEn(santuarioCorrupto.getId()).size();
        return new ReporteSantuarioMasCorrupto(santuarioCorrupto.getNombre(), mediumEndemoniado, cantDemoniosTotal, cantDemoniosLibres);
    }


}

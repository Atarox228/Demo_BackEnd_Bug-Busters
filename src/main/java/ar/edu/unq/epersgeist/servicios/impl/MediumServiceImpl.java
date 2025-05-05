package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.MediumService;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.exception.MovimientoInvalidoException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MediumServiceImpl implements MediumService {
    public final MediumDAO mediumDAO;
    public final EspirituDAO espirituDAO;
    public final UbicacionDAO ubicacionDAO;

    public MediumServiceImpl(MediumDAO mediumDAO, EspirituDAO espirituDAO, UbicacionDAO ubicacionDAO) {
        this.mediumDAO = mediumDAO;
        this.espirituDAO = espirituDAO;
        this.ubicacionDAO = ubicacionDAO;
    }

    @Override
    public void crear(Medium medium) {
        if (medium.getId() != null) {
            throw new IdNoValidoException(null);
        }
        mediumDAO.save(medium);
    }

    @Override
    public Optional <Medium> recuperar(Long id) {
        return mediumDAO.findById(id);
    }

    @Override
    public Collection<Medium> recuperarTodos() {
        return mediumDAO.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
    }

    @Override
    public void eliminar(Medium medium) {
        mediumDAO.delete(medium);
    }

    @Override
    public void eliminarTodo() {
        mediumDAO.deleteAll();
    }

    @Override
    public void actualizar(Medium medium) {
        if (medium.getId() == null || !mediumDAO.existsById(medium.getId())) {
            throw new IdNoValidoException(medium.getId());
        }
        mediumDAO.save(medium);
    }

    @Override
    public void exorcizar(long idMedium, long idMedium2) {
        Medium medium = mediumDAO.findById(idMedium)
                .orElseThrow(() -> new IdNoValidoException(idMedium));
        Medium medium2 = mediumDAO.findById(idMedium2)
                .orElseThrow(() -> new IdNoValidoException(idMedium2));
        List<Espiritu> angeles = espirituDAO.recuperarEspiritusDeTipo(medium.getId(), Angel.class);
        List<Espiritu> demonios = espirituDAO.recuperarEspiritusDeTipo(medium2.getId(), Demonio.class);
        medium.exorcizar(medium2, angeles, demonios);
        mediumDAO.save(medium);
        mediumDAO.save(medium2);
    }

    @Override
    public Optional<Espiritu> invocar(Long mediumId, Long espirituId) {
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new IdNoValidoException(mediumId));
        Espiritu espiritu = espirituDAO.findById(espirituId)
                .orElseThrow(() -> new IdNoValidoException(espirituId));
        medium.invocar(espiritu);
        mediumDAO.save(medium);
        return espirituDAO.findById(espirituId);
    }

    @Override
    public List<Espiritu> espiritus(Long idMedium) {
        mediumDAO.findById(idMedium)
                .orElseThrow(() -> new IdNoValidoException(idMedium));
        return mediumDAO.obtenerEspiritus(idMedium);
    }

    @Override
    public void descansar(Long mediumId){
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new IdNoValidoException(mediumId));
        medium.descansar();
        mediumDAO.save(medium);
    }

    @Override
    public void mover(Long mediumId, Long ubicacionId) {
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new IdNoValidoException(mediumId));
        Ubicacion ubicacion = ubicacionDAO.findById(ubicacionId)
                .orElseThrow(() -> new IdNoValidoException(ubicacionId));
        if (medium.getUbicacion() != null && medium.getUbicacion().getId().equals(ubicacion.getId())) throw new MovimientoInvalidoException();
        medium.moverseA(ubicacion);
        ubicacionDAO.save(ubicacion);
        mediumDAO.save(medium);
    }
}
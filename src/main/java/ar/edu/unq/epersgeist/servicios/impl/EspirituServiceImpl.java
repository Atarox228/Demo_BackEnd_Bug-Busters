package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.impl.validacionesGenerales;
import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import ar.edu.unq.epersgeist.servicios.exception.*;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EspirituServiceImpl implements EspirituService {

    private final EspirituDAO espirituDAO;
    private final MediumDAO mediumDAO;

    public EspirituServiceImpl(EspirituDAO espirituDAO, MediumDAO mediumDao) {
        this.espirituDAO = espirituDAO;
        this.mediumDAO = mediumDao;
    }


    @Override
    public void crear(Espiritu espiritu) {
        this.espirituDAO.save(espiritu);
    }

    @Override
    public Optional<Espiritu> recuperar(Long espirituId) {
        validacionesGenerales.revisarId(espirituId);
        Espiritu espiritu = espirituDAO.findById(espirituId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espiritu con ID " + espirituId + " no encontrado"));
        validacionesGenerales.revisarEntidadEliminado(espiritu.getDeleted(),espiritu);
        return Optional.of(espiritu);
    }

    @Override
    public List<Espiritu> recuperarTodos() {
        return  espirituDAO.recuperarTodosNoEliminados();
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        validacionesGenerales.revisarId(espiritu.getId());
        if (!espirituDAO.existsById(espiritu.getId())) {
            throw new RecursoNoEncontradoException("Espiritu con ID " + espiritu.getId() + " no encontrado");
        }
        validacionesGenerales.revisarEntidadEliminado(espiritu.getDeleted(),espiritu);
        espirituDAO.save(espiritu);
    }

    @Override
    public void eliminar(Espiritu espiritu) {
        if (!espirituDAO.existsById(espiritu.getId())) {
            throw new RecursoNoEncontradoException("Espiritu con ID " + espiritu.getId() + " no encontrado");
        }
        validacionesGenerales.revisarEntidadEliminado(espiritu.getDeleted(),espiritu);
        espiritu.setDeleted(true);
        espirituDAO.save(espiritu);
    }

    public Optional<Medium> conectar(Long espirituId, Long mediumId) {
        validacionesGenerales.revisarId(espirituId);
        validacionesGenerales.revisarId(mediumId);

        Espiritu espiritu = espirituDAO.findById(espirituId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espiritu con ID " + espirituId + " no encontrado"));
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + mediumId + " no encontrado"));
        
        validacionesGenerales.revisarEntidadEliminado(espiritu.getDeleted(),espiritu);
        validacionesGenerales.revisarEntidadEliminado(medium.getDeleted(),medium);
        
        validacionesGenerales.revisarUbicacionNoNula(espiritu.getUbicacion(),espiritu,espirituId);
        validacionesGenerales.revisarUbicacionNoNula(medium.getUbicacion(),medium,mediumId);
        
        medium.conectarseAEspiritu(espiritu);
        mediumDAO.save(medium);
        return mediumDAO.findById(mediumId);
    }

    @Override
    public List<Espiritu> espiritusDemoniacos(Direccion direccion, Integer pagina, Integer cantidadPorPagina) {
        if (pagina < 1 || cantidadPorPagina < 0){
            throw new PaginaInvalidaException();
        }
        Sort.Direction direccionOrden = (direccion == Direccion.DESCENDENTE) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pagina - 1, cantidadPorPagina, Sort.by(direccionOrden, "nivelConexion"));
        return espirituDAO.findDemonios(pageable).getContent();
    }

    @Override
    public Optional<Espiritu> recuperarAunConSoftDelete(Long espirituId) {
        validacionesGenerales.revisarId(espirituId);
        Espiritu espiritu = espirituDAO.findById(espirituId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espiritu con ID " + espirituId + " no encontrado"));
        return Optional.of(espiritu);
    }

}

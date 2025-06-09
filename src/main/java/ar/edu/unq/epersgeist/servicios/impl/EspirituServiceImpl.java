package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.*;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.CoordenadaRepository;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.EspirituRepository;
import ar.edu.unq.epersgeist.servicios.EspirituService;
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

    private final MediumDAO mediumDAO;
    private final EspirituDAO espirituDAO;
    private final CoordenadaRepository coordenadaRepository;

    public EspirituServiceImpl(EspirituDAO espirituDao, MediumDAO mediumDao, CoordenadaRepository coordenadaRepository) {
        this.espirituDAO = espirituDao;
        this.mediumDAO = mediumDao;
        this.coordenadaRepository = coordenadaRepository;
    }


    @Override
    public void crear(Espiritu espiritu) {
        espirituDAO.save(espiritu);
    }

    @Override
    public Optional<Espiritu> recuperar(Long espirituId) {
        validacionesGenerales.revisarId(espirituId);
        Optional<Espiritu> espiritu = espirituDAO.findById(espirituId);
        validacionesGenerales.revisarEntidadEliminado(espiritu.get().getDeleted(), espiritu);
        return espiritu;
    }

    @Override
    public List<Espiritu> recuperarTodos() {
        return espirituDAO.recuperarTodosNoEliminados();
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        validacionesGenerales.revisarId(espiritu.getId());
        if (!espirituDAO.existsById(espiritu.getId())) {
            throw new RecursoNoEncontradoException("Espiritu con ID " + espiritu.getId() + " no encontrado");
        }
        validacionesGenerales.revisarEntidadEliminado(espiritu.getDeleted(), espiritu);
        espirituDAO.save(espiritu);
    }

    @Override
    public void eliminar(Espiritu espiritu) {
        if (!espirituDAO.existsById(espiritu.getId())) {
            throw new RecursoNoEncontradoException("Espiritu con ID " + espiritu.getId() + " no encontrado");
        }
        validacionesGenerales.revisarEntidadEliminado(espiritu.getDeleted(), espiritu);
        espiritu.setDeleted(true);
        espirituDAO.save(espiritu);
    }

    @Override
    public Optional<Medium> conectar(Long espirituId, Long mediumId) {
        validacionesGenerales.revisarId(espirituId);
        validacionesGenerales.revisarId(mediumId);

        Optional<Espiritu> espiritu = espirituDAO.findById(espirituId);
        Medium medium = mediumDAO.findById(mediumId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medium con ID " + mediumId + " no encontrado"));

        validacionesGenerales.revisarEntidadEliminado(espiritu.get().getDeleted(), espiritu);
        validacionesGenerales.revisarEntidadEliminado(medium.getDeleted(), medium);

        validacionesGenerales.revisarUbicacionNoNula(espiritu.get().getUbicacion(), espiritu, espirituId);
        validacionesGenerales.revisarUbicacionNoNula(medium.getUbicacion(), medium, mediumId);

        medium.conectarseAEspiritu(espiritu.get());
        mediumDAO.save(medium);
        return mediumDAO.findById(mediumId);
    }

    @Override
    public List<Espiritu> espiritusDemoniacos(Direccion direccion, Integer pagina, Integer cantidadPorPagina) {
        if (pagina < 1 || cantidadPorPagina < 0) {
            throw new PaginaInvalidaException();
        }
        Sort.Direction direccionOrden = (direccion == Direccion.DESCENDENTE) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pagina - 1, cantidadPorPagina, Sort.by(direccionOrden, "nivelConexion"));
        return espirituDAO.findDemonios(pageable).getContent();
    }

    @Override
    public void dominar(Long idDominante, Long idDominado) {
        validacionesGenerales.revisarId(idDominante);
        validacionesGenerales.revisarId(idDominado);
        Optional<Espiritu> dominante = espirituDAO.findById(idDominante);
        Optional<Espiritu> dominado = espirituDAO.findById(idDominado);

        CoordenadaMongo coordenadaDominante = coordenadaRepository.findByEntityIdAndEntityType(dominante.get().getTipo().toString(), dominante.get().getId());
        CoordenadaMongo coordenadaDominado = coordenadaRepository.findByEntityIdAndEntityType(dominado.get().getTipo().toString(), dominado.get().getId());
        validacionesGenerales.revisarEntidadEliminado(dominante.get().getDeleted(), dominante);
        validacionesGenerales.revisarEntidadEliminado(dominado.get().getDeleted(), dominado);


        if (!coordenadaRepository.estaEnRangoDeDominar(coordenadaDominado, dominante.get())) {
            throw new FueraDeRangoDistanciaException();
        }

        dominante.get().dominar(dominado.get());
        espirituDAO.save(dominado.get());
    }

}

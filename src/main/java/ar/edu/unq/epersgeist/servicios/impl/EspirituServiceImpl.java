package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.controller.excepciones.*;
import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.*;
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

    private final EspirituRepository espirituRepository;
    private final MediumDAO mediumDAO;
    private final EspirituDAOMongo espirituDAOMongo;

    public EspirituServiceImpl(EspirituRepository espirituRepository, MediumDAO mediumDao, EspirituDAOMongo espirituDAOMongo) {
        this.espirituRepository = espirituRepository;
        this.mediumDAO = mediumDao;
        this.espirituDAOMongo = espirituDAOMongo;
    }


    @Override
    public void crear(Espiritu espiritu) {
        espirituRepository.crear(espiritu);
    }

    @Override
    public Optional<Espiritu> recuperar(Long espirituId) {
        validacionesGenerales.revisarId(espirituId);
        Espiritu espiritu = espirituRepository.recuperar(espirituId);
        validacionesGenerales.revisarEntidadEliminado(espiritu.getDeleted(),espiritu);
        return Optional.of(espiritu);
    }

    @Override
    public EspirituMongo recuperarMongo(Long id) {
        return espirituRepository.recuperarMongo(id.toString());
    }

    @Override
    public List<Espiritu> recuperarTodos() {
        return  espirituRepository.recuperarTodosNoEliminados();
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        validacionesGenerales.revisarId(espiritu.getId());
        if (!espirituRepository.existsById(espiritu.getId())) {
            throw new RecursoNoEncontradoException("Espiritu con ID " + espiritu.getId() + " no encontrado");
        }
        validacionesGenerales.revisarEntidadEliminado(espiritu.getDeleted(),espiritu);
        espirituRepository.actualizar(espiritu);
    }

    @Override
    public void actualizarMongo(EspirituMongo espiritu) {
        espirituRepository.actualizarMongo(espiritu);
    }

    @Override
    public void eliminar(Espiritu espiritu) {
        if (!espirituRepository.existsById(espiritu.getId())) {
            throw new RecursoNoEncontradoException("Espiritu con ID " + espiritu.getId() + " no encontrado");
        }
        validacionesGenerales.revisarEntidadEliminado(espiritu.getDeleted(),espiritu);
        espiritu.setDeleted(true);
        espirituRepository.crear(espiritu);
    }

    public Optional<Medium> conectar(Long espirituId, Long mediumId) {
        validacionesGenerales.revisarId(espirituId);
        validacionesGenerales.revisarId(mediumId);

        Espiritu espiritu = espirituRepository.recuperar(espirituId);
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
        return espirituRepository.findDemonios(pageable).getContent();
    }

    @Override
    public void dominar(Long idDominante, Long idDominado) {
        validacionesGenerales.revisarId(idDominante);
        validacionesGenerales.revisarId(idDominado);
        Espiritu dominante = espirituRepository.recuperar(idDominante);
        Espiritu dominado = espirituRepository.recuperar(idDominado);
        EspirituMongo dominator = espirituRepository.recuperarMongo(idDominante.toString());
        EspirituMongo dominated = espirituRepository.recuperarMongo(idDominado.toString());

        validacionesGenerales.revisarEntidadEliminado(dominante.getDeleted(),dominante);
        validacionesGenerales.revisarEntidadEliminado(dominado.getDeleted(),dominado);

        validacionesGenerales.revisarUbicacionNoNula(dominante.getUbicacion(),dominante,idDominante);
        validacionesGenerales.revisarUbicacionNoNula(dominado.getUbicacion(),dominado,idDominado);

        if (!espirituRepository.estaEnRango(dominator, dominated)) {
            throw new FueraDeRangoDistanciaException();
        }
        dominante.dominar(dominado);
        espirituRepository.actualizar(dominado);
    }






}

package ar.edu.unq.epersgeist.servicios.impl;

import ar.edu.unq.epersgeist.modelo.*;
import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.servicios.EspirituService;
import ar.edu.unq.epersgeist.servicios.enums.Direccion;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import ar.edu.unq.epersgeist.servicios.exception.PaginaInvalidaException;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
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
    private final UbicacionDAO ubicacionDAO;

    public EspirituServiceImpl(EspirituDAO espirituDAO, MediumDAO mediumDao, UbicacionDAO ubicacionDAO) {
        this.espirituDAO = espirituDAO;
        this.mediumDAO = mediumDao;
        this.ubicacionDAO = ubicacionDAO;
    }


    @Override
    public void crear(Espiritu espiritu) {
        if(espiritu.getId() != null){
            throw new IdNoValidoException(null);
        }
        this.espirituDAO.save(espiritu);
    }

    @Override
    public Espiritu recuperar(Long espirituId) {
        if (espirituId == null) {
            throw new IdNoValidoException(null);
        }
        return espirituDAO.findById(espirituId).orElseThrow(() -> new IdNoValidoException(espirituId));
    }

    @Override
    public List<Espiritu> recuperarTodos() {
        List<Espiritu> espiritus = espirituDAO.findAll(Sort.by(Sort.Direction.ASC, "nombre"));
        return  espiritus;
    }

    @Override
    public void actualizar(Espiritu espiritu) {
        if (espiritu.getId() == null) {
            throw new IdNoValidoException(null);
        }
        espirituDAO.save(espiritu);
    }

    @Override
    public void eliminar(Espiritu espiritu) {
        espirituDAO.deleteById(espiritu.getId());
    }

    public void eliminarTodo(){
        espirituDAO.deleteAll();
    }

    public Medium conectar(Long espirituId, Long mediumId) {
        Optional<Espiritu> espiritu = espirituDAO.findById(espirituId);
        Optional<Medium> medium = mediumDAO.findById(mediumId);
        medium.get().conectarseAEspiritu(espiritu.get());
        espirituDAO.save(espiritu.get());
        mediumDAO.save(medium.get());
        return mediumDAO.findById(mediumId).get();
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
//
//    public void ubicarseEn(Long idEspiritu, Long idUbicacion) {
//        Optional<Espiritu> espiritu = espirituDAO.findById(idEspiritu);
//        Optional<Ubicacion> ubicacion = ubicacionDAO.findById(idUbicacion);
//        espiritu.get().setUbicacion(ubicacion.get());
//        espirituDAO.save(espiritu.get());
//    }
}

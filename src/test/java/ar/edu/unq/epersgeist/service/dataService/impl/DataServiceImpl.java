package ar.edu.unq.epersgeist.service.dataService.impl;

import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.repositories.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.service.dataService.DataService;
import ar.edu.unq.epersgeist.servicios.exception.IdNoValidoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DataServiceImpl implements DataService {

    private final MediumDAO mediumDAO;
    private final EspirituDAO espirituDAO;
    private final UbicacionRepository ubicacionRepository;

    public DataServiceImpl (EspirituDAO espirituDAO, MediumDAO mediumDao, UbicacionRepository ubicacionRepository) {
        this.espirituDAO = espirituDAO;
        this.mediumDAO = mediumDao;
        this.ubicacionRepository = ubicacionRepository;
    }
    public void eliminarTodo(){
            espirituDAO.deleteAll();
            mediumDAO.deleteAll();
            ubicacionRepository.eliminarTodos();
    }

    public void revisarId(Long id) {
        if (id == null || id <= 0) {
            throw new IdNoValidoException();
        }
    }
}

package ar.edu.unq.epersgeist.service.dataService.impl;

import ar.edu.unq.epersgeist.persistencia.dao.EspirituDAO;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.persistencia.dao.UbicacionDAO;
import ar.edu.unq.epersgeist.persistencia.repositorys.interfaces.UbicacionRepository;
import ar.edu.unq.epersgeist.service.dataService.DataService;
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
}

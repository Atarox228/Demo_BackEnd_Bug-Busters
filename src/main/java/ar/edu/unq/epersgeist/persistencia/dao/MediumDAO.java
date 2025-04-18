package ar.edu.unq.epersgeist.persistencia.dao;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Collection;


public interface MediumDAO extends JpaRepository<Medium, Long> {


}

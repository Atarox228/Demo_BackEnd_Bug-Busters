package ar.edu.unq.epersgeist.persistencia.dao.impl;

import ar.edu.unq.epersgeist.modelo.Espiritu;
import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.Collection;
import java.util.List;

public class HibernateMediumDAO extends HibernateDAO<Medium> implements MediumDAO {

    public HibernateMediumDAO() {
        super(Medium.class);
    }

    public Collection<Medium> recuperarTodos() {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "select m from Medium m order by m.nombre asc";
        Query<Medium> query = session.createQuery(hql, Medium.class);
        return query.getResultList();
    }

    @Override
    public List<Espiritu> obtenerEspiritus(Long idMedium) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "SELECT e FROM Espiritu e WHERE e.medium.id = :mediumId";
        TypedQuery<Espiritu> query = session.createQuery(hql, Espiritu.class);
        query.setParameter("mediumId", idMedium);
        return query.getResultList();
    }


    public List<Medium> mediumsSinEspiritusEn(Long ubicacionId){
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "select m from Medium m where m.id NOT IN (SELECT e.medium.id FROM Espiritu e) and m.ubicacion.id = :ubicacion";
        Query<Medium> query = session.createQuery(hql, Medium.class);
        query.setParameter("ubicacion", ubicacionId);
        return query.getResultList();
    }
}

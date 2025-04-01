package ar.edu.unq.epersgeist.persistencia.dao.impl;

import ar.edu.unq.epersgeist.modelo.Medium;
import ar.edu.unq.epersgeist.persistencia.dao.MediumDAO;
import ar.edu.unq.epersgeist.servicios.runner.HibernateTransactionRunner;
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
}

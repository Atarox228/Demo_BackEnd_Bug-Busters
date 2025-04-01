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

    public void actualizar(Medium medium) {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "UPDATE Medium m SET m.nombre = :medNombre, m.manaMax = :medManaMax, m.mana = :medMana WHERE m.id = :medId";
        Query<Medium> query = session.createQuery(hql);
        query.setParameter("medNombre", medium.getNombre());
        query.setParameter("medManaMax", medium.getManaMax());
        query.setParameter("medMana", medium.getMana());
        query.setParameter("medId", medium.getId());
        query.executeUpdate();
    }


    public Collection<Medium> recuperarTodos() {
        Session session = HibernateTransactionRunner.getCurrentSession();
        String hql = "select m from Medium m order by m.nombre asc";
        Query<Medium> query = session.createQuery(hql, Medium.class);
        return query.getResultList();
    }
}

package org.example.repository;

import org.example.config.HibernateUtil;
import org.example.domain.entity.City;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class CityRepository {
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public List<City> getItems(int offset, int limit) {
        Query<City> query = sessionFactory.getCurrentSession().createQuery("select c from City c", City.class)
                .setFirstResult(offset)
                .setMaxResults(limit);
        return query.list();
    }

    public int getTotalCount() {
        Query<Long> query = sessionFactory.getCurrentSession().createQuery("select count(c) from City c", Long.class);
        return Math.toIntExact(query.getSingleResult());
    }

    public City getById(Integer id) {
        Session session = sessionFactory.getCurrentSession();
        session.beginTransaction();
        City result = session.createQuery("select c from City c join fetch c.country where c.id = :id", City.class)
                .setParameter("id", id)
                .getSingleResult();
        session.getTransaction().commit();
        return result;
    }
}

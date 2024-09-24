package org.example.repository;

import org.example.config.HibernateUtil;
import org.example.domain.entity.City;
import org.example.domain.entity.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class CityRepository {
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public List<City> getItems(int offset, int limit) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            sessionFactory.getCurrentSession().createQuery("select c from Country c join fetch c.languages", Country.class).list();
            List<City> cities = sessionFactory.getCurrentSession().createQuery("select c from City c", City.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
            session.getTransaction().commit();
            return cities;
        }
    }

    public List<City> getAll() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            List<City> cities = sessionFactory.getCurrentSession().createQuery("select c from City c", City.class)
                    .list();
            session.getTransaction().commit();
            return cities;
        }
    }

    public int getTotalCount() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Long result = sessionFactory.getCurrentSession().createQuery("select count(c) from City c", Long.class).getSingleResult();
            session.getTransaction().commit();
            return Math.toIntExact(result);
        }
    }

    public Optional<City> getById(Integer id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            City result = sessionFactory.getCurrentSession().createQuery("select c from City c join fetch c.country where c.id = :id", City.class)
                    .setParameter("id", id)
                    .getSingleResult();
            session.getTransaction().commit();
            return Optional.ofNullable(result);
        }
    }

}

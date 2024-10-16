package org.example.repository;

import org.example.config.HibernateUtil;
import org.example.domain.entity.City;
import org.example.domain.entity.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class CityRepository implements CrudRepository<City> {
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public List<City> getItems(int offset, int limit) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            List<Country> countries = session.createQuery("select c from Country c join fetch c.languages", Country.class).list();
            List<City> cities = session.createQuery("select c from City c", City.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
            session.getTransaction().commit();
            return cities;
        }
    }

    @Override
    public List<City> getAll() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            List<Country> countries = session.createQuery("select c from Country c join fetch c.languages", Country.class).list();
            List<City> cities = session.createQuery("select c from City c", City.class)
                    .list();
            session.getTransaction().commit();
            return cities;
        }
    }

    @Override
    public int getTotalCount() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Long result = session.createQuery("select count(c) from City c", Long.class).getSingleResult();
            session.getTransaction().commit();
            return Math.toIntExact(result);
        }
    }

    @Override
    public Optional<City> getById(Integer id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            City city = session.find(City.class, id);
            session.getTransaction().commit();
            return Optional.ofNullable(city);
        }
    }

    @Override
    public City save(City city) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(city);
            session.getTransaction().commit();
            return city;
        }
    }

    @Override
    public City update(City city) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.merge(city);
            session.getTransaction().commit();
        }
        return city;
    }

    @Override
    public void delete(City city) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.remove(city);
            session.getTransaction().commit();
        }
    }
}

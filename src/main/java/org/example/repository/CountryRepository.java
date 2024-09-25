package org.example.repository;

import org.example.config.HibernateUtil;
import org.example.domain.entity.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;
import java.util.Optional;

public class CountryRepository implements CrudRepository<Country> {
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public List<Country> getItems(int offset, int limit) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            List<Country> countries = sessionFactory.getCurrentSession().createQuery("select c from Country c join fetch c.languages", Country.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .list();
            session.getTransaction().commit();
            return countries;
        }
    }

    public List<Country> getAll() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            List<Country> list = sessionFactory.getCurrentSession().createQuery("select c from Country c join fetch c.languages", Country.class).list();
            session.getTransaction().commit();
            return list;
        }
    }

    @Override
    public int getTotalCount() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Long result = sessionFactory.getCurrentSession().createQuery("select count(c) from Country c", Long.class).getSingleResult();
            session.getTransaction().commit();
            return Math.toIntExact(result);
        }
    }

    @Override
    public Optional<Country> getById(Integer id) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            Country result = sessionFactory.getCurrentSession().createQuery("select c from Country c where c.id = :id", Country.class)
                    .setParameter("id", id)
                    .getSingleResult();
            session.getTransaction().commit();
            return Optional.ofNullable(result);
        }
    }

    @Override
    public Country save(Country country) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.persist(country);
            session.getTransaction().commit();
            return country;
        }
    }

    @Override
    public Country update(Country country) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.merge(country);
            session.getTransaction().commit();
        }
        return country;
    }

    @Override
    public void delete(Country country) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            session.remove(country);
            session.getTransaction().commit();
        }
    }
}

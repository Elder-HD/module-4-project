package org.example.repository;

import org.example.config.HibernateUtil;
import org.example.domain.entity.Country;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class CountryRepository {
    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public List<Country> getAll() {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            List<Country> list = sessionFactory.getCurrentSession().createQuery("select c from Country c join fetch c.languages", Country.class).list();
            session.getTransaction().commit();
            return list;
        }
    }
}

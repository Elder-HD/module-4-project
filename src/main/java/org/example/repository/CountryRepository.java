package org.example.repository;

import org.example.config.HibernateUtil;
import org.example.domain.entity.Country;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class CountryRepository {
        private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    public List<Country> getAll() {
        Query<Country> query = sessionFactory.getCurrentSession().createQuery("select c from Country c join fetch c.languages", Country.class);
        return query.list();
    }
}

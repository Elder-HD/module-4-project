package org.example.config;

import lombok.Getter;
import org.example.domain.entity.City;
import org.example.domain.entity.Country;
import org.example.domain.entity.CountryLanguage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;


public class HibernateUtil {
    @Getter
    private static final SessionFactory sessionFactory;

    static {
        Configuration configuration = new Configuration()
                .setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect")
                .setProperty(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver")
                .setProperty(Environment.URL, "jdbc:p6spy:mysql://localhost:3305/world")
                .setProperty(Environment.USER, "root")
                .setProperty(Environment.PASS, "project")
                .setProperty(Environment.HBM2DDL_AUTO, "validate")
                .setProperty(Environment.SHOW_SQL, "true")
                .setProperty(Environment.HIGHLIGHT_SQL, "true")
                .setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread")
                .setProperty(Environment.STATEMENT_BATCH_SIZE, "100")
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class);

        sessionFactory = configuration.buildSessionFactory();

    }
}

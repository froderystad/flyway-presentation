package no.jpro.flywaydemo;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.internal.util.jdbc.DriverDataSource;
import org.junit.After;
import org.junit.Before;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class AbstractJpaTest {
    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @Before
    public void startTransaction() {
        entityManager().getTransaction().begin();
    }

    protected void clearCache() {
        entityManager.flush();
        entityManager.clear();
    }

    @After
    public void rollbackTransaction() {
        entityManager().getTransaction().rollback();
        entityManager().close();
        entityManager = null;
    }

    protected EntityManager entityManager() {
        if (entityManager == null) {
            entityManager = entityManagerFactory().createEntityManager();
        }
        return entityManager;
    }

    private EntityManagerFactory entityManagerFactory() {
        if (entityManagerFactory == null) {
            initFlyway();
            entityManagerFactory = Persistence.createEntityManagerFactory("no.jpro.flywaydemo.inmemory");
        }
        return entityManagerFactory;
    }

    private void initFlyway() {
        Flyway flyway = new Flyway();
        flyway.setDataSource(createDataSource());
        flyway.clean();
        flyway.migrate();
    }

    private DriverDataSource createDataSource() {
        return new DriverDataSource(
                getClass().getClassLoader(),
                "org.h2.Driver",
                "jdbc:h2:~/flywaydemodb-inmemory",
                "jpro",
                "jpro"
        );
    }
}

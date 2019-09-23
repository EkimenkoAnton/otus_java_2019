package ru.otus.services.dbservicemanager;//import domain.Account;
import ru.otus.domain.*;
import ru.otus.domain.Address;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.otus.services.dbservice.DBService;
import ru.otus.services.dbservice.HibernateDBService;
import ru.otus.services.executor.Executor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

@Component
public class HibernateDBServiceManager implements DBServiceManager {

    private static final String HIBERNATE_CONFIG = "hibernate.cfg.xml";

    private final SessionFactory sessionFactory;

    private DBService dbService = null;

    public HibernateDBServiceManager() {
        Configuration configuration = new Configuration()
                .configure(HIBERNATE_CONFIG);
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        Metadata metadata = new MetadataSources(serviceRegistry)
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Phone.class)
                .addAnnotatedClass(User.class)

                .getMetadataBuilder()
                .build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();
    }

    @Bean
    public DBService getDBService() {
        if (null == dbService) {
            Executor executor = new Executor(sessionFactory);
            HibernateDBService hibernateDBService = new HibernateDBService(executor);
            this.dbService = hibernateDBService;
        }
        return dbService;
    }
}
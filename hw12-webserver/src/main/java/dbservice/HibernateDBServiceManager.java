package dbservice;

import datasets.Account;
import datasets.Address;
import datasets.Phone;
import datasets.User;
import executor.Executor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateDBServiceManager implements DBServiceManager {

    private static final String HIBERNATE_CONFIG = "hibernate.cfg.xml";
    private final SessionFactory sessionFactory;
    private HibernateDBService hibernateDBService;


    public HibernateDBServiceManager() {
        Configuration configuration = new Configuration()
                .configure(HIBERNATE_CONFIG);
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        Metadata metadata = new MetadataSources(serviceRegistry)
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Phone.class)
                .getMetadataBuilder()
                .build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();
    }

    public DBService getHibernateDBService() {
        if (null == hibernateDBService) {
            this.hibernateDBService = new HibernateDBService(
                    new Executor(sessionFactory)
            );
        }
        return this.hibernateDBService;
    }
}
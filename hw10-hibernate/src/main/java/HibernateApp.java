import datasets.Account;
import datasets.Address;
import datasets.Phone;
import datasets.User;
import dbservice.HibernateDBService;
import executor.Executor;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateApp {

    private static final String HIBERNATE_CONFIG = "hibernate.cfg.xml";

    private final SessionFactory sessionFactory;

    public HibernateApp() {
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

    public static void main(String [] args) {
        new HibernateApp().start();
    }

    public void start() {
        Executor executor = new Executor(sessionFactory);
        HibernateDBService hibernateDBService = new HibernateDBService(executor);
    }
}

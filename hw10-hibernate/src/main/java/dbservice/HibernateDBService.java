package dbservice;

import datasets.Account;
import datasets.Address;
import datasets.Phone;
import datasets.User;
import executor.Executor;
import lombok.SneakyThrows;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;

import java.util.Set;
import java.util.stream.Collectors;

public class HibernateDBService implements DBService {

    private static final String HIBERNATE_CONFIG = "hibernate.cfg.xml";
    private final Executor executor;
    private final Set<Class> supportedClasses;

    public HibernateDBService() {

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
        supportedClasses = metadata.getEntityBindings().stream().map(PersistentClass::getMappedClass).collect(Collectors.toSet());
        SessionFactory factory = metadata.getSessionFactoryBuilder().build();
        this.executor = new Executor(factory);

    }

    @Override
    public void create(Object objectData) {
        isSupported(objectData);
        executor.execUpdate(session -> session.save(objectData));
    }

    @Override
    public void update(Object objectData) {
        isSupported(objectData);
        executor.execUpdate(session -> session.update(objectData));
    }

    @Override
    public void createOrUpdate(Object objectData) {
        isSupported(objectData);
        executor.execUpdate(session -> session.saveOrUpdate(objectData));
    }

    @Override
    public <I> I load(long id, Class<I> clazz) {
        isSupported(clazz);
        return executor.execQuery(session -> session.get(clazz, id));
    }

    @SneakyThrows
    private boolean isSupported(Object obj) {
        if (null == obj)
            throw new Exception("Unsupported class");
        return isSupported(obj.getClass());
    }

    @SneakyThrows
    private boolean isSupported(Class<?> clazz) {
        if(!supportedClasses.contains(clazz))
            throw new Exception("Unsupported class");
        return true;
    }
}

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HibernateDBServiceTest {

    private static final String HIBERNATE_CONFIG = "hibernate.cfg.xml";
    private final SessionFactory sessionFactory;
    private DBService dbService;
    private User user;

    HibernateDBServiceTest() {
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

    @BeforeEach
    void setUp() {

        Executor executor = new Executor(sessionFactory);
        dbService = new HibernateDBService(executor);

        user = new User();
        user.setAge(99);
        user.setName("NoName");

        for (int i = 0; i < 5; i++) {
            Phone phone = new Phone();
            phone.setNumber("91"+i);
            phone.setUser(user);
            user.addPhone(phone);
        }

        Address address = new Address();
        address.setStreet("Interstate60");
        address.setUser(user);

        user.setAddress(address);

    }

    @Test
    void create() {
        dbService.create(user);
        User realUser = dbService.load(user.getId(), user.getClass());

        compareUsers(user,realUser);
    }

    @Test
    void update() {
        dbService.create(user);

        User created = dbService.load(user.getId(), user.getClass());

        compareUsers(user,created);

        user.setAge(66);
        user.setName("AnotherName");

        user.getPhones().clear();
        for (int i = 0; i < 5; i++) {
            Phone phone = new Phone();
            phone.setNumber("61"+i);
            phone.setUser(user);
            user.addPhone(phone);
        }

        Address address = new Address();
        address.setStreet("AnotherStreet");
        address.setUser(user);

        dbService.update(user);
        User updated = dbService.load(user.getId(), user.getClass());

        compareUsers(user,updated);
    }

    @Test
    void createOrUpdate() {

        dbService.createOrUpdate(user);
        user = new User();
        user.setAge(66);
        user.setName("AnotherName");

        user.getPhones().clear();
        for (int i = 0; i < 5; i++) {
            Phone phone = new Phone();
            phone.setNumber("61"+i);
            phone.setUser(user);
            user.addPhone(phone);
        }

        Address address = new Address();
        address.setStreet("AnotherStreet");
        address.setUser(user);


        dbService.createOrUpdate(user);
        User realUser = dbService.load(user.getId(), user.getClass());

        compareUsers(user,realUser);
    }

    void compareUsers(User expectedUser, User realUser){
        Assertions.assertEquals(expectedUser,realUser);
        Assertions.assertEquals(expectedUser.getAddress(),realUser.getAddress());

        List<Phone> expectedPhones = expectedUser.getPhones();
        List<Phone> realPhones = realUser.getPhones();

        Assertions.assertEquals(expectedPhones.size(),expectedPhones.size());

        int sameElementsCount = 0;
        for (Phone expectedPhone : expectedPhones) {
            for (Phone realPhone : realPhones) {
                if(expectedPhone.equals(realPhone))
                    ++sameElementsCount;
            }
        }
        assertEquals(expectedPhones.size(),sameElementsCount);
    }
}
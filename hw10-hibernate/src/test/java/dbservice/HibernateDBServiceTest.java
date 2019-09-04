package dbservice;

import datasets.Address;
import datasets.Phone;
import datasets.User;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ReflectionHelper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HibernateDBServiceTest {

    DBService dbService;
    User user;

    @BeforeEach
    void setUp() {
        dbService = new HibernateDBService();

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
        create();

        //user = new User();
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
        User realUser = dbService.load(user.getId(), user.getClass());

        compareUsers(user,realUser);
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

    public void compareUsers(User expectedUser, User realUser){
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
        Assertions.assertEquals(expectedPhones.size(),sameElementsCount);
    }
}
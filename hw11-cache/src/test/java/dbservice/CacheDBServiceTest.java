package dbservice;

import cache.CacheEngine;
import datasets.Address;
import datasets.Phone;
import datasets.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.ReflectionHelper;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CacheDBServiceTest {

    private DBService dbService;
    private User user;
    private Map<Class, CacheEngine<Long, Object>> cacheMap;

    CacheDBServiceTest() {
    }

    @BeforeEach
    void setUp() {
        DBServiceManager dbServiceManager = new CacheDBServiceManager();
        dbService = dbServiceManager.getDBService();

        cacheMap = (Map<Class, CacheEngine<Long, Object>>) ReflectionHelper.getFieldValue(dbService,"cacheMap");

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
    void update() throws InterruptedException {
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

        Assertions.assertEquals(cacheMap.size(),1);
        CacheEngine<Long, Object> cacheEngine = cacheMap.get(User.class);
        Assertions.assertNotNull(cacheEngine);
        Assertions.assertEquals(cacheEngine.getHitCount(),2);
        Assertions.assertEquals(cacheEngine.getMissCount(),0);

        long sleepCascheOverTime = (long) ReflectionHelper.getFieldValue(cacheEngine,"lifeTimeMs");
        Thread.sleep(sleepCascheOverTime+50);

        dbService.load(user.getId(), user.getClass());

        Assertions.assertEquals(cacheEngine.getHitCount(),2);
        Assertions.assertEquals(cacheEngine.getMissCount(),1);

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
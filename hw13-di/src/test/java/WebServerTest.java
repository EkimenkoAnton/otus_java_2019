import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.domain.Account;
import ru.otus.domain.Address;
import ru.otus.domain.Phone;
import ru.otus.domain.User;
import ru.otus.services.dbservice.DBService;
import ru.otus.services.dbservice.HibernateDBService;
import ru.otus.services.executor.Executor;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

class WebServerTest {

    private static final Boolean BUILD_MODE = true;
    private static final String APP_URL = "http://localhost:8080/hw13di/";
    private static final String HIBERNATE_CONFIG = "hibernate.cfg.xml";

    @AllArgsConstructor
    static class ParamEntity {
        @Getter String key;
        @Getter String value;
    }

    @BeforeEach
    void setUp() throws Exception {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration()
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
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
        Executor executor = new Executor(sessionFactory);
        DBService dbService = new HibernateDBService(executor);
    }

    @Test
    void addUsers(){
        if (BUILD_MODE)
            return;
        String expectedResult = "[{\"id\":1,\"name\":\"Ivan\",\"age\":99,\"address\":{\"id\":1,\"street\":\"NoName Street\"},\"phones\":[{\"id\":1,\"number\":\"911\"},{\"id\":2,\"number\":\"912\"}]}]";
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>[]>(){}.getType();

        Map<String, Object>[] expectedMap = gson.fromJson(expectedResult, type);

        List<ParamEntity> parameters = new ArrayList<>();
        parameters.add(new ParamEntity("username","Ivan"));
        parameters.add(new ParamEntity("age","99"));
        parameters.add(new ParamEntity("address","NoName Street"));
        parameters.add(new ParamEntity("phone[]","911"));
        parameters.add(new ParamEntity("phone[]","912"));

        executePost(APP_URL+"addUser", parameters);

        String realResult = executeGet(APP_URL+"getUsers", null);
        System.out.println("------>  RESULT : "+realResult);
        Map<String, Object>[] realMap = gson.fromJson(realResult, type);


        Assertions.assertNotNull(realResult);
        Assertions.assertEquals(expectedMap.length,realMap.length);
        Assertions.assertEquals(expectedMap[0],realMap[0]);

    }

    String executePost(String targetURL, List<ParamEntity> urlParameters) {
        return  executeRquest(targetURL, urlParameters, "POST");
    }

    String executeGet(String targetURL, List<ParamEntity> urlParameters) {
        return  executeRquest(targetURL, urlParameters, "GET");
    }

    String executeRquest(String targetURL, List<ParamEntity> urlParameters, String requestType) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestType);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setUseCaches(false);

            connection.setDoOutput(true);

            if (null != urlParameters && !urlParameters.isEmpty()) {
                StringJoiner sj = new StringJoiner("&");
                for (ParamEntity urlParameter : urlParameters) {
                    sj.add(URLEncoder.encode(urlParameter.getKey(), StandardCharsets.UTF_8) + "="
                            + URLEncoder.encode(urlParameter.getValue(), StandardCharsets.UTF_8));
                }
                byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);

                System.out.println("--------------->sending "+sj.toString());
                int length = out.length;

                connection.setFixedLengthStreamingMode(length);
                connection.connect();

                try(OutputStream wr = connection.getOutputStream()) {
                    wr.write(out);
                }
            }

            StringBuilder response = new StringBuilder();
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                }
            }
            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }



}
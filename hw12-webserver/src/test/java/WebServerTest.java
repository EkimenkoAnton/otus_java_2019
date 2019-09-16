import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

class WebServerTest {

    private Server server;

    @AllArgsConstructor
    static class ParamEntity {
        @Getter String key;
        @Getter String value;
    }

    @BeforeEach
    void setUp() throws Exception {
        WebServer webServer = new WebServer();
        this.server = (Server) ReflectionHelper.callMethod(webServer,"createServer",8080);
        this.server.start();
    }

    @Test
    void addUsers(){

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

        executePost("http://localhost:8080/addUser", parameters);

        String realResult = executePost("http://localhost:8080/getUsers", null);
        System.out.println("------>  RESULT : "+realResult);
        Map<String, Object>[] realMap = gson.fromJson(realResult, type);

        Assertions.assertNotNull(realResult);
        Assertions.assertEquals(expectedMap.length,realMap.length);
        Assertions.assertEquals(expectedMap[0],realMap[0]);

    }

    String executePost(String targetURL, List<ParamEntity> urlParameters) {
        HttpURLConnection connection = null;

        try {
            URL url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setUseCaches(false);
            setAuthorization(connection);

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

    private void setAuthorization(HttpURLConnection connection){
        String encoding = Base64.getEncoder().encodeToString(("ivan:pass").getBytes(Charset.forName("UTF-8")));
        connection.setRequestProperty  ("Authorization", "Basic " + encoding);
    }

    @AfterEach
    void tearDown() {
        try {
            if(null!=server && server.isRunning())
                server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
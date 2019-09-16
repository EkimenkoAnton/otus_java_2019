package servlets;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import datasets.User;
import dbservice.DBService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class GetUsers extends HttpServlet {
    private static final Gson gson = new Gson();
    private final DBService service;
    private final ExclusionStrategy exclusionStrategy;

    public GetUsers(DBService service) {
        this.service = service;
        exclusionStrategy = new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getName().equalsIgnoreCase("user");
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        };
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter printWriter = response.getWriter();
        printWriter.print(getUsers());
        printWriter.flush();
    }

    private String getUsers(){
        return gson.newBuilder().addSerializationExclusionStrategy(exclusionStrategy).create()
                .toJson(service.loadEntities(User.class));
    }
}

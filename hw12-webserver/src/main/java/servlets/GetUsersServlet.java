package servlets;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import datasets.User;
import dbservice.DBService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class GetUsersServlet extends HttpServlet {

    private static final Gson gson = new Gson();
    private final DBService service;
    private final ExclusionStrategy exclusionStrategy;

    public GetUsersServlet(DBService service) {
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.setStatus(HttpServletResponse.SC_OK);
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(getUsers());
        printWriter.flush();
    }

    private String getUsers(){
        return gson.newBuilder().addSerializationExclusionStrategy(exclusionStrategy).create()
                .toJson(service.loadEntities(User.class));
    }
}

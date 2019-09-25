package ru.otus.controllers;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.domain.Address;
import ru.otus.domain.Phone;
import ru.otus.domain.User;
import ru.otus.services.ServletService;
import ru.otus.services.dbservice.DBService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;
import java.util.Map;

@RestController
public class UserController extends HttpServlet {

    private final DBService service;
    private final ExclusionStrategy exclusionStrategy;
    private final ServletService servletService;

    private static final String USER_NAME_PARAM = "username";
    private static final String USER_AGE_PARAM = "age";
    private static final String USER_ADDRESS_PARAM = "address";
    private static final String USER_PHONES_PARAM = "phone[]";

    private static final Gson gson = new Gson();

    public UserController(DBService service, ServletService servletService) {
        this.service = service;
        this.servletService = servletService;
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

    @GetMapping("/getUsers")
    private void getUsers(HttpServletResponse resp) throws IOException {
        servletService.setJsonType(resp);
        servletService.setOK(resp);
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(getUsersFromRepository());
        printWriter.flush();
    }

    //NOT USES, causes cyclic dependency from Phone to User
    @GetMapping("/getUsers2")
    private List<User> getUsers2() throws IOException {
        return service.loadEntities(User.class);
    }

    @PostMapping("/addUser")
    protected void addUser(HttpServletRequest req, HttpServletResponse resp) {

        Map<String, String[]> parameterMap = getParametersMap(req);
        if (null == parameterMap) {
            servletService.setNoContent(resp);
            return;
        }

        String reqUserName = getParam(parameterMap,USER_NAME_PARAM);
        String reqAge = getParam(parameterMap,USER_AGE_PARAM);
        String reqAddress = getParam(parameterMap, USER_ADDRESS_PARAM);
        String[] reqPhones = getParams(parameterMap, USER_PHONES_PARAM);

        User user = createUser(reqUserName, reqAge, reqAddress, reqPhones);
        if(null == user ) {
            servletService.setBadRequest(resp);
            return;
        }

        service.create(user);
        servletService.setOK(resp);
    }

    private String getUsersFromRepository(){
        return gson.newBuilder().addSerializationExclusionStrategy(exclusionStrategy).create()
                .toJson(service.loadEntities(User.class));
    }

    private User createUser(String reqUserName, String reqAge, String reqAddress, String[] reqPhones) {
        if (null == reqUserName || null == reqAge || null == reqAddress || null == reqPhones)
            return null;

        User user = new User();
        user.setName(reqUserName);
        user.setAge(Integer.parseInt(reqAge));

        Address address = new Address();
        address.setStreet(reqAddress);
        address.setUser(user);

        user.setAddress(address);

        for (String reqPhone : reqPhones) {
            Phone phone = new Phone();
            phone.setNumber(reqPhone);
            phone.setUser(user);
            user.addPhone(phone);
        }

        return user;
    }
    
    private Map<String, String[]> getParametersMap(HttpServletRequest req) {
        Map<String, String[]> parameterMap = req.getParameterMap();
        return null == parameterMap || parameterMap.size() < 4 ? null : parameterMap;
    }

    private String getParam(Map<String, String[]> parameterMap, String paramName) {
        String[] params = getParams(parameterMap, paramName);
        return null!=params ? params[0] : null;
    }

    private String[] getParams(Map<String, String[]> parameterMap, String paramName) {
        String[] params = parameterMap.get(paramName);
        return null!= paramName
                && null != params
                && params.length > 0
                && null != params[0]
                && !params[0].isEmpty()
                ? params : null;
    }

}

package servlets;

import datasets.Address;
import datasets.Phone;
import datasets.User;
import dbservice.DBService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class AddUserServlet extends HttpServlet {

    private final DBService service;

    public AddUserServlet(DBService service) {
        this.service = service;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Map<String, String[]> parameterMap = getParameters(req);
        if (null == parameterMap) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        String reqUserName = getParam(parameterMap,"username");
        String reqAge = getParam(parameterMap,"age");
        String reqAddress = getParam(parameterMap, "address");
        String[] reqPhones = getParams(parameterMap, "phone[]");

        User user = createUser(reqUserName, reqAge, reqAddress, reqPhones);
        if(null == user ) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        service.create(user);

        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.setStatus(HttpServletResponse.SC_OK);
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
    
    private Map<String, String[]> getParameters(HttpServletRequest req) {
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

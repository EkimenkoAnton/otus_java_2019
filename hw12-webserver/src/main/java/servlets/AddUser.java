package servlets;

import datasets.Address;
import datasets.Phone;
import datasets.User;
import dbservice.DBService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

public class AddUser extends HttpServlet {

    private final DBService service;

    public AddUser(DBService service) {
        this.service = service;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        System.out.println("----------> INSIDE REQ : "+parameterMap);
        if (parameterMap.size()<4) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        System.out.println("----REQUEST : "+request.getParameterMap());

        String reqUserName = parameterMap.get("username")[0];
        String reqAge = parameterMap.get("age")[0];
        String reqAddress = parameterMap.get("address")[0];
        String[] reqPhones = parameterMap.get("phone[]");

        if(null == reqUserName || reqUserName.isEmpty() ||
                null == reqAge || reqAge.isEmpty() ||
                null == reqAddress || reqAddress.isEmpty() ||
                null == reqPhones || reqPhones.length == 0 ||
                (reqPhones.length == 1 && (null == reqPhones[0] || reqPhones[0].isEmpty()))
        ) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

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

        service.create(user);

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

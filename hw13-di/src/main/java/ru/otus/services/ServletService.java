package ru.otus.services;

import org.springframework.stereotype.Service;
import ru.otus.domain.User;

import javax.servlet.http.HttpServletResponse;

@Service
public class ServletService {

    public void setJsonType(HttpServletResponse resp){
        resp.setContentType("application/json");
    }

    public void setTextType(HttpServletResponse resp){
        resp.setContentType("text/html;charset=utf-8");
    }

    public void setNoContent(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    public void setBadRequest(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    public void setOK(HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}

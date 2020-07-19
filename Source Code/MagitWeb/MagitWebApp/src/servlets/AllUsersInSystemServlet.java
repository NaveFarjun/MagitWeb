package servlets;


import com.google.gson.Gson;
import users.UserInSystem;
import users.UserManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AllUsersInSystemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        Gson gson = new Gson();
        Map<String, UserInSystem> users = ServletUtils.getUserManaqer(getServletContext()).getUsers();
        List<String> usersNamesList = new ArrayList<>(users.keySet());
        String json = gson.toJson(usersNamesList);
        try(PrintWriter out=resp.getWriter()) {
            out.print(json);
        }
    }
}

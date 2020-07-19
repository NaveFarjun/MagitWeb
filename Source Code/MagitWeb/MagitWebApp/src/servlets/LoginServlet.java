package servlets;


import Lib.User;
import constants.Constants;
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

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean isError=false;
        response.setContentType("text/html;charset=UTF-8");
        String userName = SessionUtils.getUserName(request);
        if (userName == null) {
            String userNameFromRequest = request.getParameter("userName");
            if (userNameFromRequest.isEmpty()) {
                response.sendError(402, "empty user name");
                isError =true;
                return;
            }
            UserManager userManager = ServletUtils.getUserManaqer(getServletContext());
            if (!isError && userManager.isUserExists(userNameFromRequest)) {
                UserInSystem userInSystem=ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userNameFromRequest);
                if(userInSystem.isLogedIn()) {
                    response.sendError(403, "user already logged in");
                    isError = true;
                    return;
                }
                else{
                    synchronized (this) {
                        request.getSession(true).setAttribute(Constants.USERNAME, userNameFromRequest);
                        return;
                    }
                }
            }
            User user = new User(userNameFromRequest);
            UserInSystem userInSystem = new UserInSystem(user);
            if(!isError) {
                synchronized (this) {
                    userManager.addUser(userInSystem);
                    request.getSession(true).setAttribute(Constants.USERNAME, user.getName());
                    userInSystem=ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userNameFromRequest);
                    userInSystem.setLogedIn(true);
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}

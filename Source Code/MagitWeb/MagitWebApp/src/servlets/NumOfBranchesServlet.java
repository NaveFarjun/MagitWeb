package servlets;

import Lib.Repository;
import com.google.gson.Gson;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class NumOfBranchesServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson=new Gson();
        String userName = req.getParameter("userName");
        if (userName == null) {
            userName = SessionUtils.getUserName(req);
        }
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        Repository repository=user.getRepositories().get(req.getParameter("repName"));
        int numOfBranches=repository.getBranchesMap().keySet().size();
        try(PrintWriter out=resp.getWriter()){
            String json=gson.toJson(numOfBranches);
            out.println(json);
        }
    }
}

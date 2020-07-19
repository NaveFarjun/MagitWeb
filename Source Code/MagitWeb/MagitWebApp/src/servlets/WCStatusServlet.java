package servlets;

import Lib.RepositoryManager;
import MagitExceptions.CommitException;
import MagitExceptions.RepositoryDoesnotExistException;
import com.google.gson.Gson;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;
@WebServlet("/ShowStatus")
public class WCStatusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName= SessionUtils.getUserName(req);
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager = user.getRepositoryManager();
        try {
            List<List<String>> changes=repositoryManager.ShowStatus();
            Gson gson=new Gson();
            String json=gson.toJson(changes);
            try(PrintWriter out=resp.getWriter()){
                out.print(json);
            }
        } catch (ParseException | RepositoryDoesnotExistException | CommitException e) {
            resp.sendError(403,e.getMessage());
        }
    }
}

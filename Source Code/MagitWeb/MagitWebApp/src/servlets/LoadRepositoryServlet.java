package servlets;

import Lib.RepositoryManager;
import MagitExceptions.RepositoryDoesnotExistException;
import MagitExceptions.RepositorySameToCurrentRepositoryException;
import constants.Constants;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

public class LoadRepositoryServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String repoName=req.getParameter("repName");
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        RepositoryManager repositoryManager = user.getRepositoryManager();
        try {
            if(!repositoryManager.GetCurrentRepositoryName().equals(repoName)) {
                repositoryManager.ChangeRepository(user.getRepositories().get(repoName));
            }
        } catch (RepositoryDoesnotExistException  | ParseException e) {
           resp.sendError(403,"didn't manage to load repo");
        }
    }
}

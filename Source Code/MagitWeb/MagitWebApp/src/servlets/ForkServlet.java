package servlets;

import Lib.RepositoryManager;
import MagitExceptions.RepositoryDoesnotExistException;
import MagitExceptions.RepositorySameToCurrentRepositoryException;
import constants.Constants;
import notification.Notification;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

public class ForkServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userNameToForkFrom = req.getParameter(Constants.USER_NAME_TO_FORK);
        String forkedRepoName = req.getParameter(Constants.REPOSITORY_NAME);
        String userName = SessionUtils.getUserName(req);
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager = user.getRepositoryManager();
        String newRepoName = userNameToForkFrom + "-" + forkedRepoName;
        String localRepoLocation = Constants.ALL_USERS_FOLDER + userName + "\\" + newRepoName;
        String rempoteRepoLocation = Constants.ALL_USERS_FOLDER + userNameToForkFrom + "\\" + forkedRepoName;
        try {
            repositoryManager.CloneRepository(localRepoLocation, rempoteRepoLocation, newRepoName);
            user.addRepository(repositoryManager.GetCurrentRepository());
            UserInSystem userToForkFrom=ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userNameToForkFrom);
            Notification notification=new Notification(user.getUser().getName(),user.getUser().getName()+" forked from you "+forkedRepoName+" repository","forked");
            userToForkFrom.getNotificationsManager().addNewMessage(notification);
        } catch (RepositoryDoesnotExistException | ParseException | RepositorySameToCurrentRepositoryException e) {
            resp.sendError(403, e.getMessage());
        }

    }
}

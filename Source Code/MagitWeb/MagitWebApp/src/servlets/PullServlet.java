package servlets;

import Lib.FileUtils;
import Lib.Repository;
import Lib.RepositoryManager;
import MagitExceptions.*;
import constants.Constants;
import users.UserInSystem;
import users.UserManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

@WebServlet("/pull")
public class PullServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = SessionUtils.getUserName(req);
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager = user.getRepositoryManager();

        try {
            String repoName = repositoryManager.GetCurrentRepositoryName();
            String[] arr = repoName.split("-");
            String forkedUserName = arr[0];
            String originalRepoName = repoName.substring(forkedUserName.length() + 1);
            UserInSystem originalUserOfRepo = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(forkedUserName);
            Repository originalRepo = originalUserOfRepo.getRepositories().get(originalRepoName);
            repositoryManager.GetCurrentRepository().Pull(user.getUser(),originalRepo);
        } catch (RepositoryDoesnotExistException | CommitException | RepositoryDoesntTrackAfterOtherRepositoryException | ParseException | BranchDoesNotExistException | OpenChangesException e) {
            e.printStackTrace();
        }

    }
}

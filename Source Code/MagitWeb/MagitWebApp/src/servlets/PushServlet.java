package servlets;

import Lib.Branch;
import Lib.Repository;
import Lib.RepositoryManager;
import Lib.User;
import MagitExceptions.*;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.rmi.CORBA.Util;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

@WebServlet("/push")
public class PushServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = SessionUtils.getUserName(req);
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager = user.getRepositoryManager();
        try {
            String repoName = repositoryManager.GetCurrentRepositoryName();
            String[] arr=repoName.split("-");
            String forkedUserName=arr[0];
            String originalRepoName=repoName.substring(forkedUserName.length()+1);
            UserInSystem originalUserOfRepo=ServletUtils.getUserManaqer(getServletContext()).getUsers().get(forkedUserName);
            Repository originalRepo=originalUserOfRepo.getRepositories().get(originalRepoName);
            String branchToPushName = req.getParameter("branchName");
            Branch b = repositoryManager.GetCurrentRepository().getBranchesMap().get(branchToPushName);
            synchronized (originalUserOfRepo.getRepositoryLock(originalRepoName)) {
                if(Boolean.parseBoolean(req.getParameter("isNewBranch"))) {
                    repositoryManager.pushLocalBranchToRemoteBranch(b, originalRepo);
                }
                else{
                    repositoryManager.Push(originalRepo);
                }
            }
        } catch (RemoteTrackingBranchException | OpenChangesException | CommitException | HeadBranchDeletedExcption | RepositoryDoesntTrackAfterOtherRepositoryException | BranchFileDoesNotExistInFolderException | BranchDoesNotExistException | ParseException | RepositoryDoesnotExistException | BranchNameIsAllreadyExistException e) {
            resp.sendError(403, e.getMessage());
        }
    }
}

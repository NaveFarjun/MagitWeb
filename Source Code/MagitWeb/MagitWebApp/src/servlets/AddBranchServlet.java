package servlets;

import Lib.RepositoryManager;
import Lib.SHA1;
import MagitExceptions.BranchNameIsAllreadyExistException;
import MagitExceptions.CommitException;
import MagitExceptions.RepositoryDoesnotExistException;
import com.google.gson.Gson;
import constants.Constants;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AddBranchServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String branchName = req.getParameter(Constants.BRANCH_NAME);
        UserInSystem currentUser= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        RepositoryManager repositoryManager = currentUser.getRepositoryManager();
        String sha1String=req.getParameter("sha1");
        SHA1 sha1;
        if(sha1String!=null){
            sha1=new SHA1(sha1String);
        }
        else{
            sha1=repositoryManager.GetCurrentRepository().getActiveBranch().getCommitSH1();
        }
        try {
            synchronized (currentUser.getRepositoryLock(repositoryManager.GetCurrentRepositoryName())) {
                repositoryManager.CreateNewBranch(branchName,sha1);
            }
        } catch (BranchNameIsAllreadyExistException | CommitException | RepositoryDoesnotExistException e) {
           resp.sendError(402,e.getMessage());
        }
        resp.setStatus(200);
    }
}

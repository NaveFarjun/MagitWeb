package servlets;

import Lib.RemoteBranch;
import Lib.Repository;
import Lib.RepositoryManager;
import MagitExceptions.BranchNameIsAllreadyExistException;
import MagitExceptions.CommitException;
import MagitExceptions.RepositoryDoesnotExistException;
import constants.Constants;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet("/createRTB")
public class CreateRTBServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String remoteBranchName = req.getParameter(Constants.BRANCH_NAME);
        RepositoryManager repositoryManager = ServletUtils.getRepositoryManager(getServletContext(),req);
        Repository currentRepository= repositoryManager.GetCurrentRepository();
        RemoteBranch remoteBranch = (RemoteBranch) currentRepository.getBranchesMap().get(remoteBranchName);
        String rtbName=remoteBranchName.split("\\\\")[1];
        try {
            currentRepository.CreateNewRemoteTrackingBranch(rtbName,remoteBranch);
        } catch (RepositoryDoesnotExistException | CommitException | BranchNameIsAllreadyExistException e) {
            e.printStackTrace();
        }
    }
}

package servlets;

import Lib.*;
import MagitExceptions.BranchDoesNotExistException;
import MagitExceptions.BranchFileDoesNotExistInFolderException;
import MagitExceptions.HeadBranchDeletedExcption;
import MagitExceptions.RepositoryDoesnotExistException;
import constants.Constants;
import notification.Notification;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/deleteBranch")
public class DeleteBranchServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String branchNameToDelete = req.getParameter(Constants.BRANCH_NAME);
        RepositoryManager repositoryManager = ServletUtils.getRepositoryManager(getServletContext(), req);
        Repository currentRepository = repositoryManager.GetCurrentRepository();
        Branch branch = currentRepository.getBranchesMap().get(branchNameToDelete);
        String forkedUserName = currentRepository.getName().split("-")[0];
        UserInSystem LRUser = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        UserInSystem originalUserOfRepo = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(forkedUserName);
        try {
            if (branch instanceof RemoteBranch) {
                resp.sendError(402, "trying to delete Remote branch");
            } else if (branch instanceof RemoteTrackingBranch) {
                synchronized (originalUserOfRepo.getRepositoryLock(currentRepository.getName().split("-")[1])) {
                    Repository RR = ServletUtils.getRRRepository(getServletContext(), req);
                    RR.DeleteBranch(branchNameToDelete);
                    currentRepository.DeleteBranch(((RemoteTrackingBranch) branch).getFollowAfter().getName());
                    currentRepository.DeleteBranch(branch.getName());
                /*    currentRepository.getBranchesMap().remove(((RemoteTrackingBranch) branch).getFollowAfter().getName());
                    currentRepository.getBranchesMap().remove(branch.getName());
                    RR.getBranchesMap().remove(branchNameToDelete);*/
                    Notification notification = new Notification(LRUser.getUser().getName(), LRUser.getUser().getName() + " deleted yours  " + branchNameToDelete + " branch in " + RR.getName(), "Deleted branch");
                    originalUserOfRepo.getNotificationsManager().addNewMessage(notification);
                }
            } else {
                currentRepository.DeleteBranch(branchNameToDelete);
                //currentRepository.getBranchesMap().remove(branchNameToDelete);
            }
        } catch (RepositoryDoesnotExistException  | BranchDoesNotExistException | BranchFileDoesNotExistInFolderException e) {
            resp.sendError(402, e.getMessage());
        }
        catch(HeadBranchDeletedExcption ex)
        {
            resp.sendError(403, ex.getMessage());
        }

    }
}

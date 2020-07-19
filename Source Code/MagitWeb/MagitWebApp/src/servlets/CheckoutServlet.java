package servlets;

import Lib.RepositoryManager;
import MagitExceptions.BranchDoesNotExistException;
import MagitExceptions.BranchIsAllReadyOnWCException;
import MagitExceptions.CheckoutToRemoteBranchException;
import MagitExceptions.RepositoryDoesnotExistException;
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

public class CheckoutServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String branchName = req.getParameter(Constants.BRANCH_NAME);
        UserInSystem currentUser= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        RepositoryManager repositoryManager = currentUser.getRepositoryManager();
        try{
            synchronized (currentUser.getRepositoryLock(repositoryManager.GetCurrentRepositoryName())) {
                repositoryManager.CheckOut(branchName);
            }
        } catch (BranchIsAllReadyOnWCException | BranchDoesNotExistException | ParseException | RepositoryDoesnotExistException e) {
            resp.sendError(403,e.getMessage());
        }
        catch (CheckoutToRemoteBranchException ex){
            resp.sendError(402,ex.getMessage());
        }
    }
}

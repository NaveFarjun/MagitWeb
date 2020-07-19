package servlets;

import Lib.*;
import MagitExceptions.*;
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
import java.text.ParseException;

@WebServlet("/acceptPR")
public class AcceptPullRequestServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String newStatus=req.getParameter("newStatus");
        PullRequestStatus newPullRequestStatus=null;
        RepositoryManager repositoryManager = ServletUtils.getRepositoryManager(getServletContext(), req);
        String senderUserName=repositoryManager.getAllPullRequests().get(id).getPrSender().getName();
        UserInSystem senderUser=ServletUtils.getUserManaqer(getServletContext()).getUsers().get(senderUserName);
        Notification notification;
        if(newStatus.equals("accept")){
            newPullRequestStatus= PullRequestStatus.ACCEPTED;
            notification=new Notification(SessionUtils.getUserName(req),SessionUtils.getUserName(req)+
                    " accepted your pull request","accepting pull request");
            senderUser.getNotificationsManager().addNewMessage(notification);
        }
        else if(newStatus.equals("deny")){
            newPullRequestStatus= PullRequestStatus.DENIED;
            notification=new Notification(SessionUtils.getUserName(req),SessionUtils.getUserName(req)+
                    " denied your pull request. He says:\n'"+req.getParameter("messageForSender")+"'","Denial of pull request");
            senderUser.getNotificationsManager().addNewMessage(notification);
        }
        try {
            if(newPullRequestStatus!=null) {
                repositoryManager.solvePullRequest(id, newPullRequestStatus);
            }
            else{
                resp.sendError(403,"illegal expression in pull request status parameter ");
            }
        } catch (ParseException | CommitException | OpenChangesException e) {
            resp.sendError(403,e.getMessage());
        }
    }
}

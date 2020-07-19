package servlets;

import Lib.*;
import MagitExceptions.RepositoryDoesnotExistException;
import com.google.gson.Gson;
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
import java.io.PrintWriter;

@WebServlet("/PR")
public class PullRequestServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RepositoryManager repositoryManagerLR = ServletUtils.getRepositoryManager(getServletContext(), req);
        Repository LR = repositoryManagerLR.GetCurrentRepository();

        String repoName;
        try {
            repoName = repositoryManagerLR.GetCurrentRepositoryName();
            String[] arr = repoName.split("-");
            String RRownerName = arr[0];
            String RRName =arr[1];
            UserInSystem RROwner = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(RRownerName);
            Repository RR = RROwner.getRepositories().get(RRName);
            String message  = req.getParameter(Constants.PR_MESSAGE);
            Branch target = RR.getBranchesMap().get(req.getParameter(Constants.BRANCH_NAME));
            Branch base = RR.getBranchesMap().get(req.getParameter(Constants.BASE_BRANCH_NAME));
            User sendPr =ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req)).getUser();
            PullRequest pullRequest=new PullRequest(target,base,message, sendPr);
            RR.getPullRequestManager().AddPullRequest(pullRequest);
            Notification notification=new Notification(sendPr.getName(),sendPr.getName()+" send you new pull request in "+RR.getName()+".","new pull request");
            RROwner.getNotificationsManager().addNewMessage(notification);
        } catch (RepositoryDoesnotExistException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RepositoryManager repositoryManager = ServletUtils.getRepositoryManager(getServletContext(), req);
        Gson gson=new Gson();
        String json=gson.toJson(repositoryManager.getAllPullRequests());
        try(PrintWriter out=resp.getWriter()){
            out.print(json);
        }
    }
}

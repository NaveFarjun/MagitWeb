package servlets;

import Lib.CommitDetails;
import Lib.RepositoryManager;
import MagitExceptions.CommitException;
import MagitExceptions.RepositoryDoesnotExistException;
import com.google.gson.Gson;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class GetActiveBranchHistory extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        RepositoryManager repositoryManager = user.getRepositoryManager();
        if(repositoryManager.GetCurrentRepository()==null){
            resp.sendError(403,"none repository loaded!!");
            return;
        }
        resp.setContentType("application/json;charset=UTF-8");
        try {
            String branchName=req.getParameter("branchName");
            if(branchName==null){
                branchName=repositoryManager.GetCurrentRepository().getActiveBranch().getName();
            }
            List<CommitDetails> list=repositoryManager.ShowActiveBranchHistory(branchName);
            Gson gson=new Gson();
            String json=gson.toJson(list);
            try(PrintWriter out=resp.getWriter()){
                out.print(json);
            }
        } catch (CommitException | RepositoryDoesnotExistException e) {
            resp.sendError(403,e.getMessage());
        }

    }
}

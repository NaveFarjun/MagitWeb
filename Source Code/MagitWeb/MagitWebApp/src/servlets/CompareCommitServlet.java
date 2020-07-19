package servlets;

import Lib.Commit;
import Lib.FileDetails;
import Lib.RepositoryManager;
import Lib.SHA1;
import MagitExceptions.CommitException;
import MagitExceptions.RepositoryDoesnotExistException;
import com.google.gson.Gson;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebEndpoint;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;

@WebServlet("/compareCommits")
public class CompareCommitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserInSystem currentUser= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        RepositoryManager repositoryManager = currentUser.getRepositoryManager();
        Commit currrent=repositoryManager.GetCurrentRepository().getCommitFromMapCommit(new SHA1(req.getParameter("current")));
        Commit prev=repositoryManager.GetCurrentRepository().getCommitFromMapCommit(new SHA1(req.getParameter("prev")));
        try {
            List<List<String>> deltas=repositoryManager.compareTwoCommits(currrent,prev);
            Gson gson=new Gson();
            try(PrintWriter out=resp.getWriter()){
                out.print(gson.toJson(deltas));
            }
        } catch (CommitException | RepositoryDoesnotExistException | ParseException e) {
            resp.sendError(403,e.getMessage());
        }
    }
}

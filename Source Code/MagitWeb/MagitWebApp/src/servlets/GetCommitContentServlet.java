package servlets;

import Lib.FileDetails;
import Lib.RepositoryManager;
import Lib.SHA1;
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.List;

public class GetCommitContentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        SHA1 sha1=new SHA1(req.getParameter("sha1"));
        try {
            List<FileDetails> fileDetailsList=user.getRepositoryManager().ShowAllCommitFiles(sha1);
            fileDetailsList.forEach(v->v.setName(v.getName().replace(Constants.ALL_USERS_FOLDER + SessionUtils.getUserName(req)+"\\","")));
            try(PrintWriter out=resp.getWriter()){
                Gson gson=new Gson();
                String json=gson.toJson(fileDetailsList);
                out.print(json);
            }
        } catch (RepositoryDoesnotExistException | ParseException e) {
            resp.sendError(403,e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        RepositoryManager repositoryManager=user.getRepositoryManager();
        try {
            synchronized (user.getRepositoryLock(repositoryManager.GetCurrentRepositoryName())) {
                repositoryManager.MakeCommit(req.getParameter("commitMessage"), null);
            }
        } catch (ParseException | CommitException | RepositoryDoesnotExistException e) {
            resp.sendError(403,e.getMessage());
        }
    }
}

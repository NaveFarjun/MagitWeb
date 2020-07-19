package servlets;

import Lib.Commit;
import Lib.Repository;
import Lib.SHA1;
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

public class GetLastCommitServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("userName");
        Gson gson=new Gson();
        String json;
        if (userName == null) {
            userName = SessionUtils.getUserName(req);
        }
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        Repository repository=user.getRepositories().get(req.getParameter("repName"));
        List<SHA1> allCommitsSha1Sorted= repository.getCurrentRepositoryAllCommitsSHA1();
        if(allCommitsSha1Sorted.size()==0){
            json=gson.toJson(null);
        }
        else {
            Commit commit = repository.getCommitFromMapCommit(allCommitsSha1Sorted.get(0));
            json = gson.toJson(commit);
        }
        try(PrintWriter out=resp.getWriter()){
            out.print(json);
        }
    }
}
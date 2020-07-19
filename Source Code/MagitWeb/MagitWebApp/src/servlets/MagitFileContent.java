package servlets;

import Lib.Blob;
import Lib.Folder;
import Lib.RepositoryManager;
import Lib.SHA1;
import com.google.gson.Gson;
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

@WebServlet("/magitContent")
public class MagitFileContent extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sha1=req.getParameter("sha1");
        String type=req.getParameter("type");
        String userName = SessionUtils.getUserName(req);
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
        RepositoryManager repositoryManager = user.getRepositoryManager();
        Gson gson=new Gson();
        if(type.equals("FOLDER")){
            Folder folder=repositoryManager.GetCurrentRepository().getFoldersMap().get(new SHA1(sha1));
            try(PrintWriter out=resp.getWriter()){
                out.print(gson.toJson(folder));
            }
        }
        else if(type.equals("FILE")){
            Blob blob=repositoryManager.GetCurrentRepository().getBlobsMap().get(new SHA1(sha1));
            try(PrintWriter out=resp.getWriter()){
                out.print(gson.toJson(blob));
            }
        }
    }
}

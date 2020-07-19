package servlets;

import Lib.Branch;
import Lib.Repository;
import Lib.RepositoryManager;
import MagitExceptions.RepositoryDoesnotExistException;
import MagitExceptions.RepositorySameToCurrentRepositoryException;
import com.google.gson.Gson;
import constants.Constants;
import users.UserInSystem;
import users.UserManager;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Map;

public class GetAllBranches extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String userName = req.getParameter("userName");
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        RepositoryManager repositoryManager = user.getRepositoryManager();
        Repository repository=repositoryManager.GetCurrentRepository();
        if (userName != null) {
            UserInSystem originalOwnerOfRepo=ServletUtils.getUserManaqer(getServletContext()).getUsers().get(userName);
            try {
                repository=originalOwnerOfRepo.getRepositories().get(repositoryManager.GetCurrentRepositoryName().split("-")[1]);
            } catch (RepositoryDoesnotExistException e) {
                resp.sendError(403,e.getMessage());
            }
        }
        if(repositoryManager.GetCurrentRepository()==null){
            resp.sendError(403,"none repository loaded!!");
        }

        Map<String, Branch> branchesMap = repository.getBranchesMap();
        Gson gson = new Gson();
        String json = gson.toJson(branchesMap);
        try (PrintWriter out = resp.getWriter()) {
            out.print(json);
        }
    }
}

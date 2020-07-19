package servlets;

import Lib.FileUtils;
import constants.Constants;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

@WebServlet("/deleteDirectory")
public class DeleteDirectoryServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filePath = req.getParameter(Constants.FILE_PATH);
        String userName = SessionUtils.getUserName(req);
        String fullPath = Constants.ALL_USERS_FOLDER + userName + "\\" + filePath + "\\";
        File fileToDelete = new File(fullPath);
        if(!FileUtils.deleteDirectory(fileToDelete)){
            resp.sendError(402,"failed to delete directory");
        }
        else{
            resp.setStatus(200);
        }
    }
}

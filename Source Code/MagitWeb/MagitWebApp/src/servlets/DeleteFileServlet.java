package servlets;

import Lib.FileUtils;
import constants.Constants;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class DeleteFileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filePath = req.getParameter(Constants.FILE_PATH);
        String userName = SessionUtils.getUserName(req);
        String fullPath=Constants.ALL_USERS_FOLDER + userName + "\\" + filePath + "\\";
        File fileToDelete = new File(fullPath);
        if(!fileToDelete.delete()) {
            resp.sendError(402, "failed deleting file");
        }
        else
        {
        resp.setStatus(200);
        }
    }
}

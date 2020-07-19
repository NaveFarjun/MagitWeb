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

@WebServlet("/createFile")
public class CreateNewFileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = SessionUtils.getUserName(req);
        String fileContent = req.getParameter(Constants.FILE_CONTENT);
        String filePath = req.getParameter(Constants.FILE_PATH);
        boolean isDirectory = Boolean.parseBoolean(req.getParameter("isDirectory"));
        String name = req.getParameter(Constants.FILE_NAME);
        if (name.isEmpty()) {
            resp.sendError(402, "File without name");
            return;
        }
        String fullPath = Constants.ALL_USERS_FOLDER + userName + "\\" + filePath + "\\" + name;
        try {
            if(!isDirectory) {
                FileUtils.CreateTextFile(fullPath, fileContent);
            }
            else{
                File file=new File(fullPath);
                if(!file.mkdir()){
                    resp.sendError(403, "error in creating folder");
                }
            }
        } catch (IOException e) {
            resp.sendError(403, e.getMessage());
        }

    }
}

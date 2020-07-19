package servlets;

import Lib.FileUtils;
import Lib.RepositoryManager;
import MagitExceptions.*;
import users.UserInSystem;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class UploadNewRepositoryServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=UTF-8");
        Collection<Part> parts = req.getParts();
        StringBuilder fileContent = new StringBuilder();
        for (Part part : parts) {
            //to write the content of the file to a string
            fileContent.append(readFromInputStream(part.getInputStream()));
        }
        UserInSystem currentUser=ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        //RepositoryManager repositoryManager=SessionUtils.getRepoistoryManager(req);
        RepositoryManager repositoryManager= currentUser.getRepositoryManager();
        InputStream targetStream = new ByteArrayInputStream(fileContent.toString().getBytes());
        try {
            List<String> errorInXML= repositoryManager.CheckXml(targetStream);
            if(!errorInXML.isEmpty()){
                String errors="";
                for(String error:errorInXML){
                    errors=errors+error+"\n";
                }
                resp.sendError(403,"XML is not valid because:\n"+errors);
                return;
            }

        } catch (JAXBException | InvocationTargetException | XMLException | IllegalAccessException | NoSuchMethodException e) {
            resp.sendError(403,"Exception by trying to read XML file");
            return;
        }
        try {
            repositoryManager.LoadXML("C:\\magit-ex3\\"+SessionUtils.getUserName(req));
            currentUser.addRepository(repositoryManager.GetCurrentRepository());
            try(PrintWriter out=resp.getWriter()){
                out.print(repositoryManager.GetCurrentRepositoryName());
            }
        } catch (RepositoryDoesnotExistException | BranchIsAllReadyOnWCException | OpenChangesException | CommitException | CheckoutToRemoteBranchException | RepositoryAllreadyExistException | ParseException | BranchDoesNotExistException e) {
            resp.sendError(403,e.getMessage());
        }

    }
    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}

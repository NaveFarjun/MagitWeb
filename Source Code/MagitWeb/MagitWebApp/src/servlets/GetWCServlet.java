package servlets;

import Lib.FileUtils;
import com.google.gson.Gson;
import constants.Constants;
import utils.SessionUtils;
import utils.WCFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GetWCServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String userName = SessionUtils.getUserName(req);
        String folderPath = req.getParameter("folderPath");
        WCFile wcFile;
        String json;
        Gson gson = new Gson();
        File file = new File(Constants.ALL_USERS_FOLDER + userName + "\\" + folderPath + "\\");
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            String repoName=folderPath.split("\\\\")[0];
            List<WCFile> wcFileList=new ArrayList<>();
            for(File f:files){
                WCFile newWcFile;
                if(f.isDirectory()){
                    newWcFile=new WCFile(true,f.getPath());
                }
                else{
                    newWcFile=new WCFile(false,f.getPath());
                }
                wcFileList.add(newWcFile);
            }
            /*List<String> fileList = Arrays.stream(files).collect(Collectors.toList()).stream().map(File::getPath).collect(Collectors.toList());*/
            wcFileList.forEach(v->v.setContent(v.getContent().substring((Constants.ALL_USERS_FOLDER + userName + "\\" + repoName + "\\").length())));
            wcFileList.removeIf(v->v.getContent().equals(".magit"));
            /*fileList = fileList.stream().map(s -> s.substring((Constants.ALL_USERS_FOLDER + userName + "\\" + repoName + "\\").length())).collect(Collectors.toList());
            fileList.removeIf(v -> v.equals(".magit"));*/
            String listToJson = gson.toJson(wcFileList);
            wcFile=new WCFile(true,listToJson);
        } else {
            String fileContent = FileUtils.ReadContentFromFile(file);
            wcFile = new WCFile(false, fileContent);
        }
        json=gson.toJson(wcFile);
        try (PrintWriter out = resp.getWriter()) {
            out.print(json);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = SessionUtils.getUserName(req);
        String folderPath = req.getParameter("folderPath");
        String fullPath=Constants.ALL_USERS_FOLDER + userName + "\\" + folderPath + "\\";
        String newContent=req.getParameter("newContent");
        FileUtils.WriteToFile(newContent,fullPath);
    }
}


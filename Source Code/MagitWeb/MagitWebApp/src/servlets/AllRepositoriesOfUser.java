package servlets;

import com.google.gson.Gson;
import constants.Constants;
import utils.SessionUtils;

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

public class AllRepositoriesOfUser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String userName = req.getParameter("userName");
        if (userName == null) {
            userName = SessionUtils.getUserName(req);
        }
        Gson gson = new Gson();
        List<String> reposNames = new ArrayList<>();
        if (userName != null) {
            File file = new File(Constants.ALL_USERS_FOLDER + "\\" + userName);
            if (file.exists() && file.listFiles() != null) {
                Arrays.stream(file.listFiles()).forEach(v -> reposNames.add(v.getName()));
            }
        }
        String json = gson.toJson(reposNames);
        try (PrintWriter out = resp.getWriter()) {
            out.print(json);
        }
    }
}

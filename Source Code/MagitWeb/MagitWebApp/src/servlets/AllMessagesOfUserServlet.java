package servlets;

import com.google.gson.Gson;
import notification.NotificationsManager;
import users.UserInSystem;
import notification.Notification;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet("/allMessages")
public class AllMessagesOfUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        NotificationsManager notificationsManager=user.getNotificationsManager();
        Map<Integer,Notification> allNotifications=notificationsManager.getMessages();
        Gson gson=new Gson();
        String json=gson.toJson(allNotifications);
        try(PrintWriter out=resp.getWriter()){
            out.print(json);
        }
       // user.getNotificationsManager().setNumOfMessagesInLastTimeUpdated(allNotifications.size());
        user.getNotificationsManager().setLastPullVersion(allNotifications.size());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int messageID=Integer.parseInt(req.getParameter("messageID"));
        UserInSystem user= ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        NotificationsManager notificationsManager=user.getNotificationsManager();
        notificationsManager.unreadToRead(messageID);
    }
}

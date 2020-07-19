package servlets;

import com.google.gson.Gson;
import notification.NotificationsManager;
import users.UserInSystem;
import notification.Notification;
import utils.ServletUtils;
import utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserInSystem user = ServletUtils.getUserManaqer(getServletContext()).getUsers().get(SessionUtils.getUserName(req));
        NotificationsManager notificationsManager=user.getNotificationsManager();
        Map<Integer,Notification> allNotifications=notificationsManager.getMessages();
        int lastVersionPull=notificationsManager.getLastPullVersion();
        Map<Integer,Notification> deltas=new HashMap<>();
        if(allNotifications.size()>lastVersionPull){
            int size = allNotifications.size();
            for(int i = lastVersionPull+1 ; i <= size ;i++ ){
                deltas.put(i,allNotifications.get(i));
            }
            notificationsManager.setLastPullVersion(size);
        }
        JsonObject jsonObject=new JsonObject(notificationsManager.getNumOfUnreadMessages(),deltas);
        Gson gson=new Gson();
        String json=gson.toJson(jsonObject);
        try(PrintWriter out=resp.getWriter()){
            out.print(json);
        }
    }

    public class JsonObject{
        private int numOfUnreadMessages;
        private Map<Integer,Notification> messages;


        public JsonObject(int numOfUnreadMessages, Map<Integer, Notification> messages) {
            this.numOfUnreadMessages = numOfUnreadMessages;
            this.messages = messages;
        }
    }

}

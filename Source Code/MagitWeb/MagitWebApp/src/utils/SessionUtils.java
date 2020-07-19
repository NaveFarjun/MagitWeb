package utils;

import Lib.RepositoryManager;
import Lib.User;
import constants.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

    public static String getUserName(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(Constants.USERNAME) == null) {
            return null;
        }

        return session.getAttribute(Constants.USERNAME).toString();
    }

    public static void removeUserNameSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(Constants.USERNAME);
    }

   /* public static RepositoryManager getRepoistoryManager(HttpServletRequest request){
        HttpSession session=request.getSession(false);
        if(session==null){
            return null;
        }
        else{
            if(session.getAttribute(Constants.REPOSITORY_MANAGER)==null){
                session.setAttribute(Constants.REPOSITORY_MANAGER,new RepositoryManager());
                ((RepositoryManager)session.getAttribute(Constants.REPOSITORY_MANAGER)).ChangeUser(new User(session.getAttribute(Constants.USERNAME).toString()));

            }
            return (RepositoryManager)session.getAttribute(Constants.REPOSITORY_MANAGER);
        }
    }*/
}

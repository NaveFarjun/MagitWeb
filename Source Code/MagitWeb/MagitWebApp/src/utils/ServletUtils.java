package utils;

import Lib.Repository;
import Lib.RepositoryManager;
import MagitExceptions.RepositoryDoesnotExistException;
import users.UserInSystem;
import users.UserManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class ServletUtils {
    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";

    private static final Object userManagerLock = new Object();

    public static UserManager getUserManaqer(ServletContext servletContext) {
        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static RepositoryManager getRepositoryManager(ServletContext servletContext, HttpServletRequest req) {
        UserInSystem currentUser = ServletUtils.getUserManaqer(servletContext).getUsers().get(SessionUtils.getUserName(req));
        return currentUser.getRepositoryManager();
    }

    public static Repository getRRRepository(ServletContext servletContext, HttpServletRequest req) throws RepositoryDoesnotExistException {
        RepositoryManager repositoryManagerLR = ServletUtils.getRepositoryManager(servletContext, req);
        String repoName;
        repoName = repositoryManagerLR.GetCurrentRepositoryName();
        String[] arr = repoName.split("-");
        String RRownerName = arr[0];
        String RRName = arr[arr.length-1];
        UserInSystem RROwner = ServletUtils.getUserManaqer(servletContext).getUsers().get(RRownerName);
        return RROwner.getRepositories().get(RRName);
    }
}

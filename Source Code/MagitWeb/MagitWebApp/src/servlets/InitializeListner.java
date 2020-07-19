package servlets;

import Lib.FileUtils;
import constants.Constants;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

@WebListener
public class InitializeListner implements ServletContextListener {

    @Override
    public final void contextInitialized(final ServletContextEvent sce) {

    }

    @Override
    public final void contextDestroyed(final ServletContextEvent sce) {
        File file = new File(Constants.ALL_USERS_FOLDER);
        if(!FileUtils.deleteDirectory(file)){
            System.out.println("Failed to delete magit\\ex03 directory");
        }
    }
}
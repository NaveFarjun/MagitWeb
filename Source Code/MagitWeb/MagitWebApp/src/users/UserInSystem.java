package users;

import Lib.Repository;
import Lib.RepositoryManager;
import Lib.User;
import constants.Constants;

import notification.NotificationsManager;
import Lib.PullRequestManager;

import java.io.File;
import java.util.*;

public class UserInSystem {
    /*private final Object unreadMessagesLock=new Object();*/

    private User user;
    private RepositoryManager repositoryManager;
    private Map<String,Repository> repositories;
    private Map<String,Object> repositoriesLocks;
    private NotificationsManager notificationsManager;
    private PullRequestManager pullRequestManager;
    private boolean logedIn;



    public UserInSystem(User user){
        this.user=user;
        repositories=new HashMap<>();
        repositoryManager=new RepositoryManager();
        repositoryManager.ChangeUser(user);
        notificationsManager=new NotificationsManager();
        repositoriesLocks=new HashMap<>();
        createDirectoryForUser();
        logedIn=false;
        pullRequestManager = new PullRequestManager();

    }

    private void createDirectoryForUser() {
        File directory = new File(Constants.ALL_USERS_FOLDER+user.getName());
        if(!directory.mkdirs()){
            System.out.println("failed to make directory for: " +user.getName());
        }
    }

    public NotificationsManager getNotificationsManager() {
        return notificationsManager;
    }

    public void addRepository(Repository repository){
        repositories.put(repository.getName(),repository);
        final Object repositoryLock=new Object();
        repositoriesLocks.put(repository.getName(),repositoryLock);
    }

/*
    public void SendPR(PullRequest pr,UserInSystem toUser){
      pullRequestManager.AddPullRequest(pr,this,);
    }
*/

    public Map<String,Repository> getRepositories(){
        return Collections.unmodifiableMap(repositories);
    }

    public RepositoryManager getRepositoryManager(){
        return repositoryManager;
    }

    public User getUser() {
        return user;
    }

    public Object getRepositoryLock(String repoName){
        return repositoriesLocks.get(repoName);
    }

    public boolean isLogedIn() {
        return logedIn;
    }

    public void setLogedIn(boolean logedIn) {
        this.logedIn = logedIn;
    }

    public PullRequestManager getPullRequestManager(){
        return this.pullRequestManager;
    }
}

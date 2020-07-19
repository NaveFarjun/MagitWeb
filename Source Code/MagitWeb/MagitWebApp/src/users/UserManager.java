package users;

import Lib.User;

import java.util.*;

public class UserManager {

    private final Map<String,UserInSystem> usersMap;

    public UserManager() {
        usersMap = new HashMap<>();
    }

    public synchronized void addUser(UserInSystem user) {
        usersMap.put(user.getUser().getName(),user);
    }

    public synchronized void removeUser(String userName) {
        usersMap.remove(userName);
    }

    public synchronized Map<String,UserInSystem> getUsers() {
        return Collections.unmodifiableMap(usersMap);
    }

    public boolean isUserExists(String userName) {
        return usersMap.containsKey(userName);
    }

}
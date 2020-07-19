package notification;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NotificationsManager {
    private final Object unreadMessagesLock=new Object();
    private Map<Integer,Notification> messages;
    private int messageID = 1;
    private int lastPullVersion = 0;
    private int numOfUnreadMessages=0;

    public NotificationsManager(){
        messages=new HashMap<>();
    }

    public void addNewMessage(Notification notification){
        synchronized (unreadMessagesLock){
            messages.put(messageID, notification);
            messageID++;
            numOfUnreadMessages++;
        }

    }
    public void unreadToRead(int messageIndex){
        Notification notification=messages.get(messageIndex);
        if( notification!=null) {
            notification.setRead(true);
            synchronized (unreadMessagesLock) {
                numOfUnreadMessages--;
            }
        }
    }

    public Map<Integer, Notification> getMessages() {
        return Collections.unmodifiableMap(messages);
    }

    public int getLastPullVersion() {
        return lastPullVersion;
    }

    public void setLastPullVersion(int lastPullVersion) {
        this.lastPullVersion = lastPullVersion;
    }

    public int getNumOfUnreadMessages() {
        return numOfUnreadMessages;
    }
}

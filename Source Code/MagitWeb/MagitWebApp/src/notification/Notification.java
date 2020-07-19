package notification;

import Lib.OurDate;
import users.UserInSystem;

import java.util.Date;

public class Notification {
    private String addressed;
    private String message;
    private String subject;
    private OurDate date;



    private Boolean isRead;


    public Notification(/*UserInSystem addressed,*/ String addressed, String message, String subject) {
        //this.addressed = addressed;
        this.addressed=addressed;
        this.message = message;
        this.subject = subject;
        date= new OurDate();
        isRead=false;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setRead(Boolean isRead) {
        this.isRead = isRead;
    }

   /* public UserInSystem getAddressed() {
        return addressed;
    }*/


    public String getMessage() {
        return message;
    }

    /*public void setAddressed(UserInSystem addressed) {
        this.addressed = addressed;
    }*/


    public void setMessage(String message) {
        this.message = message;
    }
}

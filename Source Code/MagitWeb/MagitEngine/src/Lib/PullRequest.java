package Lib;

public class PullRequest {
    private User prSender;
    private Branch target;
    private Branch base;
    private String message;
    private OurDate createdTime;
    private PullRequestStatus status;

    public PullRequest(Branch target, Branch base, String message, User prSender){
        this.target=target;
        this.base=base;
        this.message=message;
        this.createdTime=new OurDate();
        this.prSender=prSender;
        this.status=PullRequestStatus.OPEN;
    }

    public void setStatus(PullRequestStatus status){
        this.status=status;
    }

    public Branch getTarget() {
        return target;
    }

    public Branch getBase() {
        return base;
    }

    public User getPrSender() {
        return prSender;
    }
}

package Lib;

import Lib.PullRequest;
import Lib.PullRequestStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PullRequestManager {
    private int counter = 1;
    private Map<Integer, PullRequest> pullRequestMap;

    public PullRequestManager() {
        pullRequestMap = new HashMap<>();
    }

    public void AddPullRequest(PullRequest pullRequestToAdd){
        pullRequestMap.put(counter,pullRequestToAdd);
        counter++;
    }

    public void SolvePR(int id, PullRequestStatus status){
        PullRequest pr = pullRequestMap.get(id);
        pr.setStatus(status);
    }

    public Map<Integer,PullRequest> getPullRequestMap(){
        return Collections.unmodifiableMap(pullRequestMap);
    }
}

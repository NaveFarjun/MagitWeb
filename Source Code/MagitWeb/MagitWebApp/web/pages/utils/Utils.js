function createRepoElement(name, activeBranchName, numOfBranches, lastCommitDate, lastCommitMessage) {
    var repository = document.createElement('div');
    repository.classList.add("rep");
    var content = document.createElement('div');
    content.classList.add("content");
    var repoName = createElement('h3', name);
    repoName.classList.add("header-name");
    var activeBranch = createElement('h4', "Active branch: " + activeBranchName);
    var branchesNumber = createElement('h4', "Num of branches: " + numOfBranches);
    if(lastCommitDate!==undefined && lastCommitMessage!==undefined) {
        var commitDate = createElement('h4', "Last commit created at: " + lastCommitDate);
        var commitMessage = createElement('h4', "Last commit message: " + lastCommitMessage);
        content.append(repoName, activeBranch, branchesNumber, commitDate, commitMessage);
    }
    else{
        content.append(repoName, activeBranch, branchesNumber);
    }
    repository.append(content);
    return repository;
}

function createElement(elementTag, text) {
    var element = document.createElement(elementTag);
    element.innerText = text;
    return element;
}

function getActiveBranchName(repository, user) {
    var repoName = "sos";
    $.ajax({
        dataType: 'json',
        data: {userName:user, repName: repository},
        url: resolveUrl('/getRepository'),
        async: false,
        success: function (r) {
            repoName = r.m_ActiveBranch.m_Name;
        }
    });
    return repoName;
}

function getNumOfBranches(repository, user) {
    var numOfBranches = 0;
    $.ajax({
        dataType: 'json',
        data: {userName:user,repName: repository},
        url: resolveUrl('/numOfBranches'),
        method: 'post',
        async: false,
        success: function (r) {
            numOfBranches = r;
        }
    });
    return numOfBranches;
}

function getLastCommit(repository, user) {
    var lastCommit = null;
    $.ajax({
        dataType: 'json',
        data: {userName:user, repName: repository},
        url: resolveUrl('/getLastCommit'),
        method: 'post',
        async: false,
        success: function (r) {
            lastCommit = r;
        }
    });
    return lastCommit;
}

function getContextPath() {
    var base = document.getElementsByTagName('base')[0];
    if (base && base.href && (base.href.length > 0)){
        base = base.href;
    } else {
        base = document.URL;
    }
    var base = window.location.pathname;
    return base.substr(0, base.indexOf("/", 1));
}

function resolveUrl(url){
    return (getContextPath() + "/" + url).replace("//", "/");
}
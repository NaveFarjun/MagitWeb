var user;
var myUserName;

$(function () {
    var url_string = window.location.href;
    var url = new URL(url_string);
    user = url.searchParams.get("user");

    $('#userRepositories').text(user + "/Repositories");

    $.ajax({
        url: resolveUrl('/allMyRepos'),
        method: 'GET',
        data: {userName: user},
        dataType: 'json',
        success: function (r) {
            //var repositoryDiv = document.createElement('div');
            //repositoryDiv.classList.add("repository");
            for (var i = 0; i < r.length; i++) {
                var repository;
                var ActiveBranchName = getActiveBranchName(r[i], user);
                var NumOfBranches = getNumOfBranches(r[i], user);
                var Commit = getLastCommit(r[i], user);
                if(Commit!=null) {
                    repository = createRepoElement(r[i], ActiveBranchName, NumOfBranches, Commit.m_CreateTime.m_Date, Commit.m_Message);
                }
                else{
                    repository = createRepoElement(r[i], ActiveBranchName, NumOfBranches);
                }
                repository.setAttribute("repoName", r[i]);
                createForkBtn(repository);
                //repositoryDiv.append(repository);
                $("#repos").append(repository);
            }
            var back = ceateBackBtn();
            $("#repos").append(back);
        }
    })
})

function ceateBackBtn() {
   return  $("<button type='btn' class='btn-primary'>Back</button>").click(function () {
        $.ajax({
            url: resolveUrl('/userName'),
            method: 'GET',
            success: function (userName) {
                myUserName = userName;
                location.href = resolveUrl('/pages/userPage/UserPage.html?user=' + myUserName);
            },
            error: function (xhr, status, error) {
                alert(xhr.responseText);

            }
        });
    }).css({"position": "relative", "left": "8px", "display": "inline-block","font-size":"15px"," background-color":"red"});
}

function createForkBtn(repo) {

    $("<button type='btn' class='btn-primary'>Fork</button>").click(function () {
        $.ajax({
        url: resolveUrl('/fork'),
        method: 'post',
        data: {userNameToFork: user, repositoryName: repo.getAttribute("repoName")},
        success: function () {
            location.href = resolveUrl('/pages/userPage/UserPage.html');
        }
    })

    }).css({"position": "relative", "left": "8px","top":"0px" /*"display": "inline-block"*/}).appendTo(repo);
}
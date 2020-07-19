/*var modal = document.getElementById("myModal");
var span = document.getElementById("closeModal");*/
var userName;
$(function () {
    setInterval(getNotifications, 2000);
    setInterval(getUsers, 1000);
    getUsers();
    $.ajax({
        method: 'GET',
        url: resolveUrl('/userName'),
        success: function (r) {
            $('#welcome').text("welcome " + r + "!");
            userName=r;
        }
    });

    $.ajax({
        url: resolveUrl("/allMessages"),
        dataType: "json",
        success: function (r) {
            var size = Object.keys(r).length;
            if (size > 0) {
                var start = parseInt(Object.keys(r)[0]);
                var end = start + size - 1;
                for (var i = start; i <= end; i++) {
                    var user = r[i].addressed/*.user.m_Name*/;
                    var subject = r[i].subject;
                    var date = r[i].date.m_Date;
                    var message = r[i].message;
                    var messageInMailbox = $("<label class='mailBoxLabel'></label>").attr({
                        "message": message,
                        "messageNumber": i,
                        "isRead": r[i].isRead,
                        "subject":subject
                    }).css("display", "block").text(user + ": " + subject + " (" + date + ")").click(function () {
                        if ($(this).attr("isRead") == "false") {
                            $(this).css("background-color", "transparent");
                            changeMessageStatus($(this).attr("messageNumber"))
                            $(this).attr("isRead", "true");
                        }
                        $("#modalMelel").empty().append($("<h3></h3>").html($(this).attr("subject")),$("<p></p>").html($(this).attr("message")));
                       /* $("#modalMelel").html($(this).attr("message"));*/
                        $("#myModal").css("display", "block");
                        $("#closeModal").click(function () {
                            $("#myModal").css("display", "none");
                        })
                    }).appendTo("#mailbox");
                    if (r[i].isRead == false) {
                        $(messageInMailbox).css("background-color", "blue");
                    }
                }
            }
        }
    });

    $("#uploadForm").submit(function (event) {
        event.preventDefault();
        var file = this[0].files[0];
        var formData = new FormData();
        formData.append('theFile', file);
        $.ajax({
            method: 'POST',
            data: formData,
            url: resolveUrl('/upload'),
            processData: false,
            contentType: false,
            timeout: 4000,
            success: function (r) {
                var repository;
                var ActiveBranchName = getActiveBranchName(r);
                var NumOfBranches = getNumOfBranches(r);
                var Commit = getLastCommit(r);
                if(Commit!=null) {
                    repository = createRepoElement(r, ActiveBranchName, NumOfBranches, Commit.m_CreateTime.m_Date, Commit.m_Message);
                }
                else{
                    repository = createRepoElement(r, ActiveBranchName, NumOfBranches);
                }
                repoNameOnClick(repository, r);
                $("#repos").append(repository);
            },
            error: function (xhr, status, error) {
                var responseTitle = $(xhr.responseText).filter('p').get(1);
                alert($(responseTitle).text());
            }
        });
        return false;
    });

    $.ajax({
        url: resolveUrl('/allMyRepos'),
        method: 'GET',
        success: function (r) {
            for (var i = 0; i < r.length; i++) {
                var repository;
                var ActiveBranchName = getActiveBranchName(r[i]);
                var NumOfBranches = getNumOfBranches(r[i]);
                var Commit = getLastCommit(r[i]);
                if(Commit!=null) {
                    repository = createRepoElement(r[i], ActiveBranchName, NumOfBranches, Commit.m_CreateTime.m_Date, Commit.m_Message);
                }
                else{
                    repository = createRepoElement(r[i], ActiveBranchName, NumOfBranches);
                }
                //var repository = createRepoElement(r[i], ActiveBranchName, NumOfBranches, Commit.m_CreateTime.m_Date, Commit.m_Message);
                repoNameOnClick(repository, r[i]);
                $("#repos").append(repository);
            }
        }
    });



    $("#logOutBtn").click(function () {
        logOut();
    })
});

function logOut() {
    $.ajax({
        url:resolveUrl("/logout"),
        method:"post",
        success:function () {
            location.href=resolveUrl('/index.html');
        }

    })

}

function getUsers(){
    $.ajax({
        url: resolveUrl('/usersInSystem'),
        method: 'GET',
        success: function (r) {
            $("#usersNames").empty();
            for (var i = 0; i < r.length; i++) {
                var userName = r[i];
                createUserElement(userName, $("#usersOptions"));
            }

        }
    })
}

function getNotifications() {
    $.ajax({
        url: resolveUrl("/notification"),
        method: "get",
        dataType: "json",
        success: function (r) {
            var deltaSize = Object.keys(r.messages).length;
            if (deltaSize != 0) {
                var start = parseInt(Object.keys(r.messages)[0]);
                var end = start + deltaSize - 1;
                $("#notif").empty().css("visibility", "visible");
                $("<p id='exitBtnNotif'>X</p>").click(function () {
                    $("#notif").empty().css("visibility", "hidden");
                }).appendTo("#notif");
                $("<h5></h5>").text("you have " + r.numOfUnreadMessages + " unread messages").appendTo("#notif");
                for (var i = start; i <= end; i++) {
                    $("<label class='mailBoxLabel'></label>").attr({
                        "subject": r.messages[i].subject,
                        "message": r.messages[i].message,
                        "messageNumber": i,
                        "isRead": r.messages[i].isRead
                    })
                        .css({
                            "display": "block",
                            "background-color": "blue"
                        }).text(r.messages[i].addressed+ ": " + r.messages[i].subject + " (" + r.messages[i].date.m_Date + ")").click(function () {
                        if($(this).attr("isRead")=="false") {
                            $(this).css("background-color", "transparent");
                            changeMessageStatus($(this).attr("messageNumber"));
                            $(this).attr("isRead", "true");
                        }
                        $("#modalMelel").empty().append($("<h3></h3>").html($(this).attr("subject")),$("<p></p>").html($(this).attr("message")));
                        $("#myModal").css("display", "block");
                        $("#closeModal").click(function () {
                            $("#myModal").css("display", "none");
                        })
                    }).appendTo("#mailbox");
                }
            }
        }
    })
}

function changeMessageStatus(messageID) {
    $.ajax({
        url: resolveUrl("/allMessages"),
        method: 'post',
        data: {messageID: messageID}
    })
}

function createUserElement(name, select) {
    var pName = document.createElement('p');
    pName.innerText = name;
    if (userName == name) {
        pName.classList.add("myName");
        pName.disabled = true;
    } else {
        pName.classList.add("userName");
        pName.onclick = function (ev) {
            location.href = resolveUrl('/pages/userInSystemInformation/userInSystemInfo.html?user=' + name)
        }
    }
    $("#usersNames").append(pName);
    //return select;
}

function repoNameOnClick(repository, name) {

    var header = repository.getElementsByClassName('header-name')[0];
    header.onclick=function () {
        location.href = resolveUrl('/pages/repositoryPage/repositoryPage.html?repoName=' + name+'&userName='+userName);
    }
}

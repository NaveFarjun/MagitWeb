var repoName;
var repository;
var modal;
var messages;

$(function () {
    var url_String = window.location.href;
    var url = new URL(url_String);
    repoName = url.searchParams.get("repoName");
    userName = url.searchParams.get("userName");
    document.getElementById("repoName").innerText = repoName;
    setInterval(getNotifications, 1000);
    $.ajax({
        url: resolveUrl("/allMessages"),
        dataType: "json",
        success: function (r) {
            messages = r;
        }
    });

    $.ajax({
        url: resolveUrl("/loadRepo"),
        dataType: 'json',
        data: {repName: repoName},
        method: 'POST',
        success: function () {

        },
        error: function (xhr, status, error) {
            if (xhr.status == 403) {
                var responseTitle = $(xhr.responseText).filter('p').get(1);
                alert($(responseTitle).text());
            }
        }
    });
    initBtns();
    $("#allBranchesBtn").click(function () {
        getRepoAsJson();
        if(Object.keys(repository.m_CommitMap).length==0){
            alert("you have no commits!!");
            return;
        }
        $("#workArea").empty();
        $("<h2>Branches</h2>").appendTo("#workArea");
        $("<input type='text' id='newBranch'>").css({
            "margin-right": "20px",
            "margin-left": "8px",
            "color": "black"
        }).appendTo("#workArea");
        $("<button type='btn' class='btn-primary'>Add branch</button>").click(function () {
            addBranchOnClicked($("#newBranch").val());
        }).css({"display": "inline-block","background-color": "#4cae4c"}).appendTo("#workArea");
        $.ajax({
            url: resolveUrl("/getAllBranches"),
            method: "get",
            dataType: "json",
            success: function (r) {
                for (var i = 0; i < Object.keys(r).length; i++) {
                    var branchDisplay = createBranchDispalyElement(r[Object.keys(r)[i]]);
                    $("#workArea").append(branchDisplay);
                }
            }

        })

    });

    $("#HeadBranchHistory").click(function () {
        getRepoAsJson();
        if(Object.keys(repository.m_CommitMap).length==0){
            alert("you have no commits!!");
            return;
        }
        $.ajax({
            url: resolveUrl("/getHeadBranchHistory"),
            success: function (r) {
                $("#workArea").empty().append($("<h2>Head branch history</h2>"));
                buildBranchHistory(r);
                $("#workArea").append($("<br>"),$("<h4>new branch:</h4>").css("margin-top","10px"),$("<label>Name:</label>"),$("<input type='text' id='brancName'>").css({
                    "margin-right": "20px",
                    "margin-left": "8px",
                    "color": "black"
                }), $("<label>Sha1:</label>"),$("<Select type='text' id='brancSha1'></Select>").css({
                    "margin-right": "20px",
                    "margin-left": "8px",
                    "color": "black"
                }),$("<button type='btn' class='btn-primary'>Add</button>").css({"background-color": "#4cae4c","padding":"5px 8px"}).click(function () {
                    addBranchOnClicked($("#brancName").val(),$("#brancSha1").val())
                }));
                for(var i=0;i<r.length;i++){
                    $("<option  selected='selected'></option>").val(r[i].m_CommitSHA1.m_Sh1).html(r[i].m_CommitSHA1.m_Sh1).appendTo("#brancSha1");
                }
            }
        })
    });

    modal = document.getElementById("myModal");

    var span = document.getElementsByClassName("close")[0];

    span.onclick = function () {
        modal.style.display = "none";
    };

    window.onclick = function (event) {
        if (event.target == modal) {
            modal.style.display = "none";
        }
    };

    $("#wc").click(function () {
        getWc(repoName);
    })

    $("#commitBtn").click(function () {
        var commitMessage = prompt("Please enter commit message:", "");
        if (commitMessage != null) {
            makeCommit(commitMessage);
        }
    });
    $("#wcStatus").click(function () {
        if(Object.keys(repository.m_CommitMap).length==0){
            alert("you have no commits!!");
            return;
        }
        $.ajax({
            url: resolveUrl("/ShowStatus"),
            dataType: "json",
            success: function (r) {
                $("#workArea").empty();
                for (var i = 0; i < 3; i++) {
                    if (i == 0) {
                        $("#workArea").append("<h3>added:</h3>")
                    }
                    if (i == 1) {
                        $("#workArea").append("<h3>changed:</h3>")
                    }
                    if (i == 2) {
                        $("#workArea").append("<h3>deleted:</h3>");
                    }
                    if (r[i].length == 0) {
                        $("#workArea").append($("<p>none</p>"));
                    }
                    for (var j = 0; j < r[i].length; j++) {
                        $("#workArea").append($("<p></p>").text(r[i][j].substring(13 + userName.length + 1)));
                    }
                }
            }
        })
    });

    $("#prBtn").click(function () {
        $.ajax({
            url: resolveUrl("/PR"),
            method: 'GET',
            dataType: "json",
            success: function (r) {
                var keys = Object.keys(r);
                $("#workArea").empty();
                $("<table class='table'></table>").attr({"id": "prTable"}).append($("<thead class='thead-dark'></thead>")
                    .css("background-color", "gray").append($("<tr></tr>").append($("<th scope='col'>base</th>"), $("<th scope='col' >target</th>"), $("<th scope='col'>message</th>"),
                        $("<th scope='col' >sender</th>"), $("<th scope='col' >sent at</th>"), $("<th scope='col'>request status</th>"), $("<th scope='col'></th>"), $("<th scope='col'></th>")))).appendTo("#workArea");
                for (var i = 0; i < keys.length; i++) {
                    createPullRequestDisplay(r[keys[i]], keys[i]);
                }
            }
        })
    });

    $("#newPrBtn").click(function () {
        $("#headerContent").html("New pull request");
        $("#content").empty().append($("<label>Target branch:</label>").css("color", "black"), $("<select id='selectTargetBranch' class='form-control'></select>"),
            $("<label>Base branch:</label>").css("color", "black"), $("<select id='selectBaseBranch' class='form-control'></select>"),
            $("<label>Message</label>").css("color", "black"),
            $("<textarea class='form-control' id='FormControlTextarea' rows='3'></textarea>"), $("<button class='btn-info'>Send</button>")
                .css({"display": "block", "margin-top": "10px"}).click(function () {
                    sendPR($("#FormControlTextarea").val(), $("#selectBaseBranch").children('option:selected').val(), $("#selectTargetBranch").children('option:selected').val());

                }));
        $.ajax({
            url: resolveUrl("/getAllBranches"),
            method: "get",
            dataType: "json",
            success: function (r) {
                for (var i = 0; i < Object.keys(r).length; i++) {
                    var branch = r[Object.keys(r)[i]];
                    if (branch.followAfter !== undefined) {
                        $("<option selected='selected'></option>").val(branch.m_Name).html(branch.m_Name).appendTo("#selectTargetBranch");
                    }
                }
            }
        });
        var otherUser = repoName.split("-")[0];
        $.ajax({
            url: resolveUrl("/getAllBranches"),
            method: "get",
            data: {userName: otherUser},
            dataType: "json",
            success: function (r) {
                for (var i = 0; i < Object.keys(r).length; i++) {
                    var branch = r[Object.keys(r)[i]];
                    $("<option selected='selected'></option>").val(branch.m_Name).html(branch.m_Name).appendTo("#selectBaseBranch");
                }
            }
        });
        $("#myModal").css("display", "block");
    });

    $("#backBtn").click(function () {
        location.href = resolveUrl('/pages/userPage/UserPage.html');
    });

    $("#notifBox").click(function () {
        $("#headerContent").html("Notifications");
        $("#content").empty();
        var start = parseInt(Object.keys(messages)[0]);
        var end = start + Object.keys(messages).length - 1;
        if(Object.keys(messages).length==0){
            $("#content").append($("<h2>No messages! :-(</h2>"))
        }
        for (var i = start; i <= end; i++) {
            $("#content").append($("<label class='mailBoxLabel'></label>").attr({
                "id": i,
                "message": messages[i].message,
                "subject": messages[i].subject,
                "date": messages[i].date.m_Date,
                "addressed": messages[i].addressed,
                "isRead": messages[i].isRead
            })
                .click(function () {
                    if($(this).attr("isRead")==="false"){
                        changeMessageStatus($(this).attr("id"));
                        messages[$(this).attr("id")].isRead=true;
                    }
                    $("#headerContent").html($(this).attr("addressed") + ": " + $(this).attr("subject") + "(" +
                        $(this).attr("date") + ")");
                    $("#content").empty().append($("<p></p>").text($(this).attr("message")), $("<p><-back</p>").click(function () {
                        $("#notifBox").click();
                    }).css({
                        "cursor": "pointer", "color": "blue",
                        "text-decoration": "underline"
                    }))
                }).text(messages[i].addressed + ": " + messages[i].subject + "(" +
                    messages[i].date.m_Date + ")"));
            if ($("#"+ i).attr("isRead")==="false") {
                $("#"+ i).css("background-color", "blue");
            }
        }

        modal.style.display = "block";
    })

});

function initBtns() {
    getRepoAsJson();
    if (repository.RRLocation !== undefined) {
        document.getElementById("newPrBtn").disabled = false;
    } else {
        document.getElementById("prBtn").disabled = false;
    }
}

function createPullRequestDisplay(PR, id) {
    var prStatusTd = $("<td id='prStatusTd'></td>");
    var examBtn = $("<td id='prExam'></td>")
    $("#prTable").append($("<tr></tr>").append($("<td></td>").text(PR.base.m_Name),
        $("<td></td>").text(PR.target.m_Name), $("<td></td>").text(PR.message),
        $("<td></td>").text(PR.prSender.m_Name), $("<td></td>").text(PR.createdTime.m_Date),
        $("<td></td>").text(PR.status), $(prStatusTd), $(examBtn)));
    if (PR.status == "OPEN") {
        $(prStatusTd).append($("<button class='btn btn-info'>Accept</button>").css({"background-color": "#4cae4c"}).click(function () {
            ajaxAcceptPR(id, "accept", "");
        }), $("<button class='btn btn-info'>Deny</button>").css({"background-color": "#c9302c"}).click(function () {
            $("#headerContent").text("Deny message for pull request sender");
            $("#content").empty().append($("<label>Explain why you denied the request:</label>")
                , $("<textarea class='form-control' id='textExplanation' rows='3'></textarea>")
                , $("<button class='btn-info'>Finish</button>")
                    .css({"display": "block", "margin-top": "10px"}).click(function () {
                        ajaxAcceptPR(id, "deny", $("#textExplanation").val());
                    }));
            modal.style.display = "block";

        }));
        var branchName = PR.target.m_Name;
        $(examBtn).append($("<btn class='btn btn-info'>Exam</btn>").click(function () {
            $.ajax({
                url: resolveUrl("/getHeadBranchHistory"),
                data: {branchName: branchName},
                dataType: 'json',
                success: function (r) {
                    $("#workArea").empty().append($("<button class='btn btn-info'>Aggregation</button>").click(function () {
                        CompareTwoCommits(PR.base.m_CommitSH1.m_Sh1,PR.target.m_CommitSH1.m_Sh1)
                    }));
                    buildBranchHistory(r);
                    $("#workArea").append($("<button class='btn btn-info'>Back</button>").click(function () {
                        $("#prBtn").click()
                    }))
                }
            })
        }))
    }
}

function buildBranchHistory(r) {
    for (var i = 0; i < r.length; i++) {
        var commitElement = createCommitDispalyElement(r[i], modal);
        commitElement.classList.add("commitElement");
        if (i == 0) {
            commitElement.style.paddingTop = "20px";
        }
        commitElement.style.paddingLeft = "20px";
        $("#workArea").append(commitElement);
        if (i != r.length - 1) {
            var line = createElement("div", "");
            line.classList.add("line");
            $("#workArea").append(line);
        }
    }
}


function getWc(folderPath) {
    $.ajax({
        url: resolveUrl('/getWC'),
        data: {folderPath: folderPath},
        success: function (r) {
            if (r.isDirectory == true) {
                createFolderListArea(r, folderPath);
            } else {
                createFileArea(r, folderPath);
            }
        }
    })
}

function saveContentOfFileInWC(filePath, newContent) {
    $.ajax({
        url: resolveUrl('/getWC'),
        method: 'post',
        data: {folderPath: filePath, newContent: newContent},
        success: function () {
            alert("The file saved successfully")
        }
    })
}

function deleteFile(filePath) {
    console.log("deleting file: " + filePath);
    $.ajax({
        url: resolveUrl('/deleteFile'),
        data: {filePath: filePath},
        method: 'POST',
        success: function () {
            alert("File deleted successfully");
            var arrayPath = filePath.split("\\");
            filePath = filePath.substring(0, filePath.length - arrayPath[arrayPath.length - 1].length);
            getWc(filePath);
        },
        error: function (xhr, status, error) {
            var responseTitle = $(xhr.responseText).filter('p').get(1);
            alert($(responseTitle).text());
        }
    })

}

function deleteDirectory(folderPath) {
    console.log("deleting file: " + folderPath);
    $.ajax({
        url: resolveUrl('/deleteDirectory'),
        data: {filePath: folderPath},
        method: 'POST',
        success: function () {
            alert("Folder deleted successfully");
            var arrayPath = folderPath.split("\\");
            folderPath = folderPath.substring(0, folderPath.length - arrayPath[arrayPath.length - 1].length);
            getWc(folderPath);
        },
        error: function (xhr, status, error) {
            var responseTitle = $(xhr.responseText).filter('p').get(1);
            alert($(responseTitle).text());
        }
    })

}

function createFileArea(r, filePath) {
    $("#workArea").empty();
    var div = document.createElement('div');
    var textArea = document.createElement("textarea");
    textArea.rows = 20;
    textArea.cols = 80;
    textArea.value = r.content;
    textArea.style.color = "black";
    textArea.disabled = true;
    div.append(textArea);
    var sendBtn = $("<button class='btn btn-info'>Save</button>").click(function () {
        saveContentOfFileInWC(filePath, textArea.value);
    })
    $('<input type="checkbox" />').attr({id: "editable"}).appendTo("#workArea");
    $("#editable").click(function () {
        if (textArea.disabled == true) {
            textArea.disabled = false;
        } else {
            textArea.setAttribute("disabled", "true");
            textArea.disabled = true;
        }
    });
    $('<label>edit mode</label>').appendTo("#workArea");
    var backBtn = $("<button class='btn btn-info'>Back</button>").click(function () {
        var arrayPath = filePath.split("\\");
        filePath = filePath.substring(0, filePath.length - arrayPath[arrayPath.length - 1].length);
        getWc(filePath);
    });
    $("#workArea").append(div, backBtn, sendBtn);
}

function buildRow(contentElement) {
    var row = document.createElement('tr');
    row.style.padding = "10px";
    var icontd = document.createElement('td');
    row.style.padding = "10px";
    var trashtd = document.createElement('td');
    var img;
    var fullPath = repoName + "\\" + contentElement.content;
    var trash = $("<img src='trash-can-icon-transparent-18.jpg' width='25px' height='30px'>");
    trash.attr('data-relativePath', fullPath);
    trash.addClass("trashImage");

    if (contentElement.isDirectory == true) {
        img = $("<img src='resources/folder.png' width='30px' height='25px'>");
        trash.on('click', function (e) {
            deleteDirectory($(e.target).attr('data-relativePath'));
            var path = fullPath.substring(0, fullPath.lastIndexOf("\\"));
            getWc(path);
        });
    } else {
        img = $("<img src='resources/text-file-icon-11.jpg' width='25px' height='30px'>");
        trash.on('click', function (e) {
            deleteFile($(e.target).attr('data-relativePath'));
            var path = fullPath.substring(0, fullPath.lastIndexOf("\\"));
            getWc(path);
        });

    }
    trash.appendTo(trashtd);
    img.appendTo(icontd);
    var fileNametd = document.createElement('td');
    fileNametd.style.paddingRight = "30px";


    var pName = document.createElement('h5');
    var contentElementParts = contentElement.content.split("\\");
    pName.classList.add("folderText");
    pName.innerText = contentElementParts[contentElementParts.length - 1];
    pName.onclick = function () {
        getWc(repoName + "\\" + contentElement.content)
    };
    fileNametd.append(pName);
    row.append(icontd, fileNametd, trashtd);
    return row;
}

function createFolderListArea(r, filePath) {
    $("#workArea").empty();
    var table = $("<table></table>").css({"border-collapse": "separate", "border-spacing": "1em"});
    r.content = JSON.parse(r.content);
    for (var i = 0; i < r.content.length; i++) {
        var tr = buildRow(r.content[i]);
        table.append(tr);
    }
    $("#workArea").append(table);
    if (filePath != repoName && filePath != (repoName + "\\")) {
        $("#workArea").append($("<p class='folderText'><-back</p>").click(function () {
            if (filePath.charAt(filePath.length - 1) == "\\") {
                filePath = filePath.substring(0, filePath.length - 1);
            }
            var arrayPath = filePath.split("\\");
            filePath = filePath.substring(0, filePath.length - arrayPath[arrayPath.length - 1].length);
            getWc(filePath);
        }));
    }
    $("#workArea").append($("<button  type='button' class='btn btn-info'>Add new file</button>")
        .css("margin-right", "10px").click(function () {
            createAddFileModal(filePath);
        }), $("<button  type='button' class='btn btn-info'>Add new folder</button>").click(function () {
        createAddNewFolderModal(filePath);
    }));
}

function createAddFileModal(filePath) {
    $("#headerContent").html("Create new file");
    var input = document.createElement('input');
    var fileNameLabel = document.createElement('p');
    fileNameLabel.style.color = "black";
    fileNameLabel.innerText = "File name";
    var textArea = document.createElement('textarea');
    textArea.cols = "80";
    textArea.rows = "20";
    textArea.style.color = "black";
    var savebtn = document.createElement('button');
    savebtn.innerHTML = 'Save';
    savebtn.classList.add("btn-info");
    savebtn.onclick = function () {
        saveNewFile(false, textArea.value, input.value, filePath)
    };
    $("#content").empty().append($("<div></div>").append(fileNameLabel, input), $("<div></div>").append($("<p>file content</p>"), textArea), savebtn);
    modal.style.display = "block";
}

function createAddNewFolderModal(filePath) {
    $("#content").empty();
    $("<p>Folder name:</p>").css("color", "black").appendTo("#content");
    $("<input>").attr({"type": "text", "id": "folderName"}).appendTo("#content");
    $("<button class='btn btn-dark'>add</button>").click(function () {
        saveNewFile(true, null, $("#folderName").val(), filePath);
    }).appendTo("#content");
    modal.style.display = "block";
}

function deleteBranch(branchName) {
    $.ajax({
        url: resolveUrl("/deleteBranch"),
        method: 'post',
        data: {branchName: branchName},
        success: function () {
            alert(branchName + " deleted successfully");
            $("#allBranchesBtn").click();
        },
        error: function (xhr, status, error) {
            if (xhr.status == 402) {
                alert("impossible to delete remote branch - only remote tracking branches or local branches");
            } else if (xhr.status == 403) {
                alert("impossible to delete this branch because it is Head branch in RR");
            } else {
                alert("impossible to delete this branch");
            }
        }

    })
}

function createBranchDispalyElement(branch) {
    var commitSha1 = branch.m_CommitSH1.m_Sh1;
    var container = document.createElement('div');
    var branchName = document.createElement('p');
    branchName.innerText = "Branch name: " + branch.m_Name;
    var isActiveBranch = (repository.m_ActiveBranch.m_Name == branch.m_Name);
    if (isActiveBranch) {
        branchName.innerText = branchName.innerText + "------> Head branch";
        container.classList.add("headBranch");
    }
    var branchCommitSha1 = document.createElement('p');
    branchCommitSha1.innerText = "Commit SHA1: " + commitSha1;
    var commitMessage = document.createElement('p');
    commitMessage.innerText = "Commit message: " + repository.m_CommitMap[commitSha1].m_Message;
    container.append(branchName, branchCommitSha1, commitMessage);
    var buttonContent = "Checkout";
    var sidedBtn = $("<button type='button' class='btn btn-info'></button>").text(buttonContent).attr({
        "isActive": isActiveBranch,
        "branchName": branch.m_Name
    }).click(
        function () {
            checkout(branch.m_Name);
            getRepoAsJson();
        }).css({
        "position": "relative",
        "left": "665px",
        "display": "inline-block",
        "background-color": "#4cae4c"
    }).appendTo(container);
    var deleteBtn = $("<button type='button' class='btn btn-info'>Delete</button>").attr({
        "isActive": isActiveBranch,
        "branchName": branch.m_Name
    }).click(
        function () {
            deleteBranch(branch.m_Name);
            getRepoAsJson();
        }).css({
        "position": "relative",
        "left": "680px",
        "display": "inline-block",
        "background-color": "#c9302c"
    }).appendTo(container);


    if (isActiveBranch) {
        $(sidedBtn).css("visibility", "hidden");
        $(deleteBtn).css("visibility", "hidden");
        if (branch.followAfter) {
            $("<button type='btn' class='btn btn-info'>Push</button>").click(function () {
                pushBranch(repository.m_ActiveBranch.m_Name, false);
            }).css({
                "position": "relative",
                "left": "530px",
                "display": "inline-block",
                "background-color": "#4cae4c"}).appendTo(container);
            $("<button type='btn' class='btn btn-info'>Pull</button>").click(function () {
                pull();
            }).css({
                "position": "relative",
                "left": "560px",
                "display": "inline-block",
                "background-color": "#4cae4c"}).appendTo(container);
        } else if (repository.RRLocation) {
            $("<button type='btn' class='btn btn-info'>Push new local branch</button>").click(function () {
                pushBranch(repository.m_ActiveBranch.m_Name, true);
            }).css({
                "position": "relative",
                "left": "500px",
                "display": "inline-block",
                "background-color": "#4cae4c"}).appendTo(container);
        }
    }
    container.classList.add("branchDiv");
    return container;
}

function createCommitDispalyElement(commit, modal) {
    var separator = createElement("span", "");
    separator.classList.add("separator");
    var separator2 = createElement("span", "");
    separator2.classList.add("separator");
    var separator3 = createElement("span", "");
    separator3.classList.add("separator");
    var container = createElement("div", "");
    var dot = createElement("span", "");
    dot.classList.add("dot");
    var CommitMessage = createElement('span', "  " + commit.m_Message + "  ");
    var CommitSha1 = createElement('span', "  " + commit.m_CommitSHA1.m_Sh1.substring(0, 6) + "...  ");
    var CommitCreator = createElement('span', "  " + commit.m_WhoUpdated.m_Name + "  ");
    var CreationTime = createElement('span', "  " + commit.m_WhenUpdated.m_Date + "  ");
    container.append(dot, CommitMessage, separator, CommitSha1, separator2, CommitCreator, separator3, CreationTime);
    container.onclick = function () {
        commitClicked(commit.m_CommitSHA1.m_Sh1, commit.m_PrevCommits, modal);
    };
    return container;
}

function addBranchOnClicked(branchName,sha1) {
    $.ajax({
        data: {branchName: branchName,sha1:sha1},
        url: resolveUrl("/addBranch"),
        method: 'post',
        success: function () {
            getRepoAsJson();
            $("#allBranchesBtn").click();
        },
        error: function (xhr, status, error) {
            if (xhr.status == 402) {
                var responseTitle = $(xhr.responseText).filter('p').get(1);
                alert($(responseTitle).text());
            } else {
                alert("unknown error");
            }
        }
    });
}

function ajaxAcceptPR(id, newStatus, messageForSender) {
    $.ajax({
        url: resolveUrl('/acceptPR'),
        method: 'POST',
        data: {id: id, newStatus: newStatus, messageForSender: messageForSender},
        success: function () {
            if (newStatus == "accept") {
                alert("pull request accepted successfully");
            } else {
                alert("pull request denied successfully");
            }
            modal.style.display = "none";
            $("#prBtn").click();
        },
        error: function (xhr, status, error) {
            alert(xhr.responseText);
        }
    })
}

function sendPR(message, baseBranchName, targetBranchName) {
    $.ajax({
        url: resolveUrl('/PR'),
        data: {prMessage: message, baseBranchName: baseBranchName, branchName: targetBranchName},
        method: 'post',
        success: function (r) {
            alert("pull request successfully sent");
            modal.style.display = "none";
        },
        error: function (xhr, status, error) {
            var responseTitle = $(xhr.responseText).filter('p').get(1);
            alert($(responseTitle).text());
        }

    })
}

function createElement(elementTag, text) {
    var element = document.createElement(elementTag);
    element.innerText = text;
    return element;
}

function commitClicked(commitSha1, prevCommits, modal) {
    $("#headerContent").html("Commit files");
    modal.style.display = "block";
    $.ajax({
        data: {sha1: commitSha1},
        dataType: "json",
        url: resolveUrl("/getCommitFiles"),
        success: function (r) {
            $("#content").empty().append($("<ul id='myUL'></ul>"));
            r.sort();
            for (var i = 0; i < r.length; i++) {
                var fileContent = document.createElement("p");
                fileContent.innerText = r[i].m_Name + ", " + r[i].m_FileType + ", " + r[i].m_Sh1.m_Sh1 + ", " + r[i].m_WhoUpdatedLast.m_Name + ", " + r[i].m_LastUpdated.m_Date;
                $(fileContent).attr({
                    "sha1": r[i].m_Sh1.m_Sh1,
                    "type": r[i].m_FileType,
                    "name": r[i].m_Name
                }).css("cursor", "pointer");
                fileContent.onclick = function (ev) {
                    getContentOfMagitFile($(this).attr("sha1"), $(this).attr("type"), $(this).attr("name"), commitSha1, prevCommits)
                };
                $("#content").append(fileContent);
            }
            $("#content").append($("<select id='prevCommitsCombo' class='form-control'></select>"),
                $("<button type='button' class='btn-info'>Compare with prev commit</button>").click(function () {
                    CompareTwoCommits($("#prevCommitsCombo").children('option:selected').val(), commitSha1);
                }))
            for (var i = 0; i < prevCommits.length; i++) {
                $("<option selected='selected'></option>").val(prevCommits[i].m_Sh1).html(prevCommits[i].m_Sh1).appendTo("#prevCommitsCombo");
            }

        }
    });
}

function CompareTwoCommits(prevCommitsSha1, currentCommitSha1) {
    $.ajax({
        url: resolveUrl("/compareCommits"),
        data: {prev: prevCommitsSha1, current: currentCommitSha1},
        dataType: "json",
        success: function (r) {
            $("#headerContent").text("Two commits comparison");
            $("#content").empty();
            for (var i = 0; i < 3; i++) {
                if (i == 0) {
                    $("#content").append("<h3>added:</h3>")
                }
                if (i == 1) {
                    $("#content").append("<h3>changed:</h3>")
                }
                if (i == 2) {
                    $("#content").append("<h3>deleted:</h3>");
                }
                if (r[i].length == 0) {
                    $("#content").append($("<p>none</p>"));
                }
                for (var j = 0; j < r[i].length; j++) {
                    $("#content").append($("<p></p>").text(r[i][j].substring(13 + userName.length + 1)));
                }
            }
            modal.style.display = "block";
        }

    })
}


function getContentOfMagitFile(sha1, type, fileName, commitSha1, prevCommits) {
    $.ajax({
        url: resolveUrl("/magitContent"),
        data: {sha1: sha1, type: type},
        dataType: 'json',
        success: function (r) {
            $("#headerContent").html(fileName);
            if (type == "FILE") {
                $("#content").empty().append($("<textarea class='form-control' id='FileTextArea' rows='10'></textarea>").val(r.m_Content)
                    , $("<button class='btn btn-info'>Back</button>").click(function () {
                        commitClicked(commitSha1, prevCommits, modal);
                    }));
            } else {
                $("#content").empty();
                r.m_InnerFiles.forEach(function (v) {
                    $("#content").append($("<p></p>").text(v.m_Name))
                })
                $("#content").append($("<button class='btn btn-info'>Back</button>").click(function () {
                    commitClicked(commitSha1, prevCommits, modal);
                }));
            }
        }

    })

}

function getRepoAsJson() {
    $.ajax({
        url: resolveUrl("/getRepository"),
        data: {repName: repoName},
        dataType: 'json',
        async: false,
        success: function (r) {
            repository = r;
        },
        error: function (xhr, status, error) {
            if (xhr.status == 403) {
                var responseTitle = $(xhr.responseText).filter('p').get(1);
                alert($(responseTitle).text());
            }
        }
    });
}

var checkout = function (branch) {
    function createRTB(branch) {
        $.ajax({
            url: resolveUrl("/createRTB"),
            method: 'post',
            data: {branchName: branch},
            error: function (xhr, status, error) {
                var responseTitle = $(xhr.responseText).filter('p').get(1);
                alert($(responseTitle).text());
            }

        })
    }

    $.ajax({
        url: resolveUrl("/checkout"),
        method: 'post',
        data: {branchName: branch},
        success: function () {
            $("#allBranchesBtn").click();
        },
        error: function (xhr, status, error) {
            if (xhr.status == 402) {
                if (confirm("You cannot do checkout to remote branch\n Do you want to create remote tracking branch? ")) {
                    createRTB(branch);
                    alert("RTB for " + branch + " created successfully");
                    $("#allBranchesBtn").click();
                }

            } else {
                var responseTitle = $(xhr.responseText).filter('p').get(1);
                alert($(responseTitle).text());
            }
        }
    })
};

function saveNewFile(isDirectory, fileContent, inputFileName, filePath) {
    console.log("in save file");
    $.ajax({
        url: resolveUrl('/createFile'),
        method: 'post',
        data: {isDirectory: isDirectory, fileContent: fileContent, fileName: inputFileName, filePath: filePath},
        success: function () {
            alert("file saved successfully");
            modal.style.display = "none";
            getWc(filePath);
        },
        error: function (xhr, status) {
            if (xhr.status == 402) {
                alert("please give a name to your file (with extension)");
            } else {
                alert("servlet failed");
            }

        }
    })
}

function makeCommit(commitMessage) {
    $.ajax({
        url: resolveUrl('/getCommitFiles'),
        method: 'post',
        data: {commitMessage: commitMessage},
        success: function () {
            $("#HeadBranchHistory").click();
            getRepoAsJson();
        },
        error: function (xhr, status, error) {
            var responseTitle = $(xhr.responseText).filter('p').get(1);
            alert($(responseTitle).text());
        }

    })

}

function pull() {
    $.ajax({
        url: resolveUrl('/pull'),
        method: 'GET',
        success: function () {
            alert("pull done successfully");
            $("#HeadBranchHistory").click();
            getRepoAsJson();
        },
        error: function (xhr, status, error) {
            var responseTitle = $(xhr.responseText).filter('p').get(1);
            alert($(responseTitle).text());
        }

    })

}

function pushBranch(branchName, isNewBranch) {
    $.ajax({
        url: resolveUrl("/push"),
        method: "POST",
        data: {branchName: branchName, isNewBranch: isNewBranch},
        success: function () {
            alert("push done successfully");
            $("#allBranchesBtn").click();
        },
        error: function (xhr, status, error) {
            var responseTitle = $(xhr.responseText).filter('p').get(1);
            alert($(responseTitle).text());
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
                    messages[i] = r.messages[i];
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
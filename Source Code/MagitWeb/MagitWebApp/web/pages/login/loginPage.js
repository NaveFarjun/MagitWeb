$(function () {
    var userName = $("#userName");
    $.ajax({
        method: 'GET',
        url: resolveUrl('/userName'),
        success: function (r) {
            if (r != "null") {
                location.href = resolveUrl('/pages/userPage/UserPage.html');
            }
        }
    });

    $("#logInBtn").click(function (e) {
        e.preventDefault();
        $.ajax({
            data: userName,
            url:resolveUrl('/Login') ,
            success: function () {
                $("#s").text("you logged in");
                $("#s").css("color", "black");
                location.href = resolveUrl('/pages/userPage/UserPage.html');
            },
            error: function (xhr, status, error) {
                var responseTitle = $(xhr.responseText).filter('p').get(1);
                $("#s").text($(responseTitle).text().substring(8)).css("color", "red");
            }
        });
    })
});
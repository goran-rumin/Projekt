$(document).ready(function() {

    $.ajax({
        url: root+"user/active/",
        type : "POST",
        data : {}
    }).success(function(msg) {
        var json = JSON.parse(msg);
        var activeUserID = parseInt(json.data.id);

        $("#submit").on("click", function () {
            update(activeUserID);
        })

        $("#newsfeedLink").on("click", function (){
            openNewsfeed(activeUserID);
        })

        $("#wallLink").on("click", function (){
            openWall(activeUserID);
        })


        $("#galleryLink").on("click", function (){
            openGallery(activeUserID);
        })

        $("#messagesLink").on("click", function (){
            openMessages(activeUserID);
        })

    })

})

function update(userID) {
    var root= "../../";
    $.ajax ({
        url: root +  "user/info/index.php",
        type : "POST",
        data : {
            userId: userID
        }
    }).success(function(msg) {
        var userData = JSON.parse(msg);

    var username = userData.data.username;
    var name = $("#name").val();
    var lastName = $("#lastName").val();
    var password = $("#password").val();
    var passwordConfirm = $("#passwordConfirm").val();
    var root= "../../";

    if(password != passwordConfirm) {
        $("#disclaimer").show();
        $("#disclaimer").html("Passwords do not match!");
    }

    if (name == '' && lastName == '' && password == '') {
        $("#disclaimer").show();
        $("#disclaimer").html("Please fill in at least one of the fields!");
    }

    else if (name != '' && lastName != '' && password != '' && password == passwordConfirm) {
        $("#disclaimer").hide();
        $.ajax({
            url: root + "user/edit/index.php",
            type: "POST",
            data: {
                username: username,
                name: name,
                lastname: lastName,
                password: password
            }

        }).success(function (msg) {
            console.log(msg);
            success();
            $("#page").children("input").val("");
        })
    }else if (password == passwordConfirm && password != '' && passwordConfirm != ''){
        $("#disclaimer").hide();
        $.ajax({
            url: root + "user/edit/index.php",
            type: "POST",
            data: {
                username: username,
                password: password
            }

        }).success(function (msg) {
            console.log(msg);
            success();
            $("#page").children("input").val("");
        })
    } else if (name != '' && lastName != ''){
        $("#disclaimer").hide();
        $.ajax({
            url: root + "user/edit/index.php",
            type: "POST",
            data: {
                username: username,
                name: name,
                lastname: lastName
            }

        }).success(function (msg) {
            console.log(msg);
            success();
            $("#page").children("input").val("");
        })
    } else  if (lastName != ''){
        $("#disclaimer").hide();
        $.ajax({
            url: root + "user/edit/index.php",
            type: "POST",
            data: {
                username: username,
                lastname: lastName
            }

        }).success(function (msg) {
            console.log(msg);
            success();
            $("#page").children("input").val("");
        })
    } else if (name != '') {
        $("#disclaimer").hide();
        $.ajax({
            url: root + "user/edit/index.php",
            type: "POST",
            data: {
                username: username,
                name: name
            }

        }).success(function (msg) {
            console.log(msg);
            success();
            $("#page").children("input").val("");
        })
    }

    function success (){
        $("#bottomDisc").text("User details changed successfully!");
        setTimeout(function () {
            $("#bottomDisc").html("")
        }, 2000);
    }
    })


    //profile picture missing
}
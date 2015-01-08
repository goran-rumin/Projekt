$(document).ready(function() {
	$("#bottomDisc").hide();
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
    var username = userID;
    var name = $("#name").val();
    var lastName = $("#lastName").val();
    var password = $("#password").val();
    var passwordConfirm = $("#passwordConfirm").val();
    var root= "../../";

    if(password != passwordConfirm) {
        $("#bottomDisc").show();
        $("#bottomDisc").html("Passwords do not match!");
    }
	
	if (name == '' || lastName == '') {
        $("#bottomDisc").show();
        $("#bottomDisc").html("Please enter both your first and last names!");
    }

    if (name == '' && lastName == '' && password == '') {
        $("#bottomDisc").show();
        $("#bottomDisc").html("Please fill in at least one of the fields!");
    }

    else if (name != '' && lastName != '' && password != '' && password == passwordConfirm) {
        $("#bottomDisc").hide();
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
        $("#bottomDisc").hide();
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
        $("#bottomDisc").hide();
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
    } 

    function success (){
        $("#bottomDisc").show();
        $("#bottomDisc").removeClass("alert-danger").addClass("alert-success");
        $("#bottomDisc").html("User details changed successfully!");
        setTimeout(function () {
            $("#bottomDisc").html("");
            $("#bottomDisc").removeClass("alert-success").addClass("alert-danger");
            $("#bottomDisc").hide();
        }, 2000);
    }
    })


    //profile picture missing
}
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
        });

        $("#newsfeedLink").on("click", function (){
            openNewsfeed(activeUserID);
        });

        $("#wallLink").on("click", function (){
            openWall(activeUserID);
        });


        $("#galleryLink").on("click", function (){
            openGallery(activeUserID);
        });

        $("#messagesLink").on("click", function (){
            openMessages(activeUserID);
        });

        $("#upload").on("click", function(){
            postPicture(event);
        })

    })

})

function update(userID) {
    var name = $("#name").val();
    var lastName = $("#lastName").val();
    var password = $("#password").val();
    var passwordConfirm = $("#passwordConfirm").val();

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
                userId: userID,
                name: name,
                lastname: lastName,
                password: password
            }

        }).success(function (msg) {
            success();
            $("#page").children("input").val("");
        })
    }else if (password == passwordConfirm && password != '' && passwordConfirm != ''){
        $("#bottomDisc").hide();
        $.ajax({
            url: root + "user/edit/index.php",
            type: "POST",
            data: {
                userId: userID,
                password: password
            }

        }).success(function (msg) {
            success();
            $("#page").children("input").val("");
        })
    } else if (name != '' && lastName != ''){
        $("#bottomDisc").hide();
        $.ajax({
            url: root + "user/edit/index.php",
            type: "POST",
            data: {
                userId: userID,
                name: name,
                lastname: lastName
            }

        }).success(function (msg) {
            success();
            $("#page").children("input").val("");
        })
    }  else if (name != '' && lastName =='' || name=='' && lastName != '') {
        $("#bottomDisc").show();
        $("#bottomDisc").html("Please fill in both name and last name fields!");
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
}
function postPicture (event) {

    if ($(event.target).parent().children().has("input").length ==0){
        $(event.target).text("Cancel upload");
        x = document.createElement("input");
        wrap = document.createElement("div");
        form = document.createElement("form");
        submit = document.createElement("input");

        $(wrap).addClass("inputWrapper").insertAfter($(event.target));

        $(x).addClass("inputFile")
            .attr("type", "file")
            .appendTo($(form));

        $(form).attr("method", "post").attr("enctype", "multipart/form-data")
            .attr("target","upload_target").attr("id", "sendPhoto").appendTo($(wrap));

        $(submit).attr("type", "submit").val("Upload").attr("id", "submit").appendTo($(wrap));

    } else {
        $(event.target).parent().children(".inputWrapper").remove();
        $(event.target).text("Post picture");

    }


};


    //profile picture missing

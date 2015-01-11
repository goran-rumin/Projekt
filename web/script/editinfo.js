$(document).ready(function() {
    $("#bottomDisc").hide();

    $.ajax({
        url: root+"user/active/",
        type : "POST",
        data : {}
    }).success(function(msg) {
        var json = JSON.parse(msg);
        var activeUserID = parseInt(json.data.id);
        var userData;

        $.ajax({
            url: root + "user/info/index.php",
            type: "POST",
            data: {
                userId: activeUserID
            }

        }).success(function (msg) {
            var mssg = JSON.parse(msg);
            userData = mssg.data;
            $("#page").children("input").val("");
            pic = document.createElement("img");

            if (userData.picture == null) userData.picture = "../images/profile_default_big.png";
            $(pic).attr("id", "profilePic2").attr("src", userData.picture)
                .css("max-height", "200px").css("max-width", "200px;").insertAfter("#profilePic");
        })

        $("#submit2").on("click", function () {
            update(activeUserID, userData);
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
            postPicture(event, activeUserID);
        })

    })

})

var pictureURL="";
var url="";


function update(userID, userData) {

    var name;
    var lastName;

    if ($("#name").val() != "") {
        name = $("#name").val();
    } else name = userData.name;

    if ($("#lastName").val() != "") {
        lastName = $("#lastName").val();
    } else lastName = userData.lastname;


    var password = $("#password").val();
    var passwordConfirm = $("#passwordConfirm").val();

    if (password != passwordConfirm) {
        $("#bottomDisc").show();
        $("#bottomDisc").html("Passwords do not match!");
    }

    if ($("#name").val() == '' && $("#lastName").val() == '' && $("#password").val() == '') {
        $("#bottomDisc").show();
        $("#bottomDisc").html("Please fill in at least one of the fields!");

    } else if (password == passwordConfirm && password != '' && passwordConfirm != '') {
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
    } else if ($("#name").val() != '' || $("#lastName").val() != '') {
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
    }

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


function postPicture (event, userID) {

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

        $(x).change(function(){
            if (this.files[0].type != "image/jpeg") {
                $("#uploadMsg").html("Wrong file chosen, only supported .jpg photos. Please choose correct file.")
                setTimeout(function () {
                    $("#uploadMsg").html("")
                }, 4000);
                $(event.target).parent().children(".inputWrapper").remove();
                $(event.target).text("Profile picture");

                return false;
            } else if ($(".inputFile").prop("files")[0].size > 1048576) {
                $("#uploadMsg").html("Image you have selected is too big. Upload limit is 1 MB.");
                setTimeout(function () {
                    $("#uploadMsg").html("");
                }, 4000);
                $(event.target).parent().children(".inputWrapper").remove();
                $(event.target).text("Profile picture");
                return false;
            }
            readImage( this );
        });

        $(form).attr("method", "post").attr("enctype", "multipart/form-data")
            .attr("target","upload_target").attr("id", "sendPhoto").appendTo($(wrap));

        $(submit).attr("type", "submit").val("Upload").attr("id", "submit")
            .on("click", function(){

                $("#uploadMsg").html("Your image is uploading.");
                $(event.target).hide();
                $.ajax({
                    url: root + "photos/upload/index.php",
                    type: "POST",
                    data: {
                        userId: userID,
                        url: pictureURL,
                        message: "Hey! I just changed my profile picture, check it out!"
                    }
                }).success(function (msg) {
                    console.log(msg);
                    var response = JSON.parse(msg);
                    url = response.data.url;
                    $("#uploadMsg").html("Upload finished!");
                    setTimeout(function () {
                        $("#uploadMsg").html("Profile picture uploaded. Save your changes.")
                    }, 2000);
                    $(event.target).show();
                    $(event.target).text("Save changes");

                })
            }).appendTo($(wrap));

    } else if ($(event.target).text() == "Save changes") {
        $(event.target).parent().children(".inputWrapper").remove();
        $("#bottomDisc").hide();
        $.ajax({
            url: root + "user/edit/index.php",
            type: "POST",
            data: {
                userId: userID,
                pictureUrl: url
            }

        }).success(function (msg) {
            console.log(msg);
            success();
            $("#page").children("input").val("");
            $("#uploadMsg").html("Profile picture changed.");
            setTimeout(function () {
                $("#uploadMsg").html("")
            }, 2000);
            $(event.target).text("Profile picture");

            pic = document.createElement("img");

            $("#profilePic2").remove();
            $(pic).attr("id", "profilePic").attr("src", url).css("max-height", "200px").css("max-width", "200px").insertAfter("#profilePic");
        })

    }else{
        $(event.target).parent().children(".inputWrapper").remove();
        $(event.target).text("Post picture");

    }


};

function readImage(input) {
    if ( input.files && input.files[0] ) {
        var FR= new FileReader();
        FR.onload = function(e) {
            pictureURL = e.target.result.substr(e.target.result.indexOf(",")+1);
            console.log(pictureURL);
        };
        FR.readAsDataURL( input.files[0] );
    }
}


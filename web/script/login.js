/**
 * Created by Illona on 5.12.2014..
 */

$(document).ready(
    function(){
        $("#registB").click(function () {
            $("#hidden").show();
        })
        $("#loginB").on("click", function() {
            login();
        })
        $("#submit").on("click", function () {
            register();
        })

    })


function login() {
    var username = $("#usernameL").val();
    var password = $("#passwordL").val();
    if( username =='' || password ==''){
        $("#msgL").html("PLEASE FILL IN ALL THE FIELDS.")
    }
    else{
        $.ajax({
            url: root +"user/login/index.php",
            type: "POST",
            data: {
                username: username,
                password: password
            }
        }).success(function (msg) {
            var user = JSON.parse(msg);
            if (user.data.length != 0) {
                window.location.replace(rootRed + "newsfeed/");
            }
            else {
                $("#msgL").html("Wrong username or password.")
            }
        })
    }
}


function register(){
    var username = $("#usernameR").val();
    var password = $("#passwordR").val();
    var passwordRepeat = $("#passwordRepeat").val();
    var name = $("#name").val();
    var lastname = $("#lastname").val();
    var email = $("#email").val();

    if( username =='' || password =='' || name=='' || lastname =='' || email=='' || passwordRepeat==''){
        $("#msgR").html("PLEASE FILL IN ALL THE FIELDS.")
    }

    else if(password!=passwordRepeat){
        $("#msgR").html("PASSWORDS DON'T MATCH!")
    }

    else{
        $.ajax({
            url: root +"user/register/index.php",
            type: "POST",
            data: {
                username: username,
                password: password,
                repeatPassword: passwordRepeat,
                name: name,
                lastname: lastname,
                email: email
            }
        }).success(function(msg){
            console.log(msg);
            var mssg = JSON.parse(msg);
            var error = mssg.error;
            if (error.length != 0) {
                if (error.errNum == 2) {
                    $("#msgR").html("Invalid data formats. Please reenter your data.");
                } else if (error.errNum == 5) {
                    $("#msgR").html("User with that username already exists.");
                } else if (error.errNum == 6) {
                    $("#msgR").html("User with that email already exists.");
                }
            } else {
                window.location.replace(rootRed + "editinfo/");
            }

        })
    }
}





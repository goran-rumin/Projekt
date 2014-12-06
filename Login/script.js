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
            url: "user/login/index.php",
            type: "POST",
            data: {
                username: username,
                password: password
            }
        }).success(function (msg) {
            console.log(msg);
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
            url: "user/register/index.php",
            type: "POST",
            data: {
                username: username,
                password: password,
                name: name,
                lastname: lastname,
                email: email
            }
        }).success(function(msg){
            console.log(msg);
        })
    }
}





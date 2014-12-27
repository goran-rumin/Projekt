$(document).ready(function() {
    $("#submit").on("click", function() {
        update();
    })
})

function update() {
    var username = $("#username").val();
    var name = $("#name").val();
    var lastName = $("#lastName").val();
    var password = $("#password").val();
    var passwordConfirm = $("#passwordConfirm").val();

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
            url: "../ferbook/user/edit/index.php",
            type: "POST",
            data: {
                username: username,
                name: name,
                lastname: lastName,
                password: password
            }

        }).success(function (msg) {
            console.log(msg);
        })
    }

    if (password == passwordConfirm && password != '' && passwordConfirm != ''){
        $("#disclaimer").hide();
        $.ajax({
            url: "../ferbook/user/edit/index.php",
            type: "POST",
            data: {
                username: username,
                password: password
            }

        }).success(function (msg) {
            console.log(msg);
        })
    }

    if (name != '' && lastName != ''){
        $("#disclaimer").hide();
        $.ajax({
            url: "../ferbook/user/edit/index.php",
            type: "POST",
            data: {
                username: username,
                name: name,
                lastname: lastName
            }

        }).success(function (msg) {
            console.log(msg);
        })
    }

    //profile picture missing
}
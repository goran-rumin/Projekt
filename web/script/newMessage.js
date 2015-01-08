/**
 * Created by Illona on 12.12.2014..
 */

var app=angular.module("NewMessage", []);

app.controller("newMsgController", function($scope) {
    var userID2=0;

    $.ajax({
        url: root+"user/active/",
        type : "POST",
        data : {}
    }).success(function(msg) {
        var json = JSON.parse(msg);
        $scope.activeUserID = parseInt(json.data.id);
        $scope.$apply();
        console.log($scope.activeUserID);
        if ($scope.activeUserID== -1) $scope.activeUser=location.search.split('userId=')[1];


        $scope.search = function () {
            var textInput = $("#name").val();
            console.log(textInput);

            $.ajax({
                url: root + "search/index.php",
                type: "POST",
                data: {

                    query: textInput
                }
            }).success(function (msg) {
                console.log(msg);
                $scope.userFriendsData = JSON.parse(msg);

                var ufr = $scope.userFriendsData.data;
                $scope.$apply();

                $scope.listFriends(ufr);


            })
        }

        $scope.listFriends = function (ufr) {
            if (ufr.length == 0) {
            d = document.createElement("div");
            $(d).addClass("commentsError")
                .text("Nothing to show.")
                .appendTo($(".friends"));
                setTimeout(function () {
                    $(".commentsError").html("")
                }, 2000);
        }

            else {
                //  Object.getOwnPropertyNames(ufr).forEach(function (val) {
                for(val in ufr) {
                    if (val == "length") return false;
                    d = document.createElement("div");
                    var friendStat;
                    $.ajax({
                        url: root + "friends/status/index.php",
                        type: "POST",
                        data: {
                            userId1: $scope.activeUserID,
                            userId2: ufr[val].id
                        }
                    }).success(function (msg) {
                        $scope.status = JSON.parse(msg);
                        $scope.$apply();

                        friendStat = $scope.status.data.statusNumber;
                        if(friendStat ==1 ){
                            d = document.createElement("div");
                            item = document.createElement("li");
                            infoAdd = document.createElement("div");
                            nameDiv = document.createElement("div");
                            linkText = document.createElement("span");

                            $(item).appendTo(".friends");

                            $(infoAdd).addClass("infoAdd").appendTo($(item));

                            $(nameDiv).addClass("nameContainer").appendTo($(infoAdd));
                            $(linkText)
                                .on("click", function () {
                                    $("#name").val(ufr[val].name + " " + ufr[val].lastname);
                                    $scope.userID2 = ufr[val].id;
                                    $scope.$apply();
                                    console.log("klik");
                                    console.log($scope.userID2);
                                })
                                .text(ufr[val].name + " " + ufr[val].lastname)
                                .appendTo($(nameDiv));


                        }

                    })

                        }

                    }


            }

            $scope.send=function(){
                var message = $("#message").val();
                var name = $("#name").val();

                if( name="" || message == ''){
                    $("#msgL").html("PLEASE FILL IN ALL THE FIELDS.")
                }

                else{
                    $.ajax({
                        url: root + "messages/send/index.php",
                        type: "POST",
                        data: {
                            userId1: $scope.activeUserID,
                            userId2: $scope.userID2,
                            message: message
                        }
                    }).success(function (msg) {
                        $("#msgL").html("Your message was sent!");
                        $("#message").val("");
                        $("#name").val("");
                        $(".friends").remove();
                        console.log($scope.userID2);
                        setTimeout(function () {
                            $(".commentsError").html("")
                        }, 5000);


                    })
                }
            }

            $("#newsfeedLink").on("click", function () {
                openNewsfeed($scope.activeUserID);
            });


            $("#wallLink").on("click", function () {
                openWall($scope.activeUserID);
            });


            $("#galleryLink").on("click", function () {
                openGallery($scope.activeUserID);
            });

            $("#openGal").on("click", function () {
                openGallery(userID);
            });


            $("#messagesLink").on("click", function () {
                openMessages($scope.activeUserID);
            });


            $("#newMessage").on("click", function () {
                openNewMessage($scope.activeUserID);
            });

            $("#sendB").on("click", function () {
                $scope.send();
            });

        });
    });

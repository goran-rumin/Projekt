/**
 * Created by Illona on 27.12.2014..
 */



var app = angular.module("Conversation",[]);

app.controller("convController", function($scope) {

var userID = location.search.split('userId=')[1];


    $.ajax({
        url: root+"user/active/",
        type : "POST",
        data : {}
    }).success(function(msg) {
        var json = JSON.parse(msg);
        $scope.activeUserID = parseInt(json.data.id);
        $scope.$apply;
        if ($scope.activeUser == -1) $scope.activeUser=location.search.split('userId=')[1];

        function readUserData() {
            //dohvaca podatke za korisnika s kim se pregledava razgovor
            $.ajax({
                url: root + "user/info/index.php",
                type: "POST",
                data: {
                    userId: $scope.userID
                }
            }).success(function (msg) {
                $scope.userData = JSON.parse(msg);
                $scope.$apply();

            })
        }
        readUserData();


    //dohvaca poruke
    function readConversation(){
        $.ajax({
            url: root + "messages/conversation/index.php",
            type: "POST",
            data: {
                userId1: $scope.activeUserID,
                userId2: $scope.userID
            }
        }).success(function(msg){
            $scope.conversationData = JSON.parse(msg);
            $scope.$apply();
            console.log($scope.conversationData.data);

        })
    }
    readConversation();



    function getInfo(userIdfunction){
        $.ajax({
            url: root + "user/info/index.php",
            type: "POST",
            data: {
                userId: userIdfunction
            }
        }).success(function (msg) {
            $scope.userInfo = JSON.parse(msg);
            $scope.$apply();

        })

    }

	
    $scope.send= function(){
        var message = $("#message").val();
		if(message==""){
			$("#emptyMessage").html("You have to write something!");
			setTimeout(function () {
                    $("#emptyMessage").html("");
                }, 2000);
			}
		else{
			$.ajax({
                url: "../../messages/send/index.php",
                type: "POST",
                data: {
                    userid1: $scope.activeUserID,
                    userid2: $scope.userID,
                    message: message
                }
            }).success(function (msg) {
                    setTimeout(function () {
                        $scope.readConversation();
                    }, 2000);
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

	});
});






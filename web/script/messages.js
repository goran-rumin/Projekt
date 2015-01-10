/**
 * Created by Illona on 12.12.2014..
 */

var app = angular.module("Messages",[]);

app.controller("messagesController", function($scope) {
    var userID2;

    $.ajax({
        url: root+"user/active/",
        type : "POST",
        data : {}
    }).success(function(msg) {
        var json = JSON.parse(msg);
        $scope.activeUserID = parseInt(json.data.id);
        $scope.$apply();
        if ($scope.activeUserID== -1) $scope.activeUser=location.search.split('userId=')[1];



    //dohvaca stare razgovore
    $scope.readMessages = function() {
        console.log($scope.activeUserID);
        $.ajax({
            url: root + "messages/inbox/index.php",
            type: "POST",
            data: {
                userId: $scope.activeUserID
            }
        }).success(function (msg) {
            $scope.messagesData = JSON.parse(msg);
            $scope.$apply();
            console.log($scope.messagesData.data);

        })
    }

        $scope.readMessages();


        $scope.getInfo = function(userID){
            $.ajax({
                url: root + "user/info/index.php",
                type: "POST",
                data: {
                    userId: userID
                }
            }).success(function (msg) {
                $scope.userInfo = JSON.parse(msg);
                $scope.$apply();
                console.log($scope.userInfo.data);

            })
        }
        $scope.openConversation=function(userID){
            window.location = root + "web/Conversation/?userId=" + userID;

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
     // $("#name").on("click", function () {
      //    console.log($scope.userID2);
      //     openConversation($scope.userID2);
      // });



	});
});



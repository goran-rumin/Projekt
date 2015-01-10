/**
 * Created by Ante on 6.12.2014..
 */
'use strict';

var gallery = angular.module('gallery', ['ngAnimate', 'ngTouch']);
gallery.controller('albumCtrl', ['$scope', '$http', '$templateCache', function ($scope, $http, $templateCache) {
    $scope.images = [];

    var userID = location.search.split('userId=')[1];
	$("#noPics").hide();

    $.ajax({
        url: root+"user/active/",
        type : "POST",
        data : {}
    }).success(function(msg) {
        var json = JSON.parse(msg);
        $scope.activeUserID = parseInt(json.data.id);
        if ($scope.activeUser == -1) $scope.activeUser=location.search.split('userId=')[1];
        if(userID != $scope.activeUserID) {
		    $("#albumAdder").hide();
            $("#upload").hide();
		}
		console.log(userID);
    });
        $scope.add = function () {

            $http({
                method: 'POST',
                url: root+'user/galleries/index.php',
                data: $.param({'userId': userID}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                cache: $templateCache
            })
                .success(function (response) {
                    console.log(response);
                    $scope.albumList = response.data;
                    console.log($scope.albumList);
					if($scope.albumList == undefined) {
                        $("#noPics").show();
                        $("#noPics").html("No albums available!")
                    }
                    $scope.myAlbum = "";
                })
        };

        $scope.showImages = function (albumId) {
            var albumIdn = albumId;
            console.log(albumIdn);
            console.log("albumiD " + albumId);
            $http({
                method: 'POST',
                url: root + 'photos/gallery/index.php',
                data: $.param({'albumId': albumIdn}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                cache: $templateCache
            })
                .success(function (response) {
					$scope._Index = 0;
                    $scope.images = response.data;
                    console.log("Sad slike:");
                    console.log($scope.images);
					if($scope.images.length <= 0 &&  albumId != undefined)
                    {
                        $("#noPics").show();
                        $("#noPics").html("No images in this album, please choose another one!")
                    }
                    else $("#noPics").hide(); 
                })
        };

        $scope.addNewAlbum = function (name) {
            var ime = name;
            $http({
                method: 'POST',
                url: root + 'user/addGallery/inbox.php',
                data: $.param({'userId': userID, 'name': ime}),
                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                cache: $templateCache
            })
                .success(function (response) {
                    $scope.albumList.push(name);
                    console.log(ime);
                    console.log(name);
                    console.log(response);
					window.location.reload();
                })
        };

        $scope._Index = 0;

        $scope.isActive = function (index) {
            return $scope._Index === index;
        };

        $scope.showPrev = function () {
            $scope._Index = ($scope._Index > 0) ? --$scope._Index : $scope.images.length - 1;
        };

        $scope.showNext = function () {
            $scope._Index = ($scope._Index < $scope.images.length - 1) ? ++$scope._Index : 0;
        };

        $scope.showPhoto = function (index) {
            $scope._Index = index;
        };

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



}]);
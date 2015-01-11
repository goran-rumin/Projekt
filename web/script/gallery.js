/**
 * Created by Ante on 6.12.2014..
 */
'use strict';

var gallery = angular.module('gallery', []);
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

        if(userID != $scope.activeUserID) {
            $("#albumAdder").hide();
            $("#upload").hide();
        }

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

    $scope.pictureURL = "";

    $scope.postPicture = function (event) {

        if ($(event.target).text() == "Upload new photo"){
            $(event.target).val("cancel");
            $(event.target).text("Cancel upload");
            var x = document.createElement("input");
            var wrap = document.createElement("div");
            var form = document.createElement("form");
            var select = document.createElement("select");
            var submit = document.createElement("input");

            $(wrap).addClass("inputWrapper").appendTo($(event.target).parent());

            $(x).addClass("inputFile")
                .attr("type", "file")
                .appendTo($(form));

            $(select).addClass("selectBottom").appendTo($(form));
            var albums = $scope.albumList;
            Object.getOwnPropertyNames(albums).forEach(function (val) {
                if (val == "length") return false;
                var option = document.createElement("option");

                $(option).val(albums[val].name)
                    .val(albums[val].albumId)
                    .text(albums[val].name).appendTo($(select));

            });

            // Za pretvaranje u base64
            $(x).change(function(){
                if (this.files[0].type != "image/jpeg") {
                    $("#uploadMsg").html("Wrong file chosen, only supported .jpg photos. Please choose correct file.")
                    setTimeout(function () {
                        $("#uploadMsg").html("")
                    }, 4000);
                    $(event.target).parent().children(".inputWrapper").remove();
                    $(event.target).text("Upload new photo");

                    return false;
                } else if (this.files[0].size > 1048576) {
                    $("#uploadMsg").html("Image you have selected is too big. Upload limit is 1 MB.");
                    setTimeout(function () {
                        $("#uploadMsg").html("");
                    }, 4000);
                    $(event.target).parent().children(".inputWrapper").remove();
                    $(event.target).text("Upload new photo");
                    return false;
                }
                readImage( this );
            });
            // base64 kraj

            $(form).attr("method", "post").attr("enctype", "multipart/form-data")
                .attr("target","upload_target").attr("id", "sendPhoto").appendTo($(wrap));

            $(submit).attr("type", "submit").val("Upload").addClass("btn btn-default")
                .on("click", function(){
                    if ($scope.pictureURL == "") {
                        $("#uploadMsg").html("Choose photo!");
                        return false;
                    }

                    $("#uploadMsg").html("Your image is uploading.");
                    var album = $(".selectBottom :selected").val();

                    $(event.target).hide();
                    $.ajax({
                        url: root + "photos/upload/index.php",
                        type: "POST",
                        data: {
                            userId: userID,
                            url: $scope.pictureURL,
                            message: "",
                            albumId: album
                        }
                    }).success(function (msg) {
                        console.log(msg);
                        var response = JSON.parse(msg);
                        if (response.error.length ==0) {
                            $("#uploadMsg").html("Upload finished!");
                            setTimeout(function () {
                                $("#uploadMsg").html("")
                            }, 2000);
                            $scope.pictureURL="";
                            $(event.target).text("Upload new photo").show();
                            $(event.target).parent().children(".inputWrapper").remove();
                            $scope.showImages(album);
                        } else {
                            $("#uploadMsg").html("Upload failed.");
                            setTimeout(function () {
                                $("#uploadMsg").html("")
                            }, 2000);
                            $(event.target).show();
                        }

                    })
                }).appendTo($(form));

        } else {
            $(event.target).parent().children(".inputWrapper").remove();
            $(event.target).text("Upload new photo");
            $(event.target).val("post");
            $scope.pictureURL = "";
        }


    };

    // Jquery funkcija za upload slika
    function readImage(input) {
        if ( input.files && input.files[0] ) {
            var FR= new FileReader();
            FR.onload = function(e) {
                $scope.pictureURL = e.target.result.substr(e.target.result.indexOf(",")+1);

            };
            FR.readAsDataURL( input.files[0] );
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



}]);
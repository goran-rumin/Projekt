/**
 * Created by Ante on 6.12.2014..
 */
'use strict';

var gallery = angular.module('gallery', []);
gallery.controller('albumCtrl', ['$scope', '$http', '$templateCache', function ($scope, $http, $templateCache) {
    $(".hide").hide();
    $("#bottomDisc").hide();
    $scope.profilePicture = "../images/profile_default_big.png";
    $scope.images = [];
    $scope.unlike = 0;
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
            $(".footer").hide();
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
                $scope.albumList = response.data;
                if($scope.albumList == undefined) {
                    $("#noPics").show();
                    $("#noPics").html("No albums available!")
                }
                $scope.myAlbum = "";
            })
    };

    $scope.showImages = function (albumId) {
        var albumIdn = albumId;
        var userID = location.search.split('userId=')[1];
        $http({
            method: 'POST',
            url: root + 'photos/gallery/index.php',
            data: $.param({'albumId': albumIdn}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            cache: $templateCache
        })
            .success(function (response) {
                $(".hide").show();
                $scope._Index = 0;
                $scope.izracun = 2000;
                $scope.images = response.data;



                $.ajax({
                    url: root + "friends/status/index.php",
                    type: "POST",
                    data: {
                        userId1: userID,
                        userId2: $scope.activeUserID
                    }
                }).success(function(msg){
                    var status = JSON.parse(msg);
                    if(status.data.statusNumber != 1 && userID != $scope.activeUserID) {
                        $("#lajkaj").hide();
                        $("#likeComment").hide();
                        $("#comments").hide();
                    }
                });


                if($scope.images.length <= 0 &&  albumId != undefined)
                {
                    $("#noPics").show();
                    $("#noPics").html("No images in this album, please choose another one!")
                }
                //ubaciti .............................................................
                else {
                    $("#noPics").hide();
                    $("#lajkali").hide();
                    $("#komentirali").hide();
                    getComments($scope.images[$scope._Index].postId);
                    getLikes($scope.images[$scope._Index].postId);
                }
            })
    };

    $scope.addNewAlbum = function (name) {
        var ime = name;
        $http({
            method: 'POST',
            url: root + 'user/addGallery/index.php',
            data: $.param({'userId': userID, 'name': ime}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            cache: $templateCache
        })
            .success(function (response) {
                $scope.albumList.push(name);
                window.location.reload();
            })
    };

    $scope._Index = 0;
    $scope.izracun = 2000;


    $scope.isActive = function (index) {
        if($scope.imageId == undefined) $scope.imageId = $scope.images[0].postId;

        var pic = document.getElementById("pic"+index);
        var width = pic.width;
        var height = pic.height;
        if((height/2 + 165)<$scope.izracun) $scope.izracun = height/2 + 165;
        $(".arrow").css({
            "top": $scope.izracun + "px"
        });

        return $scope._Index === index;
    };

    //ubaciti .............................................................

    $scope.showPrev = function () {
        $scope._Index = ($scope._Index > 0) ? --$scope._Index : $scope.images.length - 1;
        $scope.imageId = $scope.images[$scope._Index].postId;
        getComments($scope.images[$scope._Index].postId);
        getLikes($scope.images[$scope._Index].postId);
    };

    $scope.showNext = function () {
        $scope._Index = ($scope._Index < $scope.images.length - 1) ? ++$scope._Index : 0;
        $scope.imageId = $scope.images[$scope._Index].postId;
        $("#lajkali").hide();
        getComments($scope.images[$scope._Index].postId);
        getLikes($scope.images[$scope._Index].postId);
    };

    $scope.showPhoto = function (index) {
        $scope._Index = index;
        $scope.imageId = $scope.images[$scope._Index].postId;
        $("#lajkali").hide();
        getComments($scope.images[$scope._Index].postId);
        getLikes($scope.images[$scope._Index].postId);
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

    //ubaciti .............................................................
    function readUserData() {
        $.ajax ({
            url: root + "user/info/index.php",
            type: "POST",
            data: {
                userId: userID
            }
        }).success(function (msg) {
            $scope.userData = JSON.parse(msg);
            if ($scope.userData.data.picture == null) {
                $scope.userData.data.picture = $scope.profilePicture;
            }
            $scope.$apply();

        })
    }

    function getComments(postId) {
        $scope.allComments = [];
        $.ajax({
            url: root + "post/getComments/index.php",
            type: "POST",
            data: {
                postId: postId,
                userId: $scope.activeUserID
            }
        }).success(function (msg) {
            $scope.comments = JSON.parse(msg);
            $scope.commentsAlt = $scope.comments.data;
            $scope.howManyComm = 0;
            for (var o in $scope.comments.data)
            {
                $scope.howManyComm++;
                $scope.test = $scope.commentsAlt[o];
                $scope.allComments.push($scope.test);
            }
            $("#kom").html("Show " + $scope.howManyComm + " comments");
            $scope.$apply();
        })
    }

    function getLikes(postId) {
        $.ajax({
            url: root + "post/getLikes/index.php",
            type: "POST",
            data: {
                postId: postId
            }
        }).success(function (msg) {
            $scope.likes = JSON.parse(msg);
            var likesAlt = $scope.likes.data;
            $scope.howManyLikes = 0;
            for (var o in $scope.likes.data)
            {
                $scope.howManyLikes++;

                if(likesAlt[o].userId == $scope.activeUserID) $("#lajkaj").html("Unlike");
            }
            $("#lajk").html($scope.howManyLikes + " Likes");
            if($scope.howManyLikes == 0) $("#lajkaj").html("Like");
            $scope.$apply();
        })
    }

    $("#lajkaj").click(function (){
        $.ajax({
            url: root + "post/like/index.php",
            type: "POST",
            data: {
                postId: $scope.images[$scope._Index].postId,
                userId: $scope.activeUserID
            },
            cache: false
        }).success(function(msg){
            $scope.likeMsg = JSON.parse(msg);

            if($scope.likeMsg.data.action == "like") {
                $scope.howManyLikes += 1;

                $("#lajk").html($scope.howManyLikes + " Likes");
                $("#lajkaj").html("Unlike");
            }
            else {
                $scope.howManyLikes -= 1;

                $("#lajk").html($scope.howManyLikes + " Likes");
                $("#lajkaj").html("Like");
            }

            });
    });

    $scope.checkLikes = function(postId, index) {
        $.ajax({
            url: root + "post/getLikes/index.php",
            type: "POST",
            data: {
                postId: postId
            }
        }).success(function(comm) {
            $scope.commentLikes = JSON.parse(comm);
            var commentsAlt = $scope.commentLikes.data;

            for (var o in $scope.commentLikes.data) {
                if(commentsAlt[o].userId == $scope.activeUserID)
                {
                    $(".like"+index).html("Unlike");
                    $scope.unlike = 1;
                }
                else $scope.unlike = 0;
            }

        });
    };

    $scope.likeCom = function(postId, br, index) {
        $.ajax({
            url: root + "post/getLikes/index.php",
            type: "POST",
            data: {
                postId: postId
            }
        }).success(function(por) {
            $scope.likedCom = JSON.parse(por);
            var likedComAlt = $scope.likedCom.data;

            $scope.howManyCom = 0;

            for (var o in $scope.likedCom.data)
            {
                $scope.howManyCom += 1;

                if(likedComAlt[o].userId == $scope.activeUserID && !$("#likeComment").is(":hidden")) $(".like"+index).html("Unlike");
            }

        });
        setTimeout(function () {
        $.ajax({
            url: root + "post/like/index.php",
            type: "POST",
            data: {
                postId: postId,
                userId: $scope.activeUserID
            },
            cache: false
        }).success(function(msg){
            $scope.likeComm = JSON.parse(msg);
            var pomBr = br;
            $scope.pomVar = $scope.likeComm.data.action;


            if(!$("#likeComment").is(":hidden")) {
                if(br == 0) $(".like"+index).html("Like");
                if($scope.pomVar == "like") {
                    $scope.howManyCom += 1;
                    $(".msgL"+index).html($scope.howManyCom +" Likes");
                    $(".like"+index).html("Unlike");
                }
                else if ($scope.pomVar == "unlike") {
                    if($scope.howManyCom>0) $scope.howManyCom -= 1;
                    $(".msgL"+index).html($scope.howManyCom +" Likes");
                    $(".like"+index).html("Like");
                }
            }
        });
        }, 200);
    };

    $("#lajk").on('click', function(){
        var rj = "People who liked this: ";
        var likesAlt = $scope.likes.data;
        var pom = $scope.howManyLikes;
        if($scope.howManyLikes == 0) {
            $("#lajkali").html("No one liked this yet.");
        }
        if($scope.howManyLikes > 0) {
            for (var o in likesAlt) {
                rj += " " + likesAlt[o].name + " " + likesAlt[o].lastname;
                if(pom>1) rj += ",";
                pom--;
            }
            $("#lajkali").html(rj);
        }
        if( $("#lajkali").is(":hidden") ) {
            $("#lajkali").show();
        }
        else
        {
            $("#lajkali").hide();
        }
    });

    $("#kom").on('click', function(){
        var pom = $scope.howManyComm;
        if($scope.howManyComm == 0) {
            $("#komentirali").html("No comments yet ...")
        }
        /*else {
         $("#komentirali").html($scope.test.message);
         }*/
        if( $("#komentirali").is(":hidden") ) {
            $("#komentirali").show();
            $(".delete").show();
        }
        else
        {
            $("#komentirali").hide();
            $(".delete").hide();
        }
    });

    $scope.addNewComment = function(message){
        $(".hide").show();
        $.ajax({
            url: root + "post/comment/index.php",
            type: "POST",
            data: {
                postId: $scope.imageId,
                userId: $scope.activeUserID,
                message: message
            }
        }).success(function(msg) {
            $scope.howManyComm++;
            $("#kom").html("Show " + $scope.howManyComm + " comments");
            $("#bottomDisc").show();
            $("#bottomDisc").html("Comment added successfully!");
            setTimeout(function () {
                $("#bottomDisc").html("");
                $("#msg").val("");
                $("#bottomDisc").hide();
                getComments($scope.imageId);
                getLikes($scope.imageId);
            }, 2000);
        });
    };

    $scope.seeProfile = function(userId) {
        window.location = rootRed+"wall/?userId=" + userId;
    }


}]);


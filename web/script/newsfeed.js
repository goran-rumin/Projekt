/**
 * Created by Iva
 */


var app = angular.module("NewsFeed", []);

app.controller("newsController", function ($scope) {
    $scope.profilePicture = "http://www.wiganlatics.co.uk/images/common/bg_player_profile_default_big.png";
    $scope.postOffset = 0;
    $scope.postsPicture = "http://www.photosnewhd.com/media/images/picture-wallpaper.jpg";

    var userID = location.search.split('userId=')[1];

    $.ajax({
        url: root + "user/active/",
        type: "POST",
        data: {}
    }).success(function (msg) {
        var json = JSON.parse(msg);
        $scope.activeUser = parseInt(json.data.id);

        if ($scope.activeUser == -1) $scope.activeUser = location.search.split('userId=')[1];
        userID = $scope.activeUser;


        function readUserData() {
            //dohvaca podatke za trenutnog korisnika
            $.ajax({
                url: root + "user/info/index.php",
                type: "POST",
                data: {
                    userId: userID
                }
            }).success(function (msg) {
                $scope.userData = JSON.parse(msg);
                $scope.$apply();

            })
        }

        readUserData();

        $scope.loadPosts = function () {
            $scope.postOffset = $scope.postOffset + 1;
            $.ajax({
                url: root + "post/newsfeed/index.php",
                type: "POST",
                data: {
                    userId: userID,
                    offset: $scope.postOffset
                }
            }).success(function (msg) {
                $scope.postsData = JSON.parse(msg);
                $scope.$apply();

                var postData = $scope.postsData.data;
                if (postData.length == 0) {
                    msgCont = document.createElement("div");

                    $(msgCont).addClass("post")
                        .text("No new posts to show.")
                        .appendTo(".postsContainer");
                    $("#load").hide();
                } else {

                    Object.getOwnPropertyNames(postData).reverse().forEach(function (val) {

                        post = document.createElement("div");
                        userInfoPost = document.createElement("div");
                        content = document.createElement("div");
                        textPostContainer = document.createElement("div");
                        pictureContainer = document.createElement("div");
                        likeComments = document.createElement("div");
                        like = document.createElement("div");
                        commentsContainer = document.createElement("div");

                        profilePic = document.createElement("img");
                        timestampContainer = document.createElement("div");
                        infoPoster = document.createElement("span");
                        timestamp = document.createElement("span");

                        $(profilePic).addClass("profilePic")
                            .attr("src", function () {
                                if (postData[val].senderPicture == null) {
                                    return $scope.profilePicture;
                                } else return postData[val].senderPicture;
                            })
                            .on("click", function () {
                                openWall(postData[val].senderId);
                            })
                            .appendTo($(userInfoPost));

                        $(infoPoster).addClass("infoPoster")
                            .text($scope.posterText(postData[val]))
                            .on("click", function () {
                                openWall(postData[val].senderId);
                            })
                            .appendTo($(userInfoPost));

                        $(timestamp).addClass("timestamp")
                            .text("Posted on " + postData[val].timestamp)
                            .appendTo($(timestampContainer));
                        $(timestampContainer).addClass("timestampContainer")
                            .appendTo($(userInfoPost));

                        $(userInfoPost).addClass("userInfoPost").appendTo($(post));

                        textPost = document.createElement("span");
                        $(textPost).addClass("textPost")
                            .text(postData[val].text)
                            .appendTo($(textPostContainer));

                        $(textPostContainer).addClass("textPostContainer")
                            .appendTo($(content));

                        postPicture = document.createElement("img");
                        var condition = true;
                        if (postData[val].url == "") condition = false;
                        $(postPicture).addClass("postPicture")
                            .show(postData[val].url != "")
                            .attr("src", postData[val].url)
                            .on("click", function () {
                                $scope.enlarge(postData[val]);
                            })
                            .appendTo($(pictureContainer));
                        $(pictureContainer).addClass("pictureContainer")
                            .toggle(condition)
                            .appendTo($(content));


                        likeText1 = document.createElement("span");
                        likeText2 = document.createElement("span");

                        $(likeText1).addClass("likeText")
                            .text($scope.isLiked(postData[val]) + "  ")
                            .on("click", function () {
                                $scope.likePost(postData[val].postId, event);
                            })
                            .appendTo($(like));

                        $(likeText2).addClass("likeText")
                            .text(postData[val].likesNumber + " likes")
                            .on("click", function () {
                                $scope.showLikes(event, postData[val].postId)
                            })
                            .appendTo($(like));

                        $(like).addClass("like")
                            .appendTo($(likeComments));

                        commentsShow = document.createElement("span");

                        $(commentsShow).addClass("commentsShow")
                            .text("Show comments")
                            .on("click", function () {
                                $scope.comments(event, postData[val]);
                            })
                            .appendTo($(commentsContainer));

                        $(commentsContainer).addClass("commentsContainer")
                            .appendTo($(likeComments));

                        $(likeComments).addClass("likeComments")
                            .appendTo($(content));

                        $(content).addClass("content")
                            .appendTo($(post));

                        $(post).addClass("post").appendTo(".postsContainer");
                    })
                }
            })
        }
        $scope.loadPosts();


        $scope.pictureURL = "";
        //TODO


        $scope.postPicture = function (event) {

            if ($(event.target).attr("value") == "post"){
                $(event.target).val("cancel");
                $(event.target).text("Cancel upload");
                x = document.createElement("input");
                wrap = document.createElement("div");
                form = document.createElement("form");
                submit = document.createElement("input");

                $(wrap).addClass("inputWrapper").appendTo($(event.target).parent());

                $(x).addClass("inputFile")
                    .attr("type", "file")
                    .appendTo($(form));


                // Za pretvaranje u base64
                $(x).change(function(){
                    readImage( this );
                });
                // base64 kraj

                $(form).attr("method", "post").attr("enctype", "multipart/form-data")
                    .attr("target","upload_target").attr("id", "sendPhoto").appendTo($(wrap));

                $(submit).attr("type", "submit").val("Upload").attr("id", "submit").appendTo($(wrap));

            } else {
                $(event.target).parent().children(".inputWrapper").remove();
                $(event.target).text("Post picture");
                $(event.target).val("post");
            }


        };


        $scope.newPost = function () {
            var textInput = $("#newPostData").val();
            var url="";
            if (textInput == "" && $scope.pictureURL == "") {
                $("#newPostMsg").html("Please choose photo or input your message.")
                setTimeout(function () {
                    $("#newPostMsg").html("")
                }, 2000);
            } else {
                if ($scope.pictureURL != "") {
                    console.log($scope.pictureURL);
                    $.ajax({
                        url: root + "photos/base64",
                        type: "POST",
                        data:  {
                            photo: $scope.pictureURL
                        },
                        processData: false,
                        contentType: false,
                        cache: false


                    }).success(function (msg) {
                        console.log(msg);
                        var mssg = JSON.parse(msg);
                        console.log("parsirano " +mssg);
                    })
                } else {
                    $.ajax({
                        url: root + "post/publish/index.php",
                        type: "POST",
                        data: {
                            sender: userID,
                            recipient: userID,
                            message: textInput,
                            url: url
                        }
                    }).success(function (msg) {
                        $("#newPostForm").children('input').val('');
                        $("#newPostMsg").html("Your message was posted!");
                        setTimeout(function () {
                            $("#newPostMsg").html("");
                            $("#postsContainer").children(".post").remove();
                            $scope.postOffset = 0;
                            $scope.loadPosts();
                        }, 2000);
                    })
                }
            }
        }

        $scope.posterText = function (object) {
            var posterName = object.senderName;
            var posterLastname = object.senderLastname;
            var text;
            if (object.senderUsername == object.recipientUsername) {
                text = posterName + " " + posterLastname;
            }
            else {
                text = posterName + " " + posterLastname + " posted on " +
                object.recipientName + " " + object.recipientLastname + "'s wall";
            }
            return text;
        }

        $scope.enlarge = function (object) {
            var pictureURL = object.url;
            //gdje se spremaju slike??
            var url = pictureURL;
            window.open(url);

        }

        $scope.likePost = function (postID, event) {

            $.ajax({
                url: root + "post/like/index.php",
                type: "POST",
                data: {
                    postId: postID,
                    userId: userID

                },
                cache: false
            }).success(function (msg) {
                $scope.likemsg = JSON.parse(msg);
                $scope.$apply();
                console.log($scope.likemsg.data);
                if ($scope.likemsg.data.action == "like") {
                    $(event.target).text("Unlike ");
                } else $(event.target).text("Like ");
            })
        }

        $scope.isLiked = function (element) {
            if (element.liked) {
                return "Unlike";
            } else  return "Like";
        }

        $scope.showLikes = function (event, postID) {

            console.log(event);
            var hide = false;
            if ($(event.target).parent().attr("class") == "like"){
                if ($(event.target).parent().parent()
                        .children(".commentsContainer")
                        .has(".likesShow").length > 0) {
                    $(event.target).parent().parent()
                        .children(".commentsContainer")
                        .children(".likesShow").remove();
                    hide = true;
                }
            } else {
                if ($(event.target).parent().parent().parent()
                        .has(".likesShow").length > 0) {
                    $(event.target).parent().parent().parent().children(".likesShow").remove();
                    hide = true;
                }
            }
            if (!hide) {

                $.ajax({
                    url: root + "post/getLikes/index.php",
                    type: "POST",
                    data: {
                        postId: postID

                    }
                }).success(function (msg) {
                    $scope.likedList = JSON.parse(msg);
                    $scope.$apply();

                    d = document.createElement("div");

                    if ($scope.likedList.data.length == 0) {

                        $(d).addClass("likesShow")
                            .text("No one liked this yet.");
                        if ($(event.target).parent().attr("class") == "like") {

                            $(d).insertAfter($(event.target).parent().parent().children(".commentsContainer").children(".commentsShow"));
                        } else {
                            $(d).addClass("likesShow")
                                .text("No one liked this yet.")
                                .insertAfter($(event.target).parent().parent());
                        }
                    } else {
                        var like = $scope.likedList.data;
                        var arrayNames = [];
                        Object.getOwnPropertyNames(like).forEach(function (val) {
                            var name = " " + like[val].name + " " + like[val].lastname;
                            arrayNames.push(name);
                        })
                        console.log(arrayNames);
                        $(d).addClass("likesShow")
                            .text("People that liked this: " + arrayNames);
                        if ($(event.target).parent().attr("class") == "like") {
                            $(d).insertAfter($(event.target).parent().parent()
                                .children(".commentsContainer").children(".commentsShow"));
                        } else {
                            $(d).addClass("likesShow")
                                .text("People that liked this: " + arrayNames)
                                .insertAfter($(event.target).parent().parent());
                        }
                    }

                })
            }
        }

        $scope.comments = function (event, object) {

            if ($(event.target).parent().children("div").length > 0 &&
                $(event.target).parent().has(".likesShow").length == 0 ||
                $(event.target).parent().children("div").length > 1) {
                $scope.hideComments(event, object);

            } else if ($(event.target).parent().children("div").length == 1 &&
                $(event.target).parent().has(".likesShow").length == 1) {
                $scope.showComments(event, object);
            } else {
                $scope.showComments(event, object);
            }
        }

        $scope.showComments = function (event, object) {
            var postID = object.postId;
            var contentShowed = false;

            if (!contentShowed) {
                $.ajax({
                    url: root + "post/getComments/index.php",
                    type: "POST",
                    data: {
                        postId: postID,
                        userId: userID
                    }
                }).success(function (msg) {
                    $scope.postCommentsData = JSON.parse(msg);
                    $scope.$apply();
                    var comments = $scope.postCommentsData.data;

                    if (comments.length == 0) {
                        d = document.createElement("div");
                        $(d).addClass("commentsError")
                            .text("No comments yet.")
                            .appendTo($(event.target).parent());
                    } else {
                        Object.getOwnPropertyNames(comments).forEach(function (val) {

                            d = document.createElement("div");
                            container = document.createElement("div");
                            poster = document.createElement("div");
                            posterPic = document.createElement("img");
                            mssg = document.createElement("div");
                            comment = document.createElement("div");
                            likeComments = document.createElement("div");


                            $(posterPic).addClass("commentPosterPic")
                                .attr("src", $scope.profilePicture)
                                .appendTo($(container));

                            $(poster).addClass("cInfoPoster")
                                .text(comments[val].name + " " + comments[val].lastname
                                + " on " + comments[val].timestamp)
                                .on("click", function () {
                                    openWall(comments[val].userId);
                                })
                                .appendTo($(container));

                            like1 = document.createElement("div");
                            likeText1 = document.createElement("span");
                            likeText2 = document.createElement("span");

                            $(likeText1).addClass("likeText")
                                .text($scope.isLiked(comments[val]) + "  ")
                                .on("click", function (event2) {
                                    $scope.likePost(comments[val].id, event2);
                                })
                                .appendTo($(like1));

                            $(likeText2).addClass("likeText")
                                .text(comments[val].likesNumber + " likes")
                                .on("click", function (event2) {
                                    $scope.showLikes(event2, comments[val].id);
                                })
                                .appendTo($(like1));

                            $(container).addClass("commentPoster").appendTo($(d));

                            $(mssg).addClass("commentText")
                                .text(comments[val].message)
                                .appendTo($(d));

                            $(like1).addClass("like1")
                                .appendTo($(likeComments));
                            $(likeComments).addClass("likeComments").appendTo($(d));

                            $(d).addClass("comments").appendTo($(event.target).parent());

                        });
                    }
                }).success(function () {
                    newComm = document.createElement("div");
                    input = document.createElement("input");
                    submit = document.createElement("button");

                    $(input).addClass("commentInput")
                        .attr("type", "text")
                        .appendTo($(newComm));

                    $(submit).addClass("buttonComment")
                        .text("Post")
                        .on("click", function () {
                            $scope.postComment($(input).val(), postID, event, object);
                        })

                        .appendTo($(newComm));

                    $(newComm).addClass("comments")
                        .appendTo($(event.target).parent());

                });
                contentShowed = true;
            }
            if (contentShowed) {
                $(event.target).text("Hide comments");
            }


        };


        $scope.hideComments = function (event) {
            $(event.target).parent().children(".comments, .commentsError").remove();
            $(event.target).text("Show comments");
        }

        $scope.postComment = function (text, postID, event, object) {

            $.ajax({
                url: root + "post/comment/index.php",
                type: "POST",
                data: {
                    postId: postID,
                    userId: userID,
                    message: text

                }
            }).success(function (msg) {

                $scope.hideComments(event, object);
                $scope.showComments(event, object);

            })
        }

        $scope.logout = function () {
            $.ajax({
                url: root + "user/logout/index.php"
            }).success(function () {
                window.location.replace(root + "web/login/");
            })
        }

        $("#newsfeedLink").on("click", function () {
            openNewsfeed($scope.activeUser);
        });

        $("#userProfilePicture").on("click", function () {
            openWall($scope.activeUser);
        });

        $("#wallLink").on("click", function () {
            openWall($scope.activeUser);
        });

        $("#galleryLink").on("click", function () {
            openGallery($scope.activeUser);
        });

        $("#messagesLink").on("click", function () {
            openMessages($scope.activeUser);
        });


    })

    // Jquery funkcija za upload slika
    function readImage(input) {
        if ( input.files && input.files[0] ) {
            var FR= new FileReader();
            FR.onload = function(e) {
                $scope.pictureUrl = e.target.result.substr(e.target.result.indexOf(",")+1);
                console.log($scope.pictureUrl);
            };
            FR.readAsDataURL( input.files[0] );
        }
    }
})






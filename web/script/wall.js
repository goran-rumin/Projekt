/**
 * Created by Iva
 */


var app = angular.module("Wall",[]);

app.controller("wallController", function($scope) {
    $scope.profilePicture = "../images/profile_default_big.png";
    $scope.postOffset = 0;


    var userID = location.search.split('userId=')[1];

    $.ajax({
        url: root+"user/active/",
        type : "POST",
        data : {}
    }).success(function(msg) {
        var json = JSON.parse(msg);
        $scope.activeUserID = parseInt(json.data.id);
        $scope.$apply();


        function readUserData() {
            //dohvaca podatke za korisnika ciji wall se gleda
            $.ajax({
                url: root + "user/info/index.php",
                type: "POST",
                data: {
                    userId: userID
                }
            }).success(function (msg) {
                $scope.userData = JSON.parse(msg);
                if ($scope.userData.data.picture != null) {
                    $scope.profilePicture = $scope.userData.data.picture;
                }
                $scope.$apply();

            })
        }
        readUserData();


        if (userID != $scope.activeUserID) {

            $("#postPicButton").hide(); //ne moze se postati slika na tudji zid


            $.ajax({
                url: root + "friends/status/index.php",
                type: "POST",
                data: {
                    userId1: userID,
                    userId2: $scope.activeUserID
                }
            }).success(function (msg) {
                var status = JSON.parse(msg);
                if (status.data.statusNumber != 1) {
                    $(".userNewPost").hide();
                }
            })
        }

        $scope.loadPosts = function () {
            $scope.postOffset = $scope.postOffset + 1;

            $.ajax({
                url: root + "post/wall/index.php",
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
                        .appendTo("#postsContainer");
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
                        profilePicCont = document.createElement("div");
                        profilePic = document.createElement("img");
                        timestampContainer = document.createElement("div");
                        infoPoster = document.createElement("span");
                        timestamp = document.createElement("span");

                        $(profilePicCont).addClass("profilePicCont").appendTo($(userInfoPost));

                        $(profilePic).addClass("profilePic")
                            .attr("src", function () {
                                if (postData[val].senderPicture == null) {
                                    return $scope.profilePicture;
                                } else return postData[val].senderPicture;
                            })
                            .on("click", function () {
                                openWall(postData[val].senderId);
                            })
                            .appendTo($(profilePicCont));

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

                        likePic = document.createElement("img");
                        likeText1 = document.createElement("span");
                        likeText2 = document.createElement("span");
                        likeText3 = document.createElement("span");

                        $(likeText1).addClass("likeText")
                            .text($scope.isLiked(postData[val]) + "  ")
                            .on("click", function () {
                                $scope.likePost(postData[val].postId, event);
                            })
                            .appendTo($(like));

                        $(likeText2).addClass("likeText")
                            .text(postData[val].likesNumber)
                            .on("click", function () {
                                $scope.showLikes(event, postData[val].postId)
                            })
                            .appendTo($(like));

                        $(likeText3).addClass("likeText").text(" likes")
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

                        $(post).addClass("post").appendTo("#postsContainer")
                            .insertBefore(".loadMore");
                    })
                }
            })
        }
        $scope.loadPosts();

        $scope.pictureURL = "";

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

                $(x).change(function(){
                    if (this.files[0].type != "image/jpeg") {
                        $("#newPostMsg").html("Wrong file chosen, only supported .jpg photos. Please choose correct file.")
                        setTimeout(function () {
                            $("#newPostMsg").html("")
                        }, 4000);
                        $(event.target).parent().children(".inputWrapper").remove();
                        $(event.target).text("Post picture");
                        $(event.target).val("post");

                        return false;
                    } else if (this.files[0].size > 1048576) {
                        $("#newPostMsg").html("Image you have selected is too big. Upload limit is 1 MB.");
                        setTimeout(function () {
                            $("#newPostMsg").html("");
                        }, 4000);
                        $(event.target).parent().children(".inputWrapper").remove();
                        $(event.target).text("Post picture");
                        $(event.target).val("post");
                        return false;
                    }
                    readImage( this );
                });

                $(form).attr("method", "post").attr("enctype", "multipart/form-data")
                    .attr("target","upload_target").attr("id", "sendPhoto").appendTo($(wrap));


            } else {
                $(event.target).parent().children(".inputWrapper").remove();
                $(event.target).text("Post picture");
                $(event.target).val("post");
                $scope.pictureURL="";
            }


        }

        $("#newPostData").keypress(function (event) {
            var key = event.which;
            if((event.keyCode || event.which) == 13)
            {
                $scope.newPost();
                return false;
            }
        });

        $scope.newPost = function () {
            var textInput = $("#newPostData").val();

            if (textInput == "" && $scope.pictureURL == "") {
                $("#newPostMsg").html("Please choose photo or input your message.")
                setTimeout(function () {
                    $("#newPostMsg").html("")
                }, 2000);
            } else if ($scope.pictureURL != ""){

                $("#newPostMsg").html("Your image is uploading.");
                $.ajax({
                    url: root + "photos/upload/index.php",
                    type: "POST",
                    data: {
                        userId: $scope.activeUserID,
                        message: textInput,
                        url: $scope.pictureURL
                    }
                }).success(function (msg) {

                    $("#newPostForm").children('textarea').val('');
                    $("#newPostMsg").html("Your message was posted!");
                    $("#postPicButton").parent().children(".inputWrapper").remove();
                    $("#postPicButton").text("Post picture");
                    $("#postPicButton").val("post");
                    setTimeout(function () {
                        $("#newPostMsg").html("");
                        $("#postsContainer").children(".post").remove();
                        $scope.postOffset = 0;
                        $scope.loadPosts();
                    }, 2000);

                    $scope.pictureURL="";
                    $scope.$apply();
                })
            } else {
                $.ajax({
                    url: root + "post/publish/index.php",
                    type: "POST",
                    data: {
                        sender: $scope.activeUserID,
                        recipient: userID,
                        message: textInput,
                        url: ""
                    }
                }).success(function (msg) {
                    $("#newPostForm").children('textarea').val('');
                    $("#newPostMsg").html("Your message was posted!");
                    setTimeout(function () {
                        $("#newPostMsg").html("");
                        $("#postsContainer").children(".post").remove();
                        $scope.postOffset = 0;
                        $scope.loadPosts();
                    }, 2000);
                    $scope.pictureURL="";
                    $scope.$apply();
                })
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
                var number = $(event.target).next().text();
                if ($scope.likemsg.data.action == "like") {
                    $(event.target).text("Unlike ");
                    number = parseInt(number)+1;
                    $(event.target).next().text(parseInt(number));

                } else {
                    $(event.target).text("Like ");
                    number = parseInt(number)-1;
                    $(event.target).next().text(parseInt(number));

                }
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
                        console.log($(event.target).parent().parent().children(".commentsContainer").children(".commenetsShow"));
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
                            timestampContainer = document.createElement("div");
                            posterPicCont = document.createElement("div");
                            poster = document.createElement("div");
                            posterPic = document.createElement("img");
                            mssg = document.createElement("div");
                            comment = document.createElement("div");
                            likeComments = document.createElement("div");


                            $(posterPicCont).addClass("commentPosterPicCont").appendTo($(container));

                            $(posterPic).addClass("commentPosterPic")
                                .attr("src", comments[val].picture)
                                .appendTo($(posterPicCont));

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
                            likeText3 = document.createElement("span");

                            $(likeText1).addClass("likeText")
                                .text($scope.isLiked(comments[val]) + "  ")
                                .on("click", function (event2) {
                                    $scope.likePost(comments[val].id, event2);
                                })
                                .appendTo($(like1));

                            $(likeText2).addClass("likeText")
                                .text(comments[val].likesNumber)
                                .on("click", function (event2) {
                                    $scope.showLikes(event2, comments[val].id);
                                })
                                .appendTo($(like1));

                            $(likeText3).addClass("likeText").text(" likes")
                                .on("click", function (event2) {
                                    $scope.showLikes(event2, comments[val].postId)
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
                        .keypress(function (event2) {

                            if((event2.keyCode || event2.which) == 13)
                            {
                                $scope.postComment($(input).val(), postID, event, object);
                                return false;
                            }
                        })
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
        };

        $scope.postComment = function (text, postID, event, object) {

            $.ajax({
                url: root + "post/comment/index.php",
                type: "POST",
                data: {
                    postId: postID,
                    userId: $scope.activeUserID,
                    message: text

                }
            }).success(function (msg) {
                $scope.$apply();
                $scope.hideComments(event, object);
                $scope.showComments(event, object);

            })
        };

        $('.tabs .tabLinks a').on('click', function (e) {
            var currentAttrValue = $(this).attr('href');
            $('.tabs ' + currentAttrValue).fadeIn(400).show().siblings().hide();

            $(this).parent('li').addClass('active').siblings().removeClass('active');

            e.preventDefault();
        });

        $("#editInfo button").on("click", function () {
            window.location.replace(rootRed + "editinfo/");
        });

        if (userID != $scope.activeUserID) {
            $("#editInfo").hide();
            $("#pending").hide();
        }


        $("#friends").on("click", function () {
            if ($("#friendsContainer").children("ul").children.length != 0) {
                $("#friendsContainer").children("ul").children("li").remove();
            }
            $.ajax({
                url: root + "search/index.php",
                type: "POST",
                data: {
                    userId: userID,
                    query: ""
                }
            }).success(function (msg) {
                console.log(msg);
                $scope.userFriendsData = JSON.parse(msg);
                $scope.$apply();

                var ufr = $scope.userFriendsData.data;
                $scope.listFriends(ufr);
            });

        })

        $("#searchQuery").keypress(function (event) {
                
                if((event.keyCode || event.which) == 13)
                {
                    $scope.friendsSearch();
                    return false;
                }
            });

        $scope.friendsSearch = function () {
            var textInput = $("#searchQuery").val();
            console.log(textInput);
            if (textInput == "") {
                $("#searchMsg").html("Please write what you're looking for.");
                setTimeout(function () {
                    $("#searchMsg").html("")
                }, 2000);
            } else {
                $.ajax({
                    url: root + "search/index.php",
                    type: "POST",
                    data: {

                        query: textInput
                    }
                }).success(function (msg) {
                    console.log(msg);
                    $("#searchFriends").children("input").val("");
                    $("#friendsContainer").children("ul").children("li").fadeOut(500);
                    $scope.userFriendsData = JSON.parse(msg);
                    $scope.$apply();

                    var ufr = $scope.userFriendsData.data;
                    $scope.listFriends(ufr);

                })
            }
        }

        $scope.listFriends = function (ufr) {

            if (ufr.length == 0) {
                d = document.createElement("div");
                $(d).addClass("commentsError")
                    .text("Nothing to show.")
                    .appendTo($("#friendsContainer"));
                setTimeout(function () {
                    $(".commentsError").html("")
                }, 2000);
            } else {
                Object.getOwnPropertyNames(ufr).forEach(function (val) {
                    if (val == "length") return false;
                    d = document.createElement("div");
                    var friendStat;
                    $.ajax({
                        url: root + "friends/status/index.php",
                        type: "POST",
                        data: {
                            userId1: userID,
                            userId2: ufr[val].id
                        }
                    }).success(function (msg) {
                        $scope.status = JSON.parse(msg);
                        $scope.$apply();

                        friendStat = $scope.status.data.statusNumber;
                        item = document.createElement("li");
                        infoAdd = document.createElement("div");
                        nameDiv = document.createElement("div");
                        button = document.createElement("button");
                        linkPic = document.createElement("img");
                        linkText = document.createElement("span");


                        $(linkPic).addClass("linkPicture")
                            .attr("src", function () {
                                if (ufr[val].picture == null) {
                                    return "../images/profile_default_big.png";
                                } else return ufr[val].picture;
                            })
                            .on("click", function () {
                                openWall(ufr[val].id);
                            })
                            .appendTo($(item));

                        $(infoAdd).addClass("infoAdd").appendTo($(item));

                        $(nameDiv).addClass("nameContainer").appendTo($(infoAdd));

                        $(linkText)
                            .on("click", function () {
                                openWall(ufr[val].id);
                            })
                            .text(ufr[val].name + " " + ufr[val].lastname)
                            .appendTo($(nameDiv));

                        if (userID == $scope.activeUserID) {
                            $(button).addClass("button")
                                .text(function () {
                                    if (friendStat == -1) {
                                        return "Add friend";
                                    } else if (friendStat == 1) {
                                        return "Delete friend";
                                    } else return "Pending request";
                                })
                                .on("click", function () {
                                    if (friendStat == -1) {
                                        $scope.addFriend(ufr[val].id, event);
                                    } else if (friendStat == 1) {
                                        $scope.deleteFriend(ufr[val].id, event);
                                    } else $scope.confirmFriend(ufr[val].id, event);

                                })
                                .appendTo($(infoAdd));
                        }

                        $(item).addClass("friendsList")
                            .appendTo($("#friendsContainer").children("ul"));

                    })
                })
            }
        }

        $scope.addFriend = function (id, event) {
            $.ajax({
                url: root + "friends/add/index.php",
                type: "POST",
                data: {
                    userId1: userID,
                    userId2: id
                }
            }).success(function (msg) {
                $scope.status = JSON.parse(msg);
                $scope.$apply();
                $(event.target).text("Pending request");
            })
        };

        $scope.confirmFriend = function (id, event) {
            $.ajax({
                url: root + "friends/confirm/index.php",
                type: "POST",
                data: {
                    userId1: userID,
                    userId2: id
                }
            }).success(function (msg) {
                $scope.status = JSON.parse(msg);
                $scope.$apply();
                $(event.target).text("Delete friend");
                requests();
            })
        };

        $scope.deleteFriend = function (id, event) {
            $.ajax({
                url: root + "friends/delete/index.php",
                type: "POST",
                data: {
                    userId1: userID,
                    userId2: id
                }
            }).success(function (msg) {
                $scope.status = JSON.parse(msg);
                $scope.$apply();
                $(event.target).parent().parent().fadeOut("slow");
            })
        }

        function requests() {
            $.ajax({
                url: root + "user/requests/index.php",
                type: "POST",
                data: {
                    userId: userID
                }
            }).success(function (msg) {
                $scope.requests = JSON.parse(msg);
                $scope.$apply();
                pendingButton();

            })
        }

        requests();

        function pendingButton() {
            var numRequests = $scope.requests.data.length;
            $("#pending").text(numRequests + " New Requests");

            $("#pending").on("click", function () {

                console.log(msg);
                if ($scope.requests.data.length == 0) {
                    $("#pendingMsg").text("No new requests");
                    setTimeout(function () {
                        $("#pendingMsg").html("")
                    }, 2000);
                } else {
                    $("#friendsContainer").children("ul").children("li").remove();

                    var ufr = $scope.requests.data;
                    $scope.listFriends(ufr);
                }
            })
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

    });
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

});
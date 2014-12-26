/**
 * Created by Vilim Stubičan on 3.12.2014..
 */
var app = angular.module("chat",[]);


app.controller("chatCtrl",function($scope, $filter){
    var root = "http://vdl.hr/ferbook/";
    var Server;
    $(document).ready(function() {

        Server = new FancyWebSocket("ws://192.168.1.221:9000");
        console.log("connecting");

        Server.bind("open",function() {
            var welcomeMsg = {
                type : "welcome",
                id : $scope.activeUser
            };
            Server.send("message", JSON.stringify(welcomeMsg));
        });

        Server.bind("message", function(msg){
            var newMsg = JSON.parse(msg);
            var chatNotOpen = true;
            for(key in $scope.conversations) {
                if(key == newMsg["sender"]) {
                    $scope.conversations[key].push(newMsg["msg"]);
                    $scope.$apply();
                    chatNotOpen = false;
                    break;
                }
            }

            if(chatNotOpen) {
                $.ajax({
                    url : "http://vdl.hr/ferbook/messages/conversation",
                    type : "POST",
                    data : {
                        userId1 : parseInt(newMsg["sender"]),
                        userId2 : parseInt($scope.activeUser)
                    }
                }).success(function(msg){
                    $scope.conversations[parseInt(newMsg["sender"])] = JSON.parse(msg);
                    $scope.conversations[parseInt(newMsg["sender"])].push(newMsg["msg"]);
                    $scope.$apply();
                })
            }


            scrollMax(parseInt(newMsg["sender"]));

        })

        Server.connect();


    })


    $.ajax({
        url: root+"user/active/",
        type : "POST",
        data : {}
    }).success(function(msg){
        var json = JSON.parse(msg);
        $scope.activeUser = parseInt(json.data.id);

        $.ajax({
            url: root + "search/",
            type : "POST",
            data : {
                userId : $scope.activeUser,
                query : ""
            }
        }).success(function (msg){
            var json = JSON.parse(msg);
            console.log(msg);
            $scope.friends = json.data;
            $scope.$apply();
        })



    })


    $scope.conversations = JSON.parse("{}");


    $scope.getClass = function(senderId, requestClass) {
        if(requestClass == "me") {
            return senderId == $scope.activeUser;
        } else {
            return senderId != $scope.activeUser;
        }
    }

    $scope.formatDate = function(unixTimestamp) {
        var date = new Date(unixTimestamp*1000);
        return date.getDate() + "/" + date.getMonth() +
            "/" + date.getFullYear() + " " + date.getHours()
        + "-" + date.getMinutes();
    }

    $scope.sendMsg = function($event, tab, index) {
        if($event.keyIdentifier == "Enter" && !$event.shiftKey
            || $.browser.mozilla && $event.key == "Enter") {
            if($.browser.mozilla) {
                var value = $event.target.value;
                $event.target.value = "";
            } else {
                var value = $event.srcElement.value;
                $event.srcElement.value = "";
            }

            if(value.replace(/\n/g, "") == "") {
                return;
            }
            var message = $scope.prepareMsg(value);
            tab.push({
                "message" : message,
                "senderId": $scope.activeUser,
                "timestamp" : new Date().getTime()/1000,
                "flag": 1
            });

            var sendMsg = {
                type : "message",
                sender : $scope.activeUser,
                recipient : parseInt(index),
                message : message
            }
            Server.send("message",JSON.stringify(sendMsg));
            setTimeout(function(){
                scrollMax(parseInt(index))
            },250);


            $.ajax({
                url : "http://vdl.hr/ferbook/messages/send/",
                type : "POST",
                data : {
                    userId1 : parseInt($scope.activeUser),
                    userId2 : parseInt(index),
                    message : message
                }
            })
        }
    }

    $scope.prepareMsg = function (string) {
        return string.trim();
    }


    $scope.openConversation = function (elem) {
        if(angular.isUndefined($scope.conversations[parseInt(elem.friend.id)])) {
            $.ajax({
                url : "http://vdl.hr/ferbook/messages/conversation/",
                type : "POST",
                data : {
                    userId1 : parseInt($scope.activeUser),
                    userId2 : parseInt(elem.friend.id)
                }
            }).success(function(msg){
                console.log(msg);
                $scope.conversations[parseInt(elem.friend.id)] = JSON.parse(msg)["data"];
                $scope.$apply();
            })
        }
    }

    $scope.closeConversation = function(index) {
        var newArray = JSON.parse("{}");
        for(key in $scope.conversations) {
            if(parseInt(key) == parseInt(index)) {
                continue;
            }
            newArray[key] = $scope.conversations[key];
        }

        $scope.conversations = newArray;
    }

    $scope.getFriendsName = function(index, flag) {
        if(parseInt(index) == $scope.activeUser) {
            return "Me";
        }
        for(i in $scope.friends) {
            if(parseInt(index) == parseInt($scope.friends[i].id)) {
                var friend = $scope.friends[i];
                if(flag == 2) {
                    return friend.name + " " + friend.lastname;
                } else {
                    return friend.name;
                }
            }
        }
    }

    function scrollMax(index){
        document.getElementsByClassName("mes"+index)[0].scrollTop = document.getElementsByClassName("mes"+index)[0].scrollHeight + 150;
    }
});


function toggleVisibility(elem, toggle) {
    var parent = $(elem).parent();
    if(parent.height() > 50) {
        //hide
        parent.css("height","20px");
        parent.css("width","100px");
    } else {
        //show
        parent.css("height","280px");
        parent.css("width","220px");
    }
}

function toggleUsersVisibility(elem, toggle) {
    var parent = $(elem).parent();
    if(parent.height() > 25) {
        //hide
        parent.css("height","20px");
    } else {
        //show
        parent.css("height","100%");
    }
}
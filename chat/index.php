<!DOCTYPE html>
<html>
<head>
    <meta charset='UTF-8' />

    <!-- OVO MORA BITI UKLJUČENO, ALI JE OVO I VAMA UKLJUČENO-->
    <script src="angular.min.js"></script>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min.js"></script>

    <!-- MOJI FILEOVI KOJE MORATE UKLJUČITI, GDJE GOD IH SMJESTILI -->
    <script src="http://code.jquery.com/jquery-migrate-1.2.1.js"></script>
    <script src="fancywebsocket.js"></script>
    <script src="chat.js" type="text/javascript"></script>
    <link href="chat.css" rel="stylesheet" type="text/css" />

</head>
<body>

<!-- OVAJ DIV SAMO PREKOPIRAJTE, SVE OSTALO JE NAPRAVLJENO -->
<!-- START COPY HERE -->
<div class="chat-wrapper" ng-app="chat" ng-controller="chatCtrl">
    <div class="chat-users">
        <div class="chatTabHead" onclick="toggleUsersVisibility(this,1)"></div>
        <div ng-repeat="(index,friend) in friends | filter:friendsFilter | orderBy:'lastname'" ng-click="openConversation(this)" class="userTag">
            {{ friend.name }} {{ friend.lastname }}
        </div>
        <div>
            <input type="text" ng-model="friendsFilter" placeholder="Quick search">
        </div>
    </div>
    <div ng-repeat="(index, chatTab) in conversations" class="chatTab">
        <div class="chatTabHead" onclick="toggleVisibility(this,1)">
            <span ng-click="closeConversation(index)">x</span>
            {{ getFriendsName(index,2) }}
        </div>
        <div class="chatTabBody" ng-class="{'mes{{index}}':true}">
            <div ng-repeat="message in chatTab" class="message" ng-class="{'me':getClass(message.senderId,'me'), 'other':getClass(message.senderId,'other')}">
                <div class="sender">
                    {{ getFriendsName(message.senderId, 1) }}, {{ message.timestamp*1000 | date:'yyyy-MM-dd HH:mm' }}
                </div>
                <div class="text">
                    {{ message.message }}
                </div>
            </div>
        </div>
        <div class="chatInputHolder">
            <textarea class="chatInput" ng-keyup="sendMsg($event, chatTab, index)" placeholder="Enter message"></textarea>
        </div>
    </div>
</div>
<!-- END COPY HERE -->

</body>
</html>
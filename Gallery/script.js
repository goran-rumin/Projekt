/**
 * Created by Ante on 6.12.2014..
 */
'use strict';

var gallery = angular.module('gallery', ['ngAnimate', 'ngTouch', 'flow']);
gallery.controller('albumCtrl', ['$scope', '$http', '$templateCache', function ($scope, $http, $templateCache) {
    $scope.images = [];
    $scope.add = function(user) {
        var formData = user;
        $http({
            method: 'POST',
            url: '../ferbook/user/galleries/index.php',
            data: $.param({'userId' : '4'}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            cache: $templateCache
        })
            .success(function(response) {
                $scope.albumList = response.data;
                console.log($scope.albumList);
                console.log(formData);
                $scope.myAlbum = "";
            })
    };

    $scope.showImages = function(albumId) {
        var albumIdn = albumId;
        $http({
            method: 'POST',
            url: '../ferbook/photos/gallery/index.php',
            data: $.param({'albumId' : albumIdn}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            cache: $templateCache
        })
            .success(function(response) {
                $scope.images = response.data;
                console.log("Sad slike:");
                console.log($scope.images);
            })
    };

    $scope.addNewAlbum = function(name) {
        var ime = name;
        $http({
            method: 'POST',
            url: '../ferbook/user/addGallery/inbox.php',
            data: $.param({'userId' : '4', 'name' : ime}),
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            cache: $templateCache
        })
            .success(function(response) {
                $scope.albumList.push(ime);
                console.log(ime);
                console.log(name);
                console.log(response);
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
}]);
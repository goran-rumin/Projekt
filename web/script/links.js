/**
 * Created by Iva
 */
var rootRed = "http://localhost/projekt/projekt/ferbook/";
function openNewsfeed(id){
    window.location.replace(rootRed + "web/newsfeed/");
};


function openWall(id){
    window.location = rootRed+"web/wall/?userId=" + id;
};


function openGallery(id){
    window.location = rootRed+"web/gallery/?userId=" + id;
};

function openMessages(id){
    window.location = rootRed+"web/messages/?userId=" + id;
};
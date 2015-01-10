/**
 * Created by Iva
 */
var rootRed = "http://localhost/projekt/projekt/ferbook/web/";
function openNewsfeed(id){
    window.location.replace(rootRed + "newsfeed/");
};


function openWall(id){
    window.location = rootRed+"wall/?userId=" + id;
};


function openGallery(id){
    window.location = rootRed+"gallery/?userId=" + id;
};

function openMessages(id){
    window.location = rootRed+"messages/?userId=" + id;
};

function openConversation(id){
    window.location = rootRed+"Conversation/?userId=" + id;
};

function openNewMessage(id){
    window.location = rootRed+"NewMessage/?userId=" + id;
};
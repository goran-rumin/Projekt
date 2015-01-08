<?php
/**
 * Created by PhpStorm.
 * User: Davor
 * Date: 12.11.2014.
 * Time: 16:52
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>array(), "error" => array());

header("Access-Control-Allow-Origin: *");
// Check if the data is send in any way
if( !isset($_POST['userId1'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );
    echo json_encode($response);
    die();
};
// Fetch the data
$userId = $_POST['userId1'];
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
$inbox = $db->prepare("SELECT message, timestamp, flag, sender, recipient FROM messages WHERE sender = ? OR recipient = ?");
$inbox->bindParam(1, $userId);
$inbox->bindParam(2, $userId);
$inbox->setFetchMode(PDO::FETCH_OBJ);
$inbox->execute();
$allFriends = array();
$friendInformations = array();
foreach ( $inbox as $oneMessage) {
    if ( $oneMessage->sender != $userId ) {
        if (in_array($oneMessage->sender, $allFriends)) {
            $position = array_search($oneMessage->sender, $allFriends);
            //older messages on database always have lower ID, so we can be sure that newer messages will come later
            //if ( $oneMessage->timestamp > $friendInformations[$position]["lastMessage"]["timestamp"]) {
                $lastMessage = array(
                    "message" => $oneMessage->message,
                    "senderId" => $oneMessage->sender,
                    "timestamp" => $oneMessage->timestamp,
                    "flag" => $oneMessage->flag
                );
                $friendInformations[$position]["lastMessage"] = $lastMessage;
                $container = $friendInformations[$position];
                array_splice($friendInformations, $position, 1);
                $friendInformations[] = $container;
            //}
        } else {
            $allFriends[] = $oneMessage->sender;
            $lastMessage = array(
                "message" => $oneMessage->message,
                "senderId" => $oneMessage->sender,
                "timestamp" => $oneMessage->timestamp,
                "flag" => $oneMessage->flag
            );
            $friend = $db->prepare("SELECT name, last_name FROM user WHERE id = ?");
            $friend->bindParam(1, $oneMessage->sender);
            $friend->setFetchMode(PDO::FETCH_OBJ);
            $friend->execute();
            $row = $friend->fetch();
            $friendInformations[] = array(
                "userId" => $oneMessage->sender,
                "name" => $row->name,
                "lastname" => $row->last_name,
                "lastMessage" => $lastMessage
            );
        }
    } else if (in_array($oneMessage->recipient, $allFriends)) {
        $position = array_search($oneMessage->recipient, $allFriends);
        //if ( $oneMessage->timestamp > $friendInformations[$position]["lastMessage"]["timestamp"]) {
            $lastMessage = array(
                "message" => $oneMessage->message,
                "senderId" => $oneMessage->sender,
                "timestamp" => $oneMessage->timestamp,
                "flag" => $oneMessage->flag
            );
            $friendInformations[$position]["lastMessage"] = $lastMessage;
            $container = $friendInformations[$position];
            array_splice($friendInformations, $position, 1);
            $friendInformations[] = $container;
        //}
    } else {
        $allFriends[] = $oneMessage->recipient;
        $lastMessage = array(
            "message" => $oneMessage->message,
            "senderId" => $oneMessage->sender,
            "timestamp" => $oneMessage->timestamp,
            "flag" => $oneMessage->flag
        );
        $friend = $db->prepare("SELECT name, last_name FROM user WHERE id = ?");
        $friend->bindParam(1, $oneMessage->recipient);
        $friend->setFetchMode(PDO::FETCH_OBJ);
        $friend->execute();
        $row = $friend->fetch();
        $friendInformations[] = array(
            "userId" => $oneMessage->recipient,
            "name" => $row->name,
            "lastname" => $row->last_name,
            "lastMessage" => $lastMessage
        );
    }
}
$response["data"] = $friendInformations;
echo json_encode($response, JSON_UNESCAPED_UNICODE);
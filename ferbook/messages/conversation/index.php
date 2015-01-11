<?php
/**
 * Created by PhpStorm.
 * User: Davor
 * Date: 12.11.2014.
 * Time: 11:37
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";


header("Access-Control-Allow-Origin: *");
// Define response array
$response = array("data"=>array(), "error" => array());

// Check if the data is send in any way
if( !isset($_POST['userId1']) || !isset($_POST['userId2'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );
    echo json_encode($response);
    die();
};
// Fetch the data
$userId1 = $_POST['userId1'];
$userId2 = $_POST['userId2'];
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
$messages = $db->prepare("SELECT COUNT(*) as allMessages FROM messages WHERE sender = ? AND recipient = ? OR sender = ? AND recipient = ?");
$messages->bindParam(1, $userId1);
$messages->bindParam(2, $userId2);
$messages->bindParam(3, $userId2);
$messages->bindParam(4, $userId1);
$messages->setFetchMode(PDO::FETCH_OBJ);
$messages->execute();
$numberOfMessages = $messages->fetch()->allMessages;
if ( $numberOfMessages > 0 ) {
    $messages = $db->prepare("SELECT messages.id, messages.message as message, messages.timestamp as timestamp, messages.flag as flag, messages.sender as sender, user.name as name, user.last_name as last_name, user.picture as picture FROM messages INNER JOIN user ON user.id = messages.sender WHERE sender = ? AND recipient = ? OR sender = ? AND recipient = ? ORDER BY messages.timestamp");
    $messages->bindParam(1, $userId1);
    $messages->bindParam(2, $userId2);
    $messages->bindParam(3, $userId2);
    $messages->bindParam(4, $userId1);
    $messages->setFetchMode(PDO::FETCH_OBJ);
    $messages->execute();
    $output = array();
    for ($i = 0; $i < $numberOfMessages; $i++) {
        $onemessage = $messages->fetch();
        if ( $onemessage->flag == 1 AND $onemessage->sender != $userId1 ) {
            $flag = 2;
            $read = $db->prepare("UPDATE messages SET flag = ? WHERE id = ?");
            $read->bindParam(1, $flag);
            $read->bindParam(2, $onemessage->id);
            $read->setFetchMode(PDO::FETCH_OBJ);
            $read->execute();
            $oneoutput = array(
                "message" => $onemessage->message,
                "senderId" => $onemessage->sender,
                "timestamp" => $onemessage->timestamp,
                "flag" => $flag,
				"name" => $onemessage->name,
				"last_name" => $onemessage->last_name,
				"picture" => $onemessage->picture
            );
        } else {
            $oneoutput = array(
                "message" => $onemessage->message,
                "senderId" => $onemessage->sender,
                "timestamp" => $onemessage->timestamp,
                "flag" => $onemessage->flag,
				"name" => $onemessage->name,
				"last_name" => $onemessage->last_name,
				"picture" => $onemessage->picture
            );
        }
        $output[] = $oneoutput;
    }
    $response["data"] = $output;
}
echo json_encode($response, (float) JSON_UNESCAPED_UNICODE);

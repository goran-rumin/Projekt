<?php
/**
 * Created by PhpStorm.
 * User: Davor
 * Date: 12.11.2014.
 * Time: 11:37
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>[], "error" => []);

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
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);
$messages = $db->prepare("SELECT COUNT(*) as allMessages FROM messages WHERE sender = ? AND recipient = ? OR sender = ? AND recipient = ?");
$messages->bindParam(1, $userId1);
$messages->bindParam(2, $userId2);
$messages->bindParam(3, $userId2);
$messages->bindParam(4, $userId1);
$messages->setFetchMode(PDO::FETCH_OBJ);
$messages->execute();
$numberOfMessages = $messages->fetch()->allMessages;
if ( $numberOfMessages > 0 ) {
    $messages = $db->prepare("SELECT id, message, timestamp, flag, sender FROM messages WHERE sender = ? AND recipient = ? OR sender = ? AND recipient = ?");
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
                "flag" => $flag
            );
        } else {
            $oneoutput = array(
                "message" => $onemessage->message,
                "senderId" => $onemessage->sender,
                "timestamp" => $onemessage->timestamp,
                "flag" => $onemessage->flag
            );
        }
        $output[] = $oneoutput;
    }
    $response["data"] = $output;
}
echo json_encode($response);

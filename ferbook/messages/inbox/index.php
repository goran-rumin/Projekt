<?php

include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>array(), "error" => array());

header("Access-Control-Allow-Origin: *");
// Check if the data is send in any way
if( !isset($_POST['userId'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );
    echo json_encode($response);
    die();
};
// Fetch the data
$userId = $_POST['userId'];
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));


$prijatelji = $db->prepare("SELECT DISTINCT(sender) as 'id' FROM messages WHERE recipient = ?
UNION
SELECT DISTINCT(recipient) as 'id' FROM messages WHERE sender = ?");
$prijatelji->bindParam(1, $userId);
$prijatelji->bindParam(2, $userId);
$prijatelji->setFetchMode(PDO::FETCH_OBJ);
$prijatelji->execute();

$friendsInformations = array();
$lastMessage = array();
foreach($prijatelji as $prijatelj){
    $zadnja_poruka = $db->prepare("SELECT id,message, timestamp, flag, sender FROM messages
	WHERE sender = ? AND recipient = ? 
	OR sender = ? AND recipient = ?
	ORDER BY id DESC
	LIMIT 1;");
    $zadnja_poruka->bindParam(1, $prijatelj->id);
    $zadnja_poruka->bindParam(2, $userId);
    $zadnja_poruka->bindParam(3, $userId);
    $zadnja_poruka->bindParam(4, $prijatelj->id);
    $zadnja_poruka->setFetchMode(PDO::FETCH_OBJ);
    $zadnja_poruka->execute();
    $zadnja_poruka2=$zadnja_poruka->fetch();
    $lastMessage = array(
                    "message" => $zadnja_poruka2->message,
                    "senderId" => $zadnja_poruka2->sender,
                    "timestamp" => $zadnja_poruka2->timestamp,
                    "flag" => $zadnja_poruka2->flag
                );
    $korisnik = $db->prepare("SELECT name, last_name, picture FROM user
	WHERE id = ?");
    $korisnik->bindParam(1, $prijatelj->id);
    $korisnik->setFetchMode(PDO::FETCH_OBJ);
    $korisnik->execute();
    $row = $korisnik->fetch();
    $friendInformations[] = array(
                "userId" => $prijatelj->id,
                "name" => $row->name,
                "lastname" => $row->last_name,
		"picture" => $row->picture,
                "lastMessage" => $lastMessage
            );
}

$response["data"] = $friendInformations;
echo json_encode($response, (float) JSON_UNESCAPED_UNICODE);

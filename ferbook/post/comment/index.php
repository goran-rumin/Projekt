<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 16.11.2014.
 * Time: 14:40
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>array(), "error" => array());

header("Access-Control-Allow-Origin: *");
// Check if the data is send in any way

if( !isset($_POST['userId']) || !isset($_POST['postId']) ||
    !isset($_POST["message"])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );

    echo json_encode($response);
    die();
};


// Fetch the data
$userId = $_POST['userId'];
$postId = $_POST['postId'];
$message = $_POST["message"];

if($message == "") {
    $response["error"]=array(
        "errNum" => 13,
        "errInfo" => "No input values set."
    );

    echo json_encode($response);
    die();
}

$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));


// Check if user exists
$query = $db->prepare("SELECT COUNT(*) as userExists FROM user WHERE id = ?");
$query->bindParam(1, $userId);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

$row = $query->fetch();
if($row) {
    $userCount = $row->userExists;
    if($userCount != 1) {
        $response["error"]=array(
            "errNum" => 12,
            "errInfo" => "Invalid user ids."
        );

        echo json_encode($response);
        die();
    }
} else {
    $response["error"]=array(
        "errNum" => 12,
        "errInfo" => "Invalid user ids."
    );

    echo json_encode($response);
    die();
}

// Check if post exists
$query = $db->prepare("SELECT recipient FROM post WHERE id = ? AND parent IS NULL");
$query->bindParam(1, $postId);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

$row = $query->fetch();
if($row) {
    $recipient = $row->recipient;
} else {
    $response["error"]=array(
        "errNum" => 14,
        "errInfo" => "Invalid post id."
    );

    echo json_encode($response);
    die();
}

$query = $db->prepare("INSERT INTO post(parent, sender, recipient, message) VALUES(?,?,?,?)");
$query->bindParam(1, $postId);
$query->bindParam(2, $userId);
$query->bindParam(3, $recipient);
$query->bindParam(4, $message);
$query->execute();

$query = $db->prepare("SELECT id FROM post WHERE parent = ? AND sender = ? ORDER BY timestamp DESC LIMIT 1");
$query->bindParam(1, $postId);
$query->bindParam(2, $userId);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

$row = $query->fetch();

$response["data"] = array(
    "postId" => $row->id
);

echo json_encode($response, JSON_UNESCAPED_UNICODE);


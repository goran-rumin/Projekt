<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 16.11.2014.
 * Time: 13:32
 */
include_once "../../constants.php";
include_once "../../classes/Crypter.php";


header("Access-Control-Allow-Origin: *");
// Define response array
$response = array("data"=>array(), "error" => array());

// Check if the data is send in any way

if( !isset($_POST['sender']) || !isset($_POST['recipient']) ||
    !isset($_POST["message"]) && !isset($_POST["url"])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );

    echo json_encode($response);
    die();
};


// Fetch the data
$sender = $_POST['sender'];
$recipient = $_POST['recipient'];
$message = isset($_POST['message']) ? $_POST['message']: "";
$url = isset($_POST['url']) ? $_POST['url']: "";

if($message == "" && $url == "") {
    $response["error"]=array(
        "errNum" => 13,
        "errInfo" => "No input values set."
    );

    echo json_encode($response);
    die();
}

$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));

$query = $db->prepare("SELECT COUNT(*) as userExists FROM user WHERE id = ? OR id = ?");
$query->bindParam(1, $sender);
$query->bindParam(2, $recipient);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

$row = $query->fetch();
if($row) {}
    else {
        $response["error"]=array(
            "errNum" => 12,
            "errInfo" => "Invalid user ids."
        );

        echo json_encode($response);
        die();
    }

$userCount = $row->userExists;
if($userCount == 1 && $sender != $recipient || $userCount == 0) {
    $response["error"]=array(
        "errNum" => 12,
        "errInfo" => "Invalid user ids."
    );

    echo json_encode($response);
    die();
}

$query = $db->prepare("INSERT INTO post(sender, recipient, message, url) VALUES(?,?,?,?)");
$query->bindParam(1, $sender);
$query->bindParam(2, $recipient);
$query->bindParam(3, $message);
$query->bindParam(4, $url);
$query->execute();

$query = $db->prepare("SELECT id FROM post WHERE sender = ? AND recipient = ? ORDER BY timestamp DESC LIMIT 1");
$query->bindParam(1, $sender);
$query->bindParam(2, $recipient);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

$row = $query->fetch();

$response["data"] = array(
    "postId" => $row->id
);

echo json_encode($response, (float) JSON_UNESCAPED_UNICODE);


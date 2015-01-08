<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 2.12.2014.
 * Time: 20:46
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>array(), "error" => array());


header("Access-Control-Allow-Origin: *");
// Check if the data is send in any way

if( !isset($_POST['postId'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );

    echo json_encode($response);
    die();
};


// Fetch the data
$postId = $_POST['postId'];

$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));


// Check if user exists
$query = $db->prepare("SELECT COUNT(*) as userExists FROM post WHERE id = ?");
$query->bindParam(1, $postId);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

$row = $query->fetch();
if($row) {
    $userCount = $row->userExists;
    if($userCount != 1) {
        $response["error"]=array(
            "errNum" => 12,
            "errInfo" => "Invalid post id."
        );

        echo json_encode($response);
        die();
    }
} else {
    $response["error"]=array(
        "errNum" => 12,
        "errInfo" => "Invalid post id."
    );

    echo json_encode($response);
    die();
}

// get all user friends
$query = $db->prepare("SELECT * FROM `user`");
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

$users = array();
foreach($query as $user) {
    $users[$user->id]["username"] = $user->username;
    $users[$user->id]["email"] = $user->mail;
    $users[$user->id]["name"] = $user->name;
    $users[$user->id]["lastname"] = $user->last_name;
    $users[$user->id]["picture"] = $user->picture;
}

$likes = array();

$query = $db->prepare("SELECT * FROM `like` WHERE postId = ?");
$query->bindParam(1,$postId);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

foreach($query as $like) {
    $likes[$like->id]["timestamp"] = $like->timestamp;
    $likes[$like->id]["userId"] = $like->user;
    $likes[$like->id]["username"] = $users[$like->user]["username"];
    $likes[$like->id]["name"] = $users[$like->user]["name"];
    $likes[$like->id]["lastname"] = $users[$like->user]["lastname"];
    $likes[$like->id]["email"] = $users[$like->user]["email"];
    $likes[$like->id]["picture"] = $users[$like->user]["picture"];
}

$response["data"] = $likes;

echo json_encode($response);
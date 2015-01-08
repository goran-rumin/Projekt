<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 2.12.2014.
 * Time: 21:16
 */


include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>array(), "error" => array());


header("Access-Control-Allow-Origin: *");
// Check if the data is send in any way

if( !isset($_POST['postId']) || !isset($_POST['userId'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );

    echo json_encode($response);
    die();
};


// Fetch the data
$postId = $_POST['postId'];
$userId = $_POST['userId'];

$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));


// Check if post exists
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
            "errInfo" => "Invalid user id."
        );

        echo json_encode($response);
        die();
    }
} else {
    $response["error"]=array(
        "errNum" => 12,
        "errInfo" => "Invalid user id."
    );

    echo json_encode($response);
    die();
}


$query = $db->prepare("SELECT COUNT(*) as nmbr FROM `like` WHERE postId = ? AND user = ?");
$query->bindParam(1,$postId);
$query->bindParam(2,$userId);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

$row = $query->fetch();

if($row->nmbr == 1) {
    $sql = "DELETE FROM `like` WHERE postId = ? AND user = ?";
    $like = "unlike";
} else {
    $sql = "INSERT INTO `like`(postId, user) VALUES(?,?)";
    $like = "like";
}

$query = $db->prepare($sql);
$query->bindParam(1, $postId);
$query->bindParam(2, $userId);
$query->execute();

$response["data"]["action"] = $like;

echo json_encode($response, (float) JSON_UNESCAPED_UNICODE);
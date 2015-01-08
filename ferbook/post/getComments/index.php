<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 16.11.2014.
 * Time: 20:05
 */


include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>array(), "error" => array() );


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


// Check if user exists
$query = $db->prepare("SELECT COUNT(*) as userExists FROM post WHERE id = ? AND parent IS NULL");
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

$comments = array();

//$query = $db->prepare("SELECT * FROM post WHERE parent = ?");
$query = $db->prepare("
    SELECT post.id as id, post.sender as sender, post.recipient as recipient, post.timestamp as timestamp, post.message as message, post.url as url, `like`.id as `like`,
    (SELECT COUNT(*) FROM `like` WHERE postId = post.id) as likeNmbr
    FROM post
    LEFT JOIN (SELECT * FROM `like` WHERE user = ?) as `like`
    ON post.id = `like`.postId
    WHERE post.parent = ?
");
$query->bindParam(1,$userId);
$query->bindParam(2,$postId);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

foreach($query as $comment) {
    $comments[$comment->id]["id"] = $comment->id;
    $comments[$comment->id]["timestamp"] = $comment->timestamp;
    $comments[$comment->id]["message"] = $comment->message;
    $comments[$comment->id]["url"] = $comment->url;
    $comments[$comment->id]["userId"] = $comment->sender;
    $comments[$comment->id]["username"] = $users[$comment->sender]["username"];
    $comments[$comment->id]["name"] = $users[$comment->sender]["name"];
    $comments[$comment->id]["lastname"] = $users[$comment->sender]["lastname"];
    $comments[$comment->id]["email"] = $users[$comment->sender]["email"];
    $comments[$comment->id]["picture"] = $users[$comment->sender]["picture"];
    $comments[$comment->id]["liked"] = ($comment->like == null) ? false : true;
    $comments[$comment->id]["likesNumber"] = $comment->likeNmbr;
}

$response["data"] = $comments;

echo json_encode($response, JSON_UNESCAPED_UNICODE);

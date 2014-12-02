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
// Define response array
$response = array("data"=>array(), "error" => array());

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
$offset = isset($_POST["offset"]) ? ($_POST["offset"] - 1 ) * 20 : 0;
if(!is_int($offset) || $offset < 0) $offset = 0;

$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);


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

// get all user friends
$query = $db->prepare("
    SELECT * FROM `user`
");
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


// get posts with comments
$query = $db->prepare("
    SELECT post.id as id, post.sender as sender, post.recipient as recipient, post.timestamp as timestamp, post.message as message, post.url as url, `like`.id as `like`
    FROM post
    LEFT JOIN (SELECT * FROM `like` WHERE user = ?) as `like`
    ON post.id = `like`.postId
    WHERE post.parent IS NULL AND (
    post.recipient IN (
        SELECT sender as id FROM `friends` WHERE recipient = ?
		UNION
		SELECT recipient as id FROM friends WHERE sender = ?
    ) )
    ORDER BY TIMESTAMP
    LIMIT ".$offset.",20
");
$query->bindParam(1, $userId);
$query->bindParam(2, $userId);
$query->bindParam(3, $userId);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

$posts = array();

foreach($query as $post) {
    $posts[$post->id]["postId"] = $post->id;
    $posts[$post->id]["text"] = $post->message;
    $posts[$post->id]["url"] = $post->url;
    $posts[$post->id]["timestamp"] = $post->timestamp;
    $posts[$post->id]["liked"] = ($post->like == null) ? false : true;

    $posts[$post->id]["senderId"] = $post->sender;
    $posts[$post->id]["senderUsername"] = $users[$post->sender]["username"];
    $posts[$post->id]["senderName"] = $users[$post->sender]["name"];
    $posts[$post->id]["senderLastname"] = $users[$post->sender]["lastname"];
    $posts[$post->id]["senderEmail"] = $users[$post->sender]["email"];
    $posts[$post->id]["senderPicture"] = $users[$post->sender]["picture"];


    $posts[$post->id]["recipientId"] = $post->recipient;
    $posts[$post->id]["recipientUsername"] = $users[$post->recipient]["username"];
    $posts[$post->id]["recipientName"] = $users[$post->recipient]["name"];
    $posts[$post->id]["recipientLastname"] = $users[$post->recipient]["lastname"];
    $posts[$post->id]["recipientEmail"] = $users[$post->recipient]["email"];
    $posts[$post->id]["recipientPicture"] = $users[$post->recipient]["picture"];
}


$response["data"] = $posts;

echo json_encode($response);

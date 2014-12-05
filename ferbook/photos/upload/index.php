<?php
/**
 * Created by PhpStorm.
 * User: Davor
 * Date: 3.12.2014.
 * Time: 15:27
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>[], "error" => []);

// Check if the data is send in any way
if( !isset($_POST['userId']) || !isset($_POST['url'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );
    echo json_encode($response);
    die();
};
// Fetch the data
if( isset($_POST['albumId'])) {
    $albumId = $_POST['albumId'];
} else $albumId = 75000;
$userId = $_POST['userId'];
$url = base64_decode($_POST['url']);
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);
$defAlbum = $db->prepare("SELECT COUNT(*) as defaultAlbum FROM album WHERE id = ?");
$defAlbum->bindParam(1, $albumId);
$defAlbum->setFetchMode(PDO::FETCH_OBJ);
$defAlbum->execute();

// If default album does not exist, create it
if ( $defAlbum->fetch()->defaultAlbum == 0 ) {
    $defAlbum = $db->prepare("INSERT INTO album(id, name, creator) VALUES (?,?,?)");
    $default = "default";
    $user = 0;
    $defAlbum->bindParam(1, $albumId);
    $defAlbum->bindParam(2, $default);
    $defAlbum->bindParam(3, $user);
    $defAlbum->setFetchMode(PDO::FETCH_OBJ);
    $defAlbum->execute();
}

// Create new post with image
$upload = $db->prepare("INSERT INTO post(sender, recipient, url) VALUES (?,?,?)");
$upload->bindParam(1, $userId);
$upload->bindParam(2, $userId);
$upload->bindParam(3, $url);
$upload->setFetchMode(PDO::FETCH_OBJ);
$upload->execute();

// Get ID from new post
$postId = $db->prepare("SELECT id FROM post ORDER BY id DESC");
$postId->setFetchMode(PDO::FETCH_OBJ);
$postId->execute();
$postId=$postId->fetch()->id;

// Upload image to album
$upload = $db->prepare("INSERT INTO albumhasimage(idAlbum, idImage) VALUES (?,?)");
$upload->bindParam(1, $albumId);
$upload->bindParam(2, $postId);
$upload->setFetchMode(PDO::FETCH_OBJ);
$upload->execute();

$output = array(
    "url" => $url
);
$response["data"] = $output;
echo json_encode($response);
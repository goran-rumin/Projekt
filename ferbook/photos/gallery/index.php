<?php
/**
 * Created by PhpStorm.
 * User: Davor
 * Date: 3.12.2014.
 * Time: 15:39
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>array(), "error" => array());


header("Access-Control-Allow-Origin: *");
// Check if the data is send in any way
if( !isset($_POST['albumId'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );
    echo json_encode($response);
    die();
};
// Fetch the data
if( isset($_POST['userId'])) {
    $userId = $_POST['userId'];
} else $userId = 0;
$albumId = $_POST['albumId'];
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
$album = $db->prepare("SELECT idImage FROM albumhasimage WHERE idAlbum = ? ORDER BY idImage DESC");
$album->bindParam(1, $albumId);
$album->setFetchMode(PDO::FETCH_OBJ);
$album->execute();
$allImages = array();
foreach ($album as $imageId) {
    if ( $userId > 0 ) {
        $image = $db->prepare("SELECT url,message FROM post WHERE id = ? AND sender = ?");
        $image->bindParam(1, $imageId->idImage);
        $image->bindParam(2, $userId);
        $image->setFetchMode(PDO::FETCH_OBJ);
        $image->execute();
        foreach ( $image as $userimage ) {
            $imagedata = array(
                "postId" => $imageId->idImage,
                "url" => $userimage->url,
				"message" => $userimage->message
            );
            $allImages[] = $imagedata;
        }
    } else {
        $image = $db->prepare("SELECT url FROM post WHERE id = ?");
        $image->bindParam(1, $imageId->idImage);
        $image->setFetchMode(PDO::FETCH_OBJ);
        $image->execute();
        $imagedata = array(
            "postId" => $imageId->idImage,
            "url" => $image->fetch()->url
        );
        $allImages[] = $imagedata;
    }
}

$response["data"] = $allImages;
echo json_encode($response, (float) JSON_UNESCAPED_UNICODE);
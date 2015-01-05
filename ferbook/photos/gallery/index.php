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
$response = array("data"=>[], "error" => []);

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
$albumId = $_POST['albumId'];
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);
$album = $db->prepare("SELECT idImage FROM albumhasimage WHERE idAlbum = ? ORDER BY idImage DESC");
$album->bindParam(1, $albumId);
$album->setFetchMode(PDO::FETCH_OBJ);
$album->execute();
$allImages = array();
foreach ($album as $imageId) {
    $image = $db->prepare("SELECT url FROM post WHERE id = ?");
    $image->bindParam(1, $imageId->idImage);
    $image->setFetchMode(PDO::FETCH_OBJ);
    $image->execute();
    $imagedata = array (
        "postId" => $imageId->idImage,
        "url" => $image->fetch()->url
    );
    $allImages[] = $imagedata;
}

$response["data"] = $allImages;
echo json_encode($response);
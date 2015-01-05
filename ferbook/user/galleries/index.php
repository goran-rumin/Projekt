<?php
/**
 * Created by PhpStorm.
 * User: Davor
 * Date: 3.12.2014.
 * Time: 16:27
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>[], "error" => []);

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
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);
$albums = $db->prepare("SELECT id, name FROM album WHERE creator = ? ORDER BY id DESC");
$albums->bindParam(1, $userId);
$albums->setFetchMode(PDO::FETCH_OBJ);
$albums->execute();
foreach ($albums as $album) {
    $images = $db->prepare("SELECT COUNT(*) as allImages FROM albumhasimage WHERE idAlbum = ? ORDER BY idImage DESC");
    $images->bindParam(1, $album->id);
    $images->setFetchMode(PDO::FETCH_OBJ);
    $images->execute();
    if ( $images->fetch()->allImages > 0 ) {
        $postID = $db->prepare("SELECT idImage FROM albumhasimage WHERE idAlbum = ? ORDER BY idImage DESC");
        $postID->bindParam(1, $album->id);
        $postID->setFetchMode(PDO::FETCH_OBJ);
        $postID->execute();
        $lastimage = $db->prepare("SELECT url FROM post WHERE id = ?");
        $lastimage->bindParam(1, $postID->fetch()->idImage);
        $lastimage->setFetchMode(PDO::FETCH_OBJ);
        $lastimage->execute();
        $albumdata = array (
            "albumId" => $album->id,
            "name" => $album->name,
            "url" => $lastimage->fetch()->url
        );
    } else {
        $albumdata = array (
            "albumId" => $album->id,
            "name" => $album->name,
            "url" => ""
        );
    }

    $allAlbums[] = $albumdata;
}

$response["data"] = $allAlbums;
echo json_encode($response);
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
    $albumdata = array (
        "albumId" => $album->id,
        "name" => $album->name
    );
    $allAlbums[] = $albumdata;
}

$response["data"] = $allAlbums;
echo json_encode($response);
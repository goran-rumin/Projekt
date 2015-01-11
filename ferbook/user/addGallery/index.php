<?php
/**
 * Created by PhpStorm.
 * User: Davor
 * Date: 8.12.2014.
 * Time: 17:34
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";



	header("Access-Control-Allow-Origin: *");
	
// Define response array
$response = array("data"=>array(), "error" => array());

// Check if the data is send in any way
if( !isset($_POST['userId']) || !isset($_POST['name'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );
    echo json_encode($response);
    die();
};
// Fetch the data

$userId = $_POST['userId'];
$name = $_POST['name'];
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);
$addAlbum = $db->prepare("INSERT INTO album(name, creator) VALUES (?,?)");
$addAlbum->bindParam(1, $name);
$addAlbum->bindParam(2, $userId);
$addAlbum->setFetchMode(PDO::FETCH_OBJ);
$addAlbum->execute();
$addAlbum = $db->prepare("SELECT id FROM album WHERE name = ? AND creator = ?");
$addAlbum->bindParam(1, $name);
$addAlbum->bindParam(2, $userId);
$addAlbum->setFetchMode(PDO::FETCH_OBJ);
$addAlbum->execute();
$output = array(
    "albumId" => $addAlbum->fetch()->id
);
$response["data"] = $output;
echo json_encode($response);
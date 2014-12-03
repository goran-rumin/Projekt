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
if( !isset($_POST['postId']) || !isset($_POST['albumId'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );
    echo json_encode($response);
    die();
};
// Fetch the data
$postId = $_POST['postId'];
$albumId = $_POST['albumId'];
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);
$upload = $db->prepare("INSERT INTO albumhasimage(idAlbum, idImage) VALUES (?,?)");
$upload->bindParam(1, $albumId);
$upload->bindParam(2, $postId);
$upload->setFetchMode(PDO::FETCH_OBJ);
$upload->execute();

echo json_encode($response);
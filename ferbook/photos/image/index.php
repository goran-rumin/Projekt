<?php
/**
 * Created by PhpStorm.
 * User: Davor
 * Date: 3.12.2014.
 * Time: 14:26
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>[], "error" => []);

// Check if the data is send in any way
if( !isset($_POST['postId'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );
    echo json_encode($response);
    die();
};
// Fetch the data
$postId = $_POST['postId'];
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);
$image = $db->prepare("SELECT url FROM post WHERE id = ?");
$image->bindParam(1, $postId);
$image->setFetchMode(PDO::FETCH_OBJ);
$image->execute();
$url = $image->fetch()->url;
$output = array(
    "url" => $url
);

$response["data"] = $output;
echo json_encode($response);
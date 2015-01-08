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
$response = array("data"=>array(), "error" => array());

header("Access-Control-Allow-Origin: *");
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
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
$image = $db->prepare("SELECT url FROM post WHERE id = ?");
$image->bindParam(1, $postId);
$image->setFetchMode(PDO::FETCH_OBJ);
$image->execute();
$url = $image->fetch()->url;
$output = array(
    "url" => $url
);

$response["data"] = $output;
echo json_encode($response, JSON_UNESCAPED_UNICODE);
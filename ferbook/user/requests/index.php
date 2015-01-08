<?php
/**
 * Created by PhpStorm.
 * User: Davor
 * Date: 4.1.2015.
 * Time: 11:05
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>array(), "error" => array());

header("Access-Control-Allow-Origin: *");
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
$flag = 0;
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
$requests = $db->prepare("SELECT sender FROM friends WHERE recipient = ? AND flag = ?");
$requests->bindParam(1, $userId);
$requests->bindParam(2, $flag);
$requests->setFetchMode(PDO::FETCH_OBJ);
$requests->execute();
$allRequests = array();
foreach ( $requests as $request ) {
    $id = $request->sender;
    $friend = $db->prepare("SELECT name, last_name, picture FROM user WHERE id = ?");
    $friend->bindParam(1, $id);
    $friend->setFetchMode(PDO::FETCH_OBJ);
    $friend->execute();
    $friend = $friend->fetch();
    $requestdata = array (
        "id" => $id,
        "name" => $friend->name,
        "lastname" => $friend->last_name,
        "picture" => $friend->picture
    );
    $allRequests[] = $requestdata;
}

$allRequests = array_reverse($allRequests);
$response["data"] = $allRequests;
echo json_encode($response, (float) JSON_UNESCAPED_UNICODE);
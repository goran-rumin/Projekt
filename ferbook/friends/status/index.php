<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 6.12.2014.
 * Time: 11:37
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";


header("Access-Control-Allow-Origin: *");
// Define response array
$response = array("data"=>array(), "error" => array());

// Check if the data is send in any way

if( !isset($_POST['userId1']) || !isset($_POST['userId2'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );

    echo json_encode($response);
    die();
};


// Fetch the data
$userId1 = $_POST['userId1'];
$userId2 = $_POST['userId2'];


$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));

$query = $db->prepare("SELECT COUNT(*) as nmbr, flag,sender,recipient FROM friends WHERE sender = ? AND recipient = ? OR sender = ? AND recipient = ?");
$query->bindParam(1, $userId1);
$query->bindParam(2, $userId2);
$query->bindParam(3, $userId2);
$query->bindParam(4, $userId1);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

$row = $query->fetch();
if($row->nmbr != 1) {
    $response["data"]["statusNumber"] = -1;
    $response["data"]["status"] = "none";
} else {
        $response["data"]["sender"] = $row->sender;
        $response["data"]["recipient"] = $row->recipient;
    if($row->flag == 1) {
        $response["data"]["statusNumber"] = 1;
        $response["data"]["status"] = "friends";
    } else {
        $response["data"]["statusNumber"] = 0;
        $response["data"]["status"] = "pending";
    }
}

$response["data"]["success"] = true;
echo json_encode($response, (float) JSON_UNESCAPED_UNICODE);
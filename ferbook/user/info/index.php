<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 3.11.2014.
 * Time: 21:05
 */
include_once "../../constants.php";
include_once "../../classes/Crypter.php";

header("Access-Control-Allow-Origin: *");
// Define response array
$response = array("data"=>array(), "error" => array());

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


$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));

$checkLogin = $db->prepare("SELECT * FROM user WHERE id = ?");
$checkLogin->bindParam(1, $userId);
$checkLogin->setFetchMode(PDO::FETCH_OBJ);
$checkLogin->execute();

$row = $checkLogin->fetch();

if($row) {
    $output = array();
    $output["username"] = $row->username;
    $output["email"] = $row->mail;
    $output["name"] = $row->name;
    $output["lastname"] = $row->last_name;
    $output["picture"] = $row->picture;

    $response["data"] = $output;
    echo json_encode($response);
} else {
    $response["error"]=array(
        "errNum" => 10,
        "errInfo" => "No existing user with that id."
    );

    echo json_encode($response, JSON_UNESCAPED_UNICODE);
}
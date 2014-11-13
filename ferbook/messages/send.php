<?php
/**
 * Created by PhpStorm.
 * User: Davor
 * Date: 12.11.2014.
 * Time: 16:13
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>[], "error" => []);

// Check if the data is send in any way
if( !isset($_POST['userId1']) || !isset($_POST['userId2']) || !isset($_POST['message'])) {
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
$message = $_POST['message'];
$flag = 1;  // For now
$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);
$insertData = $db->prepare("INSERT INTO messages(sender, recipient, message, flag) VALUES
            (?,?,?,?)");
$insertData->bindParam(1,$userId1);
$insertData->bindParam(2,$userId2);
$insertData->bindParam(3,$message);
$insertData->bindParam(4,$flag);
$insertData->execute();

echo json_encode($response);
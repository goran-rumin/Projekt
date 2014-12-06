<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 6.12.2014.
 * Time: 11:14
 */


include_once "../../constants.php";
include_once "../../classes/Crypter.php";

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


$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);

$query = $db->prepare("SELECT COUNT(*) as nmbr FROM user WHERE id = ? || id = ?");
$query->bindParam(1, $userId1);
$query->bindParam(2, $userId2);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

$row = $query->fetch();

if($row->nmbr != 2) {

    $response["error"]=array(
        "errNum" => 10,
        "errInfo" => "No existing users with those ids."
    );

    echo json_encode($response);
    die();

}

$query = $db->prepare("SELECT COUNT(*) as nmbr FROM friends WHERE sender = ? AND recipient = ? OR sender = ? AND recipient = ?");
$query->bindParam(1, $userId1);
$query->bindParam(2, $userId2);
$query->bindParam(3, $userId2);
$query->bindParam(4, $userId1);
$query->setFetchMode(PDO::FETCH_OBJ);
$query->execute();

$row = $query->fetch();
if($row->nmbr > 0) {
    $response["error"]=array(
        "errNum" => 19,
        "errInfo" => "Users are already friends."
    );

    echo json_encode($response);
    die();
}



$query = $db->prepare("INSERT INTO friends(sender, recipient, flag) VALUES(?,?,0)");
$query->bindParam(1,$userId1);
$query->bindParam(2,$userId2);
$query->execute();

$response["data"]["success"] = true;
echo json_encode($response);
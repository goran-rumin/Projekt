<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 3.11.2014.
 * Time: 21:05
 */
include_once "../../constants.php";
include_once "../../classes/Crypter.php";

// Define response array
$response = array("data"=>array(), "error" => array());

// Fetch the data
$userId = $_POST['userId'];



// Check the input information

$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);
$checkUser = $db->prepare("SELECT * FROM user WHERE id = ?");
$checkUser->bindParam(1,$userId);
$checkUser->setFetchMode(PDO::FETCH_OBJ);
$checkUser->execute();

$result = $checkUser->fetch();

if($result) {}
    else {
        $response["error"]=array(
            "errNum" => 3,
            "errInfo" => "User with that username does not exist."
        );

        echo json_encode($response);
        die();
    }

if(isset($_POST["name"]) && isset($_POST["lastname"]) && isset($_POST["password"])) {
    $password = $_POST["password"];
    $name = $_POST["name"];
    $lastname = $_POST["lastname"];

    $passwordNotOk = strlen($password) <= 0  || strlen($password)>100;
    if($passwordNotOk) {
        $response["error"]=array(
            "errNum" => 2,
            "errInfo" => "Invalid data formats."
        );

        echo json_encode($response);
        die();
    }

    $query = $db->prepare("
        UPDATE user
        SET pass = ?, name = ?, last_name = ?
        WHERE id = ?
        ");
    $query->bindParam(1,$password);
    $query->bindParam(2,$name);
    $query->bindParam(3,$lastname);
    $query->bindParam(4,$userId);

    $query->execute();

} else if(isset($_POST["name"]) || isset($_POST["lastname"])) {
    $name = $_POST["name"];
    $lastname = $_POST["lastname"];

    $query = $db->prepare("
        UPDATE user
        SET name = ?, last_name = ?
        WHERE id = ?
        ");
    $query->bindParam(1,$name);
    $query->bindParam(2,$lastname);
    $query->bindParam(3,$userId);

    $query->execute();
} else if(isset($_POST["password"])){
    $password = $_POST["password"];
    $passwordNotOk = strlen($password) <= 0  || strlen($password)>100;
    if($passwordNotOk) {
        $response["error"]=array(
            "errNum" => 2,
            "errInfo" => "Invalid data formats."
        );

        echo json_encode($response);
        die();
    }

    $query = $db->prepare("
        UPDATE user
        SET pass = ?
        WHERE id = ?
        ");
    $query->bindParam(1,$password);
    $query->bindParam(2,$userId);

    $query->execute();
} else {
    $response["error"]=array(
        "errNum" => 11,
        "errInfo" => "Not a valid request."
    );

    echo json_encode($response);
    die();
}

if(isset($_POST['pictureUrl'])) {
    $query = $db->prepare("
        UPDATE user
        SET picture = ?
        WHERE id = ?
    ");
    $query->bindParam(1, $_POST["pictureUrl"]);
    $query->bindParam(2, $userId);
    $query->execute();
}


echo json_encode($response);


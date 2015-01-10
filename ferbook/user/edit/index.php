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

// Fetch the data
$userId = $_POST['userId'];



// Check the input information

$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
$checkUser = $db->prepare("SELECT * FROM user WHERE id = ?");
$checkUser->bindParam(1,$userId);
$checkUser->setFetchMode(PDO::FETCH_OBJ);
$checkUser->execute();

$result = $checkUser->fetch();

if($result) {}
    else {
        $response["error"]=array(
            "errNum" => 3,
            "errInfo" => "User with that user ID does not exist."
        );

        echo json_encode($response);
        die();
    }

if(isset($_POST['password'])) {
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
    $query->bindParam(1, $_POST["password"]);
    $query->bindParam(2, $userId);
    $query->execute();
}

if(isset($_POST['name'])) {
    $query = $db->prepare("
        UPDATE user
        SET name = ?
        WHERE id = ?
    ");
    $query->bindParam(1, $_POST["name"]);
    $query->bindParam(2, $userId);
    $query->execute();
}


if(isset($_POST['lastname'])) {
    $query = $db->prepare("
        UPDATE user
        SET last_name = ?
        WHERE id = ?
    ");
    $query->bindParam(1, $_POST["lastname"]);
    $query->bindParam(2, $userId);
    $query->execute();
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


echo json_encode($response, (float) JSON_UNESCAPED_UNICODE);


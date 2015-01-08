<?php
/**
 * Created by PhpStorm.
 * User: Ivan Kovačević
 * Date: 8.1.2014.
 * Time: 17:27
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";

header("Access-Control-Allow-Origin: *");


if(!$_FILES['photo']['error']) {
    $new_file_name = "upload.jpg"; //strtolower($_FILES['photo']['tmp_name']).; //rename file

    //move it to the new location
    move_uploaded_file($_FILES['photo']['tmp_name'], './'.$new_file_name);
    $message = 'Congratulations! Your file has been accepted.';
} else {
    //set that to be the returned message
    $message = 'Ooops!  Your upload triggered the following error:  '.$_FILES['photo']['error'];
}

$path= 'upload.jpg';
$type = pathinfo($path, PATHINFO_EXTENSION);
$data = file_get_contents($path);
$base64 = base64_encode($data);

// Define response array
$response = array("data"=>array(), "error" => array());
$response["data"] = $base64;
echo json_encode($response);
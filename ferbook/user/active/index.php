<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 11.12.2014.
 * Time: 4:04
 */
    session_start();
    include_once "../../constants.php";
    include_once "../../classes/Crypter.php";

header("Access-Control-Allow-Origin: *");
    // Define response array
    $response = array("data"=>array(), "error" => array());

    if(isset($_SESSION['userId'])) {
        $response["data"]["id"] = Crypter::decrypt($_SESSION["userId"]);
    } else {
        $response["data"]["id"] = -1;
    }

    echo json_encode($response,(float) JSON_UNESCAPED_UNICODE);
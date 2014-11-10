<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 10.11.2014.
 * Time: 23:54
 */

    include_once "../../constants.php";
    include_once "../../classes/Crypter.php";

    // Define response array
    $response = array("data"=>array(), "error" => array());

    // Check if the data is send in any way

    if( !isset($_POST['username']) || !isset($_POST['password']) ||
        !isset($_POST['repeatPassword']) || !isset($_POST['email'])) {
        $response["error"]=array(
            "errNum" => 1,
            "errInfo" => "Missing variable data."
        );

        echo json_encode($response);
        die();
    };


    // Fetch the data
    $username = $_POST['username'];
    $password = $_POST['password'];
    $repeatPassword = $_POST['repeatPassword'];
    $email = $_POST['email'];
    $name = $_POST['name'];
    $lastname = $_POST['lastname'];

    // Check the input information
    $usernameRegexNotOk = !preg_match("/^[a-zA-Z][a-zA-Z0-9-.]{2,29}$/",$username);
    $passwordNotOk = strlen($password) <= 0  || strlen($password)>100;
    $passwordsDontMatch = $password != $repeatPassword;
    $emailRegexNotOk = !preg_match("/^[a-zA-Z][a-zA-Z0-9-.]+@[a-zA-Z0-9]{3,25}.[a-zA-Z]{2,4}$/",$email);
    if($usernameRegexNotOk || $passwordNotOk || $emailRegexNotOk || $passwordsDontMatch) {
        $response["error"]=array(
            "errNum" => 2,
            "errInfo" => "Invalid data formats."
        );

        echo json_encode($response);
        die();
    }


    $db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);

    $checkLogin = $db->prepare("SELECT COUNT(*) as userExists FROM user WHERE username = ?");
    $checkLogin->bindParam(1, $username);
    $checkLogin->setFetchMode(PDO::FETCH_OBJ);
    $checkLogin->execute();

    $row = $checkLogin->fetch();

    if($row->userExists == 1) {
        $response["error"]=array(
            "errNum" => 5,
            "errInfo" => "User with that username already exists."
        );

        echo json_encode($response);
        die();
    }

    $checkLogin = $db->prepare("SELECT COUNT(*) as userExists FROM user WHERE mail = ?");
    $checkLogin->bindParam(1, $email);
    $checkLogin->setFetchMode(PDO::FETCH_OBJ);
    $checkLogin->execute();

    $row = $checkLogin->fetch();
    if($row->userExists == 1) {
        $response["error"]=array(
            "errNum" => 6,
            "errInfo" => "User with that email address already exists."
        );

        echo json_encode($response);
        die();
    }




    $insertData = $db->prepare("INSERT INTO user(username, pass, mail, name, last_name) VALUES
            (?,?,?,?,?)");
    $insertData->bindParam(1,$username);
    $insertData->bindParam(2,$password);
    $insertData->bindParam(3,$email);
    $insertData->bindParam(4,$name);
    $insertData->bindParam(5,$lastname);
    $insertData->execute();




    $getUserID = $db->prepare("SELECT id FROM user WHERE username = ?");
    $getUserID->bindParam(1, $username);
    $getUserID->setFetchMode(PDO::FETCH_OBJ);
    $getUserID->execute();

    $row = $getUserID->fetch();
    // Check the passwords, depending on the type of the database encryption

    $response["data"] = array(
        "userId" => $row->id
    );

    echo json_encode($response);


/*
 * WEB DATA STORAGE
 * - encrypt the data
 * - save into a $_SESSION['userId'] variable
 *
 * $_SESSION['userId'] = Crypter::encrypt($userId);
 */



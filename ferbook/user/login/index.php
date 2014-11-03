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
    $response = array("data"=>[], "error" => []);

    // Check if the data is send in any way

    if( !isset($_POST['username']) || !isset($_POST['password'])) {
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

    // Check the input information
    $usernameRegexNotOk = !preg_match("/^[a-zA-Z][a-zA-Z0-9-.]{2,29}$/",$username);
    $passwordNotOk = strlen($password) <= 0  || strlen($password)>100;
    if($usernameRegexNotOk || $passwordNotOk) {
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

    if($row->userExists != 1) {
        $response["error"]=array(
            "errNum" => 3,
            "errInfo" => "User with that username does not exist."
        );

        echo json_encode($response);
        die();
    }

    $checkLogin = $db->prepare("SELECT id, pass FROM user WHERE username = ?");
    $checkLogin->bindParam(1, $username);
    $checkLogin->setFetchMode(PDO::FETCH_OBJ);
    $checkLogin->execute();

    $row = $checkLogin->fetch();
    // Check the passwords, depending on the type of the database encryption

    if( $password != $row->pass) {
        //for extra security against brute forcing, add a new table called "login_atempts"
        //and do not allow more than 5 wrong logins/hour
        $response["error"]=array(
            "errNum" => 4,
            "errInfo" => "Wrong username and password combination."
        );

        echo json_encode($response);
        die();
    }

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



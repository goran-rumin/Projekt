<?php
/**
 * Created by PhpStorm.
 * User: Vilim Stubičan
 * Date: 5.12.2014.
 * Time: 22:25
 */

include_once "../constants.php";
include_once "../classes/Crypter.php";

// Define response array
$response = array("data"=>array(), "error" => array());

// Check if the data is send in any way

if( !isset($_POST['query'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );

    echo json_encode($response);
    die();
};


// Fetch the data
$query = strtolower($_POST['query']);
if(isset($_POST['userId']))  { $userExists = true; }
    else { $userExists = false; }

$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";", SQL_USERNAME, SQL_PASSWORD);

if($userExists) {
// Check if user exists
    $sql = $db->prepare("SELECT COUNT(*) as userExists FROM user WHERE id = ?");
    $sql->bindParam(1, $_POST['userId']);
    $sql->setFetchMode(PDO::FETCH_OBJ);
    $sql->execute();

    $row = $sql->fetch();
    if ($row) {
        $userCount = $row->userExists;
        if ($userCount != 1) {
            $response["error"] = array(
                "errNum" => 12,
                "errInfo" => "Invalid user id."
            );

            echo json_encode($response);
            die();
        }
    } else {
        $response["error"] = array(
            "errNum" => 12,
            "errInfo" => "Invalid user id."
        );

        echo json_encode($response);
        die();
    }
}
if($query != "") {
    $tmpQueryData = explode(" ", $query);
    $queryData = array();
    foreach ($tmpQueryData as $data) {
        if ($data != "") {
            $queryData[] = $data;
        }
    }

    $dataNmbr = count($queryData);

    $inUsernameField = "";
    for ($i = 1; $i <= $dataNmbr; $i++) {
        if ($i > 1) $inUsernameField .= " OR ";
        $inUsernameField .= "LOWER(username) LIKE ? ";
    }

    $inNameField = "";
    for ($i = 1; $i <= $dataNmbr; $i++) {
        if ($i > 1) $inNameField .= " OR ";
        $inNameField .= "LOWER(name) LIKE ? ";
    }

    $inLastnameField = "";
    for ($i = 1; $i <= $dataNmbr; $i++) {
        if ($i > 1) $inLastnameField .= " OR ";
        $inLastnameField .= "LOWER(name) LIKE ? ";
    }

    if ($userExists) {
        $query = $db->prepare("SELECT * FROM user WHERE ( " . $inUsernameField . "  OR
                        " . $inNameField . " OR " . $inLastnameField . ")
                        AND id IN (
                            SELECT sender as id FROM `friends` WHERE recipient = ?  AND flag = 1
                            UNION
                            SELECT recipient as id FROM friends WHERE sender = ?  AND flag = 1
                        )
                         ORDER BY last_name, name, username
                         LIMIT 20");

        $offset = 3 * $dataNmbr;
        $query->bindParam($offset + 1, $_POST['userId']);
        $query->bindParam($offset + 2, $_POST['userId']);

    } else {
        $query = $db->prepare("SELECT * FROM user WHERE ( " . $inUsernameField . "  OR
                        " . $inNameField . " OR " . $inLastnameField . ")
                        ORDER BY last_name, name, username
                        LIMIT 20");
    }

    $offset = 0;
    for ($i = 1; $i <= $dataNmbr; $i++) {
        $param = "%" . ($queryData[$i - 1]) . "%";
        $query->bindParam($offset + $i, $param);
    }

    $offset = $dataNmbr;
    for ($i = 1; $i <= $dataNmbr; $i++) {
        $param = "%" . ($queryData[$i - 1]) . "%";
        $query->bindParam($offset + $i, $param);
    }

    $offset = 2 * $dataNmbr;
    for ($i = 1; $i <= $dataNmbr; $i++) {
        $param = "%" . ($queryData[$i - 1]) . "%";
        $query->bindParam($offset + $i, $param);
    }

    $query->setFetchMode(PDO::FETCH_OBJ);
    $query->execute();

    foreach ($query as $row) {
        $set = array();
        $set["id"] = $row->id;
        $set["username"] = $row->username;
        $set["picture"] = $row->picture;
        $set["name"] = $row->name;
        $set["lastname"] = $row->last_name;

        $response["data"][] = $set;
    }
} else {
    $query = $db->prepare("SELECT * FROM user WHERE id IN (
                            SELECT sender as id FROM `friends` WHERE recipient = ?  AND flag = 1
                            UNION
                            SELECT recipient as id FROM friends WHERE sender = ?  AND flag = 1
                        )
                         ORDER BY last_name, name, username
                         LIMIT 20");
    $query->bindParam(1, $_POST["userId"]);
    $query->bindParam(2, $_POST["userId"]);
    $query->setFetchMode(PDO::FETCH_OBJ);
    $query->execute();

    foreach ($query as $row) {
        $set = array();
        $set["id"] = $row->id;
        $set["username"] = $row->username;
        $set["picture"] = $row->picture;
        $set["name"] = $row->name;
        $set["lastname"] = $row->last_name;

        $response["data"][] = $set;
    }
}

echo json_encode($response);
?php
/**
 * Created by PhpStorm.
 * User: Davor
 * Date: 3.12.2014.
 * Time: 15:27
 */

include_once "../../constants.php";
include_once "../../classes/Crypter.php";
 
header("Access-Control-Allow-Origin: *");

// Define response array
$response = array("data"=>array(), "error" => array());

// Check if the data is send in any way
if( !isset($_POST['userId']) || !isset($_POST['url'])) {
    $response["error"]=array(
        "errNum" => 1,
        "errInfo" => "Missing variable data."
    );
    echo json_encode($response);
    die();
};
// Fetch the data 
$userId = $_POST['userId'];

if(isset($_POST['userId2'])){
    $userId2=$_POST['userId2'];
}
else{
    $userId2=$_POST['userId'];
}

$db = new PDO("mysql:host=".SQL_HOST.";dbname=".SQL_DBNAME.";charset=utf8", SQL_USERNAME, SQL_PASSWORD, array(PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8"));
if( isset($_POST['albumId'])) {
    $albumId = $_POST['albumId'];
} else {
    $albumName = $userId2."_Default";
    $defAlbum = $db->prepare("SELECT id FROM album WHERE name = ?");
    $defAlbum->bindParam(1, $albumName);
    $defAlbum->setFetchMode(PDO::FETCH_OBJ);
    $defAlbum->execute();
    $albumId = $defAlbum->fetch()->id;
}

$data = base64_decode($_POST['url']);
$img = $_POST["url"];
$im = imagecreatefromstring($data);
$width = imagesx( $im );
$height = imagesy( $im );
$photoname = $userId . time();
$thumbname = $photoname."thm";
imagejpeg($im, "data/".$photoname.".jpg");
$tmp_img = imagecreatetruecolor( 150, 150 );
imagecopyresized( $tmp_img, $im, 0, 0, 0, 0, 150, 150, $width, $height );
imagejpeg( $tmp_img, "data/".$thumbname.".jpg" );
imagedestroy($tmp_img);
imagedestroy($im);
$url = "http://ferbook.duckdns.org/ferbook/photos/upload/data/".$photoname.".jpg";

// Create new post with image
if(isset($_POST['message'])) {
	$upload = $db->prepare("INSERT INTO post(sender, recipient, url, message) VALUES (?,?,?,?)");
	$upload->bindParam(4, $_POST["message"]);
} else {
	$upload = $db->prepare("INSERT INTO post(sender, recipient, url) VALUES (?,?,?)");
}
$upload->bindParam(1, $userId);
$upload->bindParam(2, $userId2);
$upload->bindParam(3, $url);
$upload->setFetchMode(PDO::FETCH_OBJ);
$upload->execute();

// Get ID from new post
$postId = $db->prepare("SELECT id FROM post ORDER BY id DESC");
$postId->setFetchMode(PDO::FETCH_OBJ);
$postId->execute();
$postId=$postId->fetch()->id;

// Upload image to album
$upload = $db->prepare("INSERT INTO albumhasimage(idAlbum, idImage) VALUES (?,?)");
$upload->bindParam(1, $albumId);
$upload->bindParam(2, $postId);
$upload->setFetchMode(PDO::FETCH_OBJ);
$upload->execute();

$output = array(
    "url" => $url
);
$response["data"] = $output;
echo str_replace("\\", "", json_encode($response));

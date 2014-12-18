<?php
// prevent the server from timing out
set_time_limit(0);

// include the web sockets server script (the server is started at the far bottom of this file)
require 'class.PHPWebSocket.php';

// when a client sends data to the server
function wsOnMessage($clientID, $message, $messageLength, $binary) {
    echo "primio poruku". $message;
    global $Server;
    global $IDs;
    $ip = long2ip( $Server->wsClients[$clientID][6] );

    $params = json_decode($message);

    if($params->type == "welcome") {
        if(!isset($IDs[$params->id])) {
            $IDs[$params->id] = $clientID;
        }
    } else {
        if(isset($IDs[$params->recipient])){
            $params->msg = [
                "message" => $params->message,
                "senderId" => $params->sender,
                "flag" => 1,
                "timestamp" => time()
            ];
            $Server->wsSend($IDs[$params->recipient],json_encode($params));
        }
    }
}

// when a client connects
function wsOnOpen($clientID)
{
    echo "otvorena konekcija";
	global $Server;
	$ip = long2ip( $Server->wsClients[$clientID][6] );

	$Server->log( "$ip ($clientID) has connected." );

}

// when a client closes or lost connection
function wsOnClose($clientID, $status) {
    echo "zatvorena konekcija";
	global $Server;
	$ip = long2ip( $Server->wsClients[$clientID][6] );

	$Server->log( "$ip ($clientID) has disconnected." );

}

// start the server
$Server = new PHPWebSocket();
$Server->bind('message', 'wsOnMessage');
$Server->bind('open', 'wsOnOpen');
$Server->bind('close', 'wsOnClose');
// for other computers to connect, you will probably need to change this to your LAN IP or external IP,
// alternatively use: gethostbyaddr(gethostbyname($_SERVER['SERVER_NAME']))
$Server->wsStartServer('127.0.0.1', 9000);

?>
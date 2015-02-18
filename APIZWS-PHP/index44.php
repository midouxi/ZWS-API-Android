<?php 
$req=urldecode($_POST['requestcode']);
require 'zws_client.php';
$zws = new ZWS_Client('1.0'); // replace 1.0 by the version of the API you want to use.
$zws->setClientId('CV5mavPrhEAfWD1IgfVeZscCAYu6X');
$zws->setSecretKey('JgNKM1XMuR6N3z70PZOso8ym28izRkzY3QMa9BC7FJ3QijJA4WlBQ4aOrwqV');
// Exchanging the request code with an access token.
$request = $zws->get(array(
    'endPoint' => '/request_codes/'.$req.'/tokens'
    )
);
if($request->http_code != 200) {
    // Handle error here. $request->description contains a description of the error.
    echo '<pre>';
    echo "Echec de l'échange d'un request code avec un access token.\n";
    echo "Description de l'erreur : " . $request->description . "\n";
    echo "Code retour : " . $request->http_code;
    echo '</pre>';
    exit();
} 
$user = $request;
// Requesting endpoint /users/{id} to get user information.
$userInfos = $zws->get(array(
    'endPoint' => "/users/".$user->user_id,
    'params' => array(
        'access_token' => $user->access_token
        )
    )
);
echo $userInfos->username;
?>




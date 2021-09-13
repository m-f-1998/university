<?php

/**
* Open Source Code From: https://www.simplifiedios.net/ios-registration-form-example/
*
* User: Belal
* Date Accessed: 05/01/2019
*
*/

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET');

require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/userFunctions/dbOperation.php';
$response = array ();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    if (isset ($_POST['email'])) {

        $db = new DbOperation ();

        if ($db->resetPass ($db->noHTML($_POST['email']))) {

            $response['error'] = false;
            $response['message'] = "Password Reset Link Sent To E-Mail If User Exists";

        }

    } else {

        $response['error'] = true;
        $response['message'] = "E-Mail Is Not Set";

    }

} else {

    $response['error'] = true;
    $response['message'] = "Request Not Allowed";

}

echo json_encode ($response);

?>

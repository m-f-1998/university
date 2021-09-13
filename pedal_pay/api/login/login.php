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

    if (isset($_POST['email']) && isset($_POST['password'])) {

        $db = new DbOperation();

        if ($db->userLogin($db->noHTML($_POST['email']), $db->noHTML($_POST['password']))) {

            $response['error'] = false;
            $response['message'] = $_POST['email'];
            $response['userDetails'] = $db->userDetail($db->getID($db->noHTML($_POST['email'])));

            if ($db->isAdmin($db->noHTML($_POST['email'])) == '1') {

                $response['admin'] = true;

            } else {

                $response['admin'] = false;

            }

        } else {

            $response['error'] = true;
            $response['message'] = "Invalid E-Mail Or Password";

        }

    } else {

        $response['error'] = true;
        $response['message'] = "All Fields Are Required To Log In";

    }

} else {

    $response['error'] = true;
    $response['message'] = "Request Not Allowed";

}

echo json_encode($response);

?>

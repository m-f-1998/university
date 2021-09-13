<?php

/**
* Structure Is Standard Open Source. Structure Sourced From And Changed/Added To: https://www.simplifiedios.net/ios-registration-form-example/
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
            $response['message'] = $db->userBookings($db->getID($db->noHTML($_POST['email'])));

        } else {

            $response['error'] = true;
            $response['message'] = "User Login Failed";

        }

    } else {

        $response['error'] = true;
        $response['message'] = "All Fields are Required to Access User Bookings";

    }

} else {

    $response['error'] = true;
    $response['message'] = "Request Not Allowed";

}

echo json_encode($response);

?>

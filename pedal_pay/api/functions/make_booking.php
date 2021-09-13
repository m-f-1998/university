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

    if (isset($_POST['email']) && isset($_POST['password']) && isset($_POST['bookStart']) && isset($_POST['bookEnd']) && isset($_POST['hub']) && isset($_POST['bikeType'])) {

        $db = new dbOperation();

        if ($db->userLogin($db->noHTML($_POST['email']), $db->noHTML($_POST['password']))) {

            $result = $db->makeBooking($db->noHTML($_POST['email']), $db->noHTML($_POST['bookStart']), $db->noHTML($_POST['bookEnd']), $db->noHTML($_POST['hub']), $db->noHTML($_POST['bikeType']));

            if ($result == -1) {

                $response['error'] = true;
                $response['message'] = "No Bikes Available";
                $response['code'] = -1;

            } else if ($result == -2) {

                $response['error'] = true;
                $response['message'] = "Booking Unsuccessful";
                $response['code'] = -1;

            } else {

                $response['error'] = false;
                $response['message'] = "Booking Successful";
                $response['code'] = $result;

            }

        } else {

            $response['error'] = true;
            $response['message'] = "User Login Failed";

        }

    } else {

        $response['error'] = true;
        $response['message'] = "All Fields Are Required To Make A Booking";

    }
} else {

    $response['error'] = true;
    $response['errorMessage'] = "Request Not Allowed";

}

echo json_encode($response);

?>

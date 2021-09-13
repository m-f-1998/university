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

require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/adminFunctions/dbAdminOperation.php';
require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/userFunctions/dbOperation.php';

$response = array ();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    if (isset($_POST['email']) && isset($_POST['password'])) {

        $db = new DbOperation();
        $dbAdmin = new dbAdminOperation();

        if ($db->userLogin($db->noHTML($_POST['email']), $db->noHTML($_POST['password']))) {

            if ($db->isAdmin($db->noHTML($_POST['email']))) {

                $result = $dbAdmin->locOfBike($db->noHTML($_POST['bid']));
                
                if ($result == 1) {
                    
                    $response['error'] = true;
                    $response['message'] = "No Bike Updates Available Or Booking Has Not Yet Started";
                
                } else if ($result == 2) {
                    
                    $response['error'] = true;
                    $response['message'] = "Bike Not Registered In System";
                    
                } else {
                    
                    $response['error'] = false;
                    $response['message'] = $result;
                    
                }
                
            } else {
                
                $response['error'] = true;
                $response['message'] = "User Must Be Admin";
                
            }

        } else {

            $response['error'] = true;
            $response['message'] = "User Login Failed";

        }

    } else {

        $response['error'] = true;
        $response['message'] = "All Fields are Required to Access Locations of Bike";

    }

} else {

    $response['error'] = true;
    $response['message'] = "Request Not Allowed";

}

echo json_encode($response);

?>

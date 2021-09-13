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
require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/adminFunctions/dbAdminOperation.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    if (isset($_POST['email']) && isset($_POST['password'])) {

        $db = new DbOperation();
        $dbAdmin = new DbAdminOperation();

        if ($db->isAdmin($db->noHTML($_POST['adminEmail'])) && $db->userLogin($db->noHTML($_POST['adminEmail']), $db->noHTML($_POST['adminPassword']))) {

            if ($_POST['gender'] != 'm' || $_POST['gender'] != 'f' || $_POST['gender'] != null) {
                
                $response['error'] = true;
                $response['message'] = "Gender Unkown";
                
            } else {
            
                $result = $dbAdmin->createAdminUser($db->noHTML($_POST['email']), $db->noHTML($_POST['password']), $db->noHTML($_POST['name']), $db->noHTML($_POST['gender']), $db->noHTML($_POST['mobile']), $db->noHTML($_POST['dob']), $db->noHTML($_POST['addressone']), $db->noHTML($_POST['addresstwo']), $db->noHTML($_POST['city']), $db->noHTML($_POST['zip']));

                if ($result == 0) {

                    $response['error'] = false;
                    $response['message'] = "Account Created Successfully";

                } elseif ($result == 1) {

                    $response['error'] = true;
                    $response['message'] = "Some Error Occurred";

                } elseif ($result == 2) {
                    
                    $response['error'] = true;
                    $response['message'] = "Account Already Exists";
                    
                }
            
            }

        } else {

            $response['error'] = true;
            $response['message'] = "Admin User Required To Verify Register";

        }

    } else {

        $response['error'] = true;
        $response['message'] = "All Fields Are Required To Register Admin";

    }

} else {

    $response['error'] = true;
    $response['message'] = "Invalid Request";

}

echo json_encode($response);

?>

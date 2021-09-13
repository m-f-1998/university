<?php

/**
* Open Source Code From: https://www.simplifiedios.net/ios-registration-form-example/
*
* User: Belal
* Date Accessed: 05/01/2019
*
*/

require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/userFunctions/dbOperation.php';

$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    if (isset($_POST['email']) && isset($_POST['password'])) {

        if ($_POST['gender'] == 'm' || $_POST['gender'] == 'f' || $_POST['gender'] == NULL) {

            $db = new DbOperation();
            $result = $db->createUser($db->noHTML($_POST['email']), $db->noHTML($_POST['password']), $db->noHTML($_POST['name']), $db->noHTML($_POST['gender']), $db->noHTML($_POST['mobile']), $db->noHTML($_POST['dob']), $db->noHTML($_POST['addressone']), $db->noHTML($_POST['addresstwo']), $db->noHTML($_POST['city']), $db->noHTML($_POST['zip']));
            
            if ($result == 0) {
                
                $response['error'] = false;
                $response['message'] = "Account Created Successfully";
                
            } elseif ($result == 1) {
                
                $response['error'] = true;
                $response['message'] = "Some Error Occurred";
                
            }  elseif ($result == 2) {
                
                $response['error'] = true;
                $response['message'] = "Account Already Exists";
                
            }

        } else {
        
            $response['error'] = true;
            $response['message'] = "Gender Unkown";
        
        }

    } else {

        $response['error'] = true;
        $response['message'] = "All Fields Are Required To Register";

    }

} else {

    $response['error'] = true;
    $response['message'] = "Invalid Request";

}

echo json_encode($response);

?>

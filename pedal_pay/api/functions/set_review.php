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

    if (isset($_POST['email']) && isset($_POST['password']) && isset($_POST['rating']) && isset($_POST['text'])) {

        $db = new DbOperation();

        if ($db->userLogin($db->noHTML($_POST['email']), $db->noHTML($_POST['password']))) {

            $result = $db->setReview($db->getID($db->noHTML($_POST['email'])), $db->noHTML($_POST['rating']), $db->noHTML($_POST['text']));
            
            if ($result == false) {
                
                $response['error'] = true;
                $response['message'] = "Unkown Error Occured In Storing Review";
                
            } else {
             
                $response['error'] = false;
                $response['message'] = $result;
                
            }

        } else {

            $response['error'] = true;
            $response['message'] = "User Login Failed";

        }

    } else {

        $response['error'] = true;
        $response['message'] = "All Fields Are Required To Set Review";

    }

} else {

    $response['error'] = true;
    $response['message'] = "Request Not Allowed";

}

echo json_encode($response);

?>

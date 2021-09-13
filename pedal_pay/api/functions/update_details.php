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

  if (isset($_POST['oldEmail']) && isset($_POST['password']) && isset($_POST['newEmail']) && isset($_POST['name']) && isset($_POST['dob']) && isset($_POST['addressOne']) && isset($_POST['mobile']) && isset($_POST['addressTwo']) && isset($_POST['city']) && isset($_POST['zip'])) {

      $db = new DbOperation();

      if ($db->userLogin($db->noHTML($_POST['oldEmail']), $db->noHTML($_POST['password']))) {

          if ($db->updateUser(
                              $db->noHTML($_POST['oldEmail']),
                              $db->noHTML($_POST['newEmail']),
                              $db->noHTML($_POST['name']),
                              $db->noHTML($_POST['dob']),
                              $db->noHTML($_POST['mobile']),
                              $db->noHTML($_POST['addressOne']),
                              $db->noHTML($_POST['addressTwo']),
                              $db->noHTML($_POST['city']),
                              $db->noHTML($_POST['zip']))) {

              $response['error'] = false;
              $response['message'] = "User Update Successful";

          } else {

              $response['error'] = true;
              $response['message'] = "Some Unkown Error Occured";

          }

        } else {

            $response['error'] = true;
            $response['message'] = "User Login Failed";

        }

    } else {

        $response['error'] = true;
        $response['message'] = "All Fields Are Required To Update A User's Details";

    }
} else {

    $response['error'] = true;
    $response['message'] = "Request Not Allowed";

}

echo json_encode($response);

?>

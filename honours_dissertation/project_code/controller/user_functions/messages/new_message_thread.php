<?php

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET');

require_once $_SERVER['DOCUMENT_ROOT'].'/dissertation/userFunctions/messages/dbOperation.php';

$response = array ();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    if (isset($_POST['session_id']) && isset($_POST['email_recipient'])) {

        $db = new DbOperation();
        $res = $db->newMessageThread($db->noHTML($_POST['session_id']), $db->noHTML($_POST['email_recipient']));

        if ($res === -1) {

          $response['error'] = true;
          $response['message'] = "Login Credentials Invalid - You Cannot Have More Than 1 Session Open At The Same Time";

        } else if ($res === -2) {

          $response['error'] = true;
          $response['message'] = 'Message Could Not Be Created';

        } else if ($res === -3) {

          $response['error'] = true;
          $response['message'] = 'Recipient Does Not Have An Account';

        } else if ($res === -4) {

          $response['error'] = true;
          $response['message'] = 'Message Thread Already Exists';

        } else {

          $response['error'] = false;
          $response['message'] = $res;

        }

    } else {

        $response['error'] = true;
        $response['message'] = "Request Invalid";

    }

} else {

    $response['error'] = true;
    $response['message'] = "Request Not Allowed";

}

echo json_encode($response);

?>
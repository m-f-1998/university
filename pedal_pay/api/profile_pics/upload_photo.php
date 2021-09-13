<?php

/**
* Structure Is Standard Open Source. Structure Sourced From And Changed/Added To: https://www.simplifiedios.net/ios-registration-form-example/
*
* Created by PhpStorm.
* User: Belal
* Date Accessed: 05/01/2019
*
*/

require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/userFunctions/dbOperation.php';

$response = array () ;

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    if (isset ($_POST['email']) && isset ($_POST['picture']) && isset ($_POST['password'])) {

        $db = new DbOperation();

        if ($db->userLogin($db->noHTML($_POST['email']), $db->noHTML($_POST['password']))) {

            $picture = $_POST['picture'];
            $picture = str_replace (" ", "+", $picture);

            $save_directory = "./".$db->getID($db->noHTML($_POST['email'])).".jpg";

            $ifp = fopen($save_directory, "wb");
            $data = explode(',', $picture);
            fwrite($ifp, base64_decode($data[1]));
            fclose($ifp);

            $response['error'] = false;
            $response['message'] = "Profile Picture Upload Successful";

        } else {

            $response['error'] = true;
            $response['message'] = "User Login Required";

        }

    } else {

        $response['error'] = true;
        $response['message'] = "All Fields are Required to Create Gallery";

    }

} else {

    $response['error'] = true;
    $response['message'] = "Request Not Allowed";

}

echo json_encode ($response);

?>

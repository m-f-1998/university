<?php

/**
* Open Source Code From: https://www.simplifiedios.net/ios-registration-form-example/
*
* User: Belal
* Date Accessed: 05/01/2019
*
*/

class dbConnect {

    private $conn;

    function __construct () { }

    function connect () {

        require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/dbConnection/constants.php';

        $this->conn = new mysqli(DB_HOST, DB_USERNAME, DB_PASSWORD, DB_NAME);

        if (mysqli_connect_errno ()) {

            $response = array ();

            $response['error'] = true;
            $response['user'] = "Failed To Connect To MySQL: " . mysqli_connect_error();

            echo json_encode($response);

        }

        return $this->conn;

    }

}

?>

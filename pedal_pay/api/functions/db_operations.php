<?php

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, GET');

/**
* Structure Is Standard Open Source. Structure Sourced From And Changed/Added To: https://www.simplifiedios.net/ios-registration-form-example/
* Database Construction and User Login/Registration Open Source Code From: : https://www.simplifiedios.net/ios-registration-form-example/
*
* User: Belal
* Date Accessed: 05/01/2019
*
*/

require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/adminFunctions/dbAdminOperation.php';

class dbOperation {

    private $conn;

    function __construct () {

        require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/dbConnection/constants.php';
        require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/dbConnection/dbConnect.php';

        $db = new DbConnect ();
        $this->conn = $db->connect ();

    }

    public function noHTML ($input, $encoding = 'UTF-8') {
        return htmlspecialchars($input, ENT_QUOTES | ENT_HTML5, $encoding);
    }

    public function userLogin ($email, $pass) {

        $stmt = $this->conn->prepare ('SELECT `password` FROM `users` WHERE `email` = ?;');
        $stmt->bind_param ('s', $email);
        $stmt->execute ();
        $stmt->bind_result ($password);
        $stmt->fetch();
        return password_verify($pass, $password);

    }
    
    public function userDetail ($id) {
        
        $stmt = $this->conn->prepare('SELECT `email`, `name`, `dob`, `mobile`, `addressone`, `addresstwo`, `city`, `zip`, `gender` FROM `users` WHERE `id`=?;');
        $stmt->bind_param ('i', $id);
        $stmt->execute ();
        $result = $stmt->get_result();
        $res = array();
        
        while ($data = $result->fetch_assoc()) {
            
            $data['id'] = $id;
            $data['email'] = utf8_encode($data['email']);
            $data['name'] = utf8_encode($data['name']);
            $data['addressone'] = utf8_encode($data['addressone']);
            $data['addresstwo'] = utf8_encode($data['addresstwo']);
            $data['gender'] = utf8_encode($data['gender']);
            $data['dob'] = utf8_encode($data['dob']);
            $data['city'] = utf8_encode($data['city']);
            $data['zip'] = utf8_encode($data['zip']);
            $data['mobile'] = utf8_encode($data['mobile']);
            array_push($res, $data);
            
        }
        
        return $res;
        
    }
    
    public function updateQuery ($oldEmail, $data, $queryColumn) {
        
        $stmt = $this->conn->prepare ('UPDATE `users` SET `'.$queryColumn.'` = ?  WHERE `email` = ?');
        $stmt->bind_param ('ss', $data, $oldEmail);
        
        if ($stmt->execute()) {
            
            return true;
            
        } else {
            
            return false;
            
        }
        
    }
    
    public function updateUser ($oldEmail, $newEmail, $name, $dob, $mobile, $addressOne, $addressTwo, $city, $zip) {
        
        $flag = false;
        $email = $oldEmail;
        
        if (!empty($newEmail)) {
            $flag = $this->updateQuery($oldEmail, $newEmail, 'email');
            if ($flag) {
                $email = $newEmail;
            }
        }
        if (!empty($name)) {
            $flag = $this->updateQuery($email, $name, 'name');
        }
        if (!empty($dob)) {
            $flag = $this->updateQuery($email, $dob, 'dob');
        }
        if (!empty($mobile)) {
            $flag = $this->updateQuery($email, $mobile, 'mobile');
        }
        if (!empty($addressOne)) {
            $flag = $this->updateQuery($email, $addressOne, 'addressone');
        }
        if (!empty($addressTwo)) {
            $flag = $this->updateQuery($email, $addressTwo, 'addressTwo');
        }
        if (!empty($city)) {
            $flag = $this->updateQuery($email, $city, 'city');
        }
        if (!empty($zip)) {
            $flag = $this->updateQuery($email, $zip, 'zip');
        }
        
        return $flag;
    }

    public function getID ($email) {

        $stmt = $this->conn->prepare ('SELECT `id` FROM `users` WHERE `email` = ?;');
        $stmt->bind_param ('s', $email);
        $stmt->execute ();
        $stmt->bind_result ($id);
        $stmt->fetch();
        return $id;

    }

    public function isAdmin ($email, $bikeStand) {

        $stmt = $this->conn->prepare ('SELECT `isAdmin` FROM `users` WHERE `email` = ?;');
        $stmt->bind_param ('s', $email);
        $stmt->execute ();
        $stmt->bind_result ($admin);
        $stmt->fetch();
        return $admin;

    }
    
    public function userBookings ($pid) {
        
        $stmt = $this->conn->prepare('SELECT `id`, `pid`, `bid`, `lid`, `bookstart`, `bookend`, `returned`, `processing`, `code`, `distance` FROM `booking` WHERE `pid` = ?;');
        $stmt->bind_param ('s', $pid);
        $stmt->execute ();
        $result = $stmt->get_result();
        $res = array();
        
        while ($data = $result->fetch_assoc()) {
            
            array_push($res, $data);
            
        }
        
        return $res;
        
    }

    public function unlock ($id, $location, $bikeStand) {

        $stmt = $this->conn->query ('SELECT `code` FROM `booking` WHERE `pid` = "'.$id.'" AND `lid` = (SELECT `id` FROM `locations` WHERE `city` = "'.$location.'" AND `hub` = "'.$bikeStand.'") AND (`bookstart` > NOW() - INTERVAL 70 MINUTE) AND (`bookstart` < NOW() - INTERVAL 50 MINUTE);');
        $res = array();
        
        while ($data = $stmt->fetch_assoc()) {
            
            array_push($res, $data);
            
        }
        
        return $res;

    }
    
    public function getReview () {
        
        $stmt = $this->conn->query ('SELECT * FROM `review` WHERE 1;');
        $res = array();
        
        while ($data = $stmt->fetch_assoc()) {
            
            array_push($res, $data);
            
        }
        
        return $res;
        
    }
    
    public function setReview ($id, $rating, $text) {
        
        $stmt = $this->conn->prepare ('INSERT INTO `review`(`pid`, `rating`, `text`, `time`) VALUES (?,?,?,timestamp(CONVERT_TZ(NOW(), @@global.time_zone, "+00:00")));');

        $stmt->bind_param ('iis', $id, $rating, $text);
        $stmt->execute ();
        
        if ($stmt->execute ()) {

            return "Review Stored";
            
        } else {
            
            return false;
            
        }
        
    }

    public function createUser ($email, $pass, $name, $gender, $mobile, $dob, $addressone, $addresstwo, $city, $zip) {

        if (!$this->isUserExist ($email, $pass)) {

            $options = ['cost' => 12,];
            $password = password_hash($pass, PASSWORD_BCRYPT, $options);
            $stmt = $this->conn->prepare ('INSERT INTO `users` (`email`, `password`, `name`, `gender`, `mobile`, `dob`, `addressone`, `addresstwo`, `city`, `zip`, `isadmin`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, false);');
            $stmt->bind_param ('ssssssssss', $email, $password, $name, $gender, $mobile, $dob, $addressone, $addresstwo, $city, $zip);

            if ($stmt->execute ()) {

                return 0;

            } else {

                return 1;

            }

        } else {

            return 2;

        }

    }

    public function checkoutAccessory ($hub, $item) {

        $stmt = $this->conn->prepare ('SELECT `id` FROM `equipment` WHERE `item` = ? AND `hub` = (SELECT `id` FROM `locations` WHERE `hub` = ?) AND `returned` = 1;');
        $stmt->bind_param ('ss', $item, $hub);
        $stmt->execute ();
        $stmt->store_result();

        if ($stmt->num_rows > 0) {

            $stmt->bind_result ($id);
            
            $stmt->fetch();
            $stmt = $this->conn->query ('UPDATE `equipment` SET `returned` = 0 WHERE `id` = '.$id);

            return $id;

        } else {

            return false;

        }

    }
    
    public function hubLatLong ($id) {
        
        $stmt = $this->conn->prepare ('SELECT `id`, `hub`, `lat`, `lng` FROM `locations` WHERE `id` = ?;');
        $stmt->bind_param ('s', $id);
        $stmt->execute ();
        $stmt->execute ();
        $result = $stmt->get_result();
        $res = array();
        
        while ($data = $result->fetch_assoc()) {
            
            array_push($res, $data);
            
        }
        
        return $res;
        
    }

    public function checkinAccessory ($itemID) {

        $stmt = $this->conn->prepare ('SELECT `id` FROM `equipment` WHERE `id` = ? AND `returned` = 0');
        $stmt->bind_param ('s', $itemID);
        $stmt->execute ();
        $stmt->store_result();

        if ($stmt->num_rows > 0) {

            $stmt = $this->conn->query ('UPDATE `equipment` SET `returned` = 1 WHERE `id` = '.$itemID);
            return "Accessory Checked In";

        } else {

            return false;

        }

    }


    public function removeBooking ($persID, $bookID) {
        
        $distance = -1;
        $stmt = $this->conn->prepare ('SELECT `bookstart`, `bookend`, `bid` FROM `booking` WHERE `id`= ?;');
        $stmt->bind_param ('s', $bookID);
        $stmt->execute ();
        $result = $stmt->get_result();
        $res = array();
        
        while ($data = $result->fetch_assoc()) {
            array_push($res, $data);
        }
        
        $dbAdmin = new dbAdminOperation();
        $result = $dbAdmin->locOfBike($res[0]['bid']);
        if ($result == 1) {
            
            $distance = 0;
            
        } else if ($result == 2) {
            
            return "Bike Not Registered In System";
            
        } else {
            $distance = ((float) rand() / (float) getrandmax()) * 10;
        }
        
        $stmt = $this->conn->prepare ('SELECT `id` FROM `booking` WHERE `id` = ? AND `pid` = ? AND `returned` = 0');
        $stmt->bind_param ('ss', $bookID, $persID);
        $stmt->execute ();
        $stmt->store_result();
        
        if ($stmt->num_rows > 0) {
            
            $stmt = $this->conn->query ('UPDATE `booking` SET `processing` = 1, `distance` = `distance` + '.$distance.' WHERE `id` = '.$bookID);
            $stmt = $this->conn->query ('CREATE EVENT updateWait ON SCHEDULE AT CURRENT_TIMESTAMP DO UPDATE `booking` SET `returned` = 1 WHERE `id` = '.$bookID);
            return "Booking Removed";
            
        } else {
            
            return false;
            
        }
        
    }
    
    public function respToDist($jsonResp)
    {
        if (sizeof($jsonResp) < 2) {
            return 0;
        }
        if (sizeof($jsonResp) == 2) {
            return $this->dis($jsonResp[0]['lat'], $jsonResp[0]['lng'], $jsonResp[1]['lat'], $jsonResp[1]['lng']);
        }
        $d = 0;
        $i = 0;
        $last = $jsonResp[0];
        for ($i = 1; $i < sizeof($jsonResp); $i++)
        {
            $d = $d + $this->dis(last['lat'], last['lng'], jsonResp[$i]['lat'], jsonResp[$i]['lng']);
        }
        return $d;
    }
    
    public function dis($lat1, $lng1, $lat2, $lng2)
    {
        $p = 0.017453292519943295;
        return 7918 * Math.asin(Math.sqrt(0.5 - cos(($lat2 - $lat1) * $p)/2 + cos($lat1 * $p) * cos($lat2 * $p) * (1 - cos(($lng2 - $lng1) * $p))/2));
    }
    
    public function confirmationEmail ($email, $code) {

        if ($this->isUserExist ($email)) {

            $subject = 'Confirmation Of Booking => Pedal Pay';
            $headers = 'MIME-Version: 1.0' . "\r\n";
            $headers .= 'Content-type:text/html;charset=UTF-8' . "\r\n";
            $headers .= 'From: PedalPay<noreply@pedalpay.com>' . "\r\n";
            $mailContent = 'Your recent reservation of a bike with PedalPay Was successful. Your confirmation code is: '.$code.'. Use this to unlock your bike at your chosen bike stand. If you have any queries or any problems contact us via our webiste or app. <br/><br/>Regards,<br/>Pedal Pay';

            mail($email,$subject,$mailContent,$headers);

            return true;

        } else {

            return false;

        }

    }

    public function makeBooking ($email, $startTime, $endTime, $hub, $bikeType) {

        $code = rand();
        $this->confirmationEmail($email, $code);

        $stmt = $this->conn->prepare ('INSERT IGNORE INTO `booking` (pid, bid, lid, bookstart, bookend, returned, processing, code, distance) VALUES ((SELECT `id` FROM `users` WHERE `email`= ?), (SELECT `id` FROM `bikes` b WHERE `biketype` = ? AND `hub` = (SELECT id FROM locations WHERE hub=?) AND NOT EXISTS (SELECT `bid` FROM (SELECT * FROM `booking`) AS c WHERE b.`id` = c.`bid`) LIMIT 0,1), (SELECT id FROM locations WHERE hub=?), ?, ?, FALSE, FALSE, ?, 0);');

        $stmt->bind_param ('ssssssi', $email, $bikeType, $hub, $hub, $startTime, $endTime, $code);

        if ($stmt->execute ()) {

            $stmt = $this->conn->query ('SELECT `id` FROM `booking` WHERE `code` = '.$code.';');
            $res = array();
            $counter = 0;

            while ($data = $stmt->fetch_assoc()) {

                $counter = $counter + 1;
                array_push($res, $data);

            }

            if ($counter > 0) {

                return $res[0]["id"];

            } else {

                return -1;

            }

        } else {

            return -2;

        }

    }

    public function resetPass ($email) {

        if ($this->isUserExist ($email)) {

            $code = md5(uniqid(mt_rand()));
            $uniqueLink = 'http://www.matthewfrankland.co.uk/pedalPay/resetPass/passwordRecovery.php?code='.$code.'&email='.$email;

            $subject = 'Password Update Request => Pedal Pay';
            $headers = 'MIME-Version: 1.0' . "\r\n";
            $headers .= 'Content-type:text/html;charset=UTF-8' . "\r\n";
            $headers .= 'From: PedalPay<noreply@pedalpay.com>' . "\r\n";
            $mailContent = 'Recently a request was submitted to reset a password for your account. If this was a mistake, please destroy this email without any further action.<br/>To reset your password, visit the following link: <a href="'.$uniqueLink.'">'.$uniqueLink.'</a><br/><br/>Regards,<br/>Pedal Pay';

            $stmt = $this->conn->prepare('INSERT INTO `accountreset` VALUES (?, UNIX_TIMESTAMP());');
            $stmt->bind_param('s', $code);
            $stmt->execute();
            mail($email,$subject,$mailContent,$headers);

            return true;

        } else {

            return false;

        }

    }

    public function getLocations () {

        $stmt = $this->conn->query ('SELECT `id`, `hub`, `lat`, `lng`, `city` FROM `locations`;');
        $res = array();

        while ($data = $stmt->fetch_assoc()) {
            
            array_push($res, $data);

        }

        return $res;

    }


    private function isUserExist ($email) {

        $stmt = $this->conn->prepare('SELECT `id` FROM `users` WHERE `email` = ?;');
        $stmt->bind_param('s', $email);
        $stmt->execute();
        $stmt->store_result();
        return $stmt->num_rows > 0;

    }

}

?>

<?php

/**
* Structure Is Standard Open Source. Structure Sourced From And Changed/Added To: https://www.simplifiedios.net/ios-registration-form-example/
* Code For Database Construction Taken From: https://www.simplifiedios.net/ios-registration-form-example/
*
* User: Belal
* Date Accessed: 05/01/2019
*
*/

    header('Access-Control-Allow-Origin: *');
    header('Access-Control-Allow-Methods: POST, GET');

    class dbAdminOperation {

        private $conn;

        function __construct () {

            require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/dbConnection/constants.php';
            require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/dbConnection/dbConnect.php';

            $db = new DbConnect ();
            $this->conn = $db->connect ();

        }

        public function createAdminUser ($email, $pass, $name, $gender, $mobile, $dob, $addressone, $addresstwo, $city, $zip) {

            if (!$this->isUserExist ($email, $pass)) {

                $options = ['cost' => 12,];
                $password = password_hash($pass, PASSWORD_BCRYPT, $options);
                $stmt = $this->conn->prepare ('INSERT INTO `users` (`email`, `password`, `name`, `gender`, `mobile`, `dob`, `addressone`, `addresstwo`, `city`, `zip`, `isadmin`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true);');
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
        
        public function updateBikeStatus($bid, $status) {
            
            $stmt = $this->conn->prepare ('UPDATE `bikes` SET `damage`=? WHERE `id`=?;');
            $stmt->bind_param ('ss', $status, $bid);
            
            if ($stmt->execute ()) {
                
                return true;
                
            } else {
                
                return false;
                
            }
            
        }
        
        public function bikeStatus($bid) {
            
            $stmt = $this->conn->prepare ('SELECT `damage` FROM `bikes` WHERE `id`=?;');
            $stmt->bind_param ('s', $bid);
            
            if ($stmt->execute ()) {
                
                $stmt->bind_result ($damage);
                $stmt->fetch();
                return $damage;
                
            } else {
                
                return false;
                
            }
            
        }
        
        public function bikeUpdates ($pid) {
            
            $stmt = $this->conn->prepare('SELECT * FROM `bikeupdates` WHERE `bid`=?;');
            $stmt->bind_param ('s', $pid);
            $stmt->execute ();
            $result = $stmt->get_result();
            $res = array();
            
            while ($data = $result->fetch_assoc()) {

                array_push($res, $data);
                
            }
            
            return $res;
            
        }

        public function userDetails () {

            $stmt = $this->conn->query('SELECT `email`, `name`, `dob`, `addressone`, `addresstwo`, `city`, `zip`, `gender`, `mobile` `isadmin` FROM `users`;');
            $res = array();

            while ($data = $stmt->fetch_assoc()) {
                
                $data['email'] = utf8_encode($data['email']);
                $data['name'] = utf8_encode($data['name']);
                $data['addressone'] = utf8_encode($data['addressone']);
                $data['addresstwo'] = utf8_encode($data['addresstwo']);
                $data['city'] = utf8_encode($data['city']);
                $data['gender'] = utf8_encode($data['gender']);
                $data['mobile'] = utf8_encode($data['mobile']);
                array_push($res, $data);
                
            }

            return $res;

        }
        

        public function allUserBookings () {

            $stmt = $this->conn->query('SELECT `pid`, `bid`, `lid`, `bookstart`, `bookend`, `returned`, `code`, `distance` FROM `booking` WHERE 1;');
            $res = array();

            while ($data = $stmt->fetch_assoc()) {

                array_push($res, $data);
            }

            return $res;

        }

        public function updateLocation ($lat, $long, $bid) {

            $stmt = $this->conn->prepare('INSERT INTO `bikeupdates`(`bid`, `lat`, `lng`, `time`) VALUES (?, ?, ?, CONVERT_TZ(NOW(), @@global.time_zone, "+00:00"));');
                $stmt->bind_param ('ddd', $bid, $lat, $long);

                if ($stmt->execute ()) {

                    return "Location Update Successful";

                } else {

                    return "Location Update Unsuccessful";

                }
            
        }

        public function locOfBike ($bid) {
            
            if (empty($bid)) {                
                $stmt = $this->conn->query('SELECT `id`, `bid`, `lat`, `lng`, `time` FROM `bikeupdates` WHERE DATE(`time`) = DATE(NOW()) AND `bid`='.$bid.';');
                $res = array();
                
                while ($data = $stmt->fetch_assoc()) {
                    
                    array_push($res, $data);
                    
                }
                                
                if (sizeof($res) > 0) {
                    
                    return $res;
                    
                } else {
                    
                    return 1;
                    
                }
            }
            
            $output;
            
            if ($stmt = $this->conn->prepare('SELECT * FROM `booking` WHERE `bid`= ? ')){
                $stmt->bind_param('s', $bid);
                $stmt->execute();
                $result = $stmt->get_result();
                
                while($row = $result->fetch_assoc()) {
                    $output[]= $row;
                }
                
            }
                
            $stmt = $this->conn->prepare('SELECT `id` FROM `bikes` WHERE `id` = ?;');
            $stmt->bind_param ('s', $bid);
            $stmt->execute ();
            $stmt->store_result();
                
            if ($stmt->num_rows > 0) {
                
                $stmt = $this->conn->prepare('SELECT `id`, `bid`, `lat`, `lng`, `time` FROM `bikeupdates` WHERE `bid` = ? AND DATE(`time`) = DATE(NOW());');
                $stmt->bind_param ('s', $bid);
                $stmt->execute ();
                $result = $stmt->get_result();
                $res = array();
                
                while ($data = $result->fetch_assoc()) {

                    array_push($res, $data);
                        
                }
                
                if (sizeof($res) > 0) {

                    return $res;
                    
                } else {
                    
                    return 1;
                    
                }
                    
            } else {
                    
                return 2;
                    
            }
                
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

<?php
	session_start();
	$errors = array();

	if (isset($_POST['register'])) {
		if (empty($_POST['email'])) {

			array_push($errors, "An email is required!");

		}

		if (empty($_POST['password'])) {

			array_push($errors, "A password is required!");

		}

		if ($_POST['password'] != $_POST['confirmPassword']) {

			array_push($errors, "The passwords do not match!");

		}


		if (count($errors) == 0) {
			require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/userFunctions/dbOperation.php';
	 
			if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    
    			if (isset($_POST['email']) && isset($_POST['password']) && isset($_POST['confirmPassword'])) {


        			$db = new DbOperation();
        			$result = $db->createUser($_POST['email'], $_POST['password']);
     
        			if ($result == 0) {
            
            			$_SESSION['email'] = $email;
						$_SESSION['success'] = "Login was successful";

						header('location: index.php');
            
        			} elseif ($result == 1) {
            
           				array_push($errors, "The account already exists");
            
        			}
        
    			}
    
			}

		}

	}

	if (isset($_POST['login'])) {

		require_once $_SERVER['DOCUMENT_ROOT'].'/pedalPay/userFunctions/dbOperation.php';

		if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    		if (isset($_POST['email']) && isset($_POST['password'])) {
        		$db = new DbOperation();
        
        		if ($db->userLogin($_POST['email'], $_POST['password'])) {
            		
                		$_SESSION['email'] = $_POST['email'];
						$_SESSION['success'] = "You are now logged in";
						
						header('location: index.php');
        		} else {

					if (empty($_POST['email'])) {

						array_push($errors, "An email is required!");

					} 

					if (empty($_POST['password'])) {
					
						array_push($errors, "A password is required!");
					
					} 

					if (count($errors) == 0) {

						array_push($errors, "The email/password you entered is wrong!");

					}

       			}
        
    		}

		}
	}

	if (isset($_GET['logout'])) {

		session_destroy();

		unset($_SESSION['email']);

		header('location: login.php');

	}	
?>

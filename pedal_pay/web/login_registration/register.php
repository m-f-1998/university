<?php include('server.php'); ?>
<!DOCTYPE html>
<html>
<head>
	<title>Admin registration page</title>
	<link rel = "stylesheet" type = "text/css" href = "style.css">
</head>
<body>
	<div class = "header">
		<h2> Register </h2>
	</div>

	<form method = "post" action = "register.php">
		<?php include('errors.php'); ?>
		<div class = "inputs">
			<label> Email </label>
			<input type = "text" name = "email">
		</div>

		<div class = "inputs">
			<label> Password </label>
			<input type = "password" name = "password">
		</div>

		<div class = "inputs">
			<label> Confirm Password </label>
			<input type = "password" name = "confirmPassword">
		</div>

		<div class = "inputs">
			<button type = "submit" name = "register" class = "btn"> Register </button>
		</div>

		<p>
			Already signed up? <a href = "login.php">Sign in </a> 
		</p>
	</form>
</body>
</html>

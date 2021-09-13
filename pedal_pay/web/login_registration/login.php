<?php include('server.php'); ?>
<!DOCTYPE html>
<html>
<head>
	<title>Admin login page</title>
	<link rel = "stylesheet" type = "text/css" href = "style.css">
</head>
<body>
	<div class = "header">
		<h2> Login </h2>
	</div>

	<form method = "post" action = "login.php">
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
			<button type = "submit" name = "login" class = "btn"> Login </button>
		</div>

		<p>
			Not signed up? <a href = "register.php">Sign up </a> 
		</p>
	</form>
</body>
</html>


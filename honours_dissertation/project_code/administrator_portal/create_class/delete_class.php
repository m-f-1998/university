<?php session_start(); ?>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="description" content="Administrator Portal - Study Planner">
  <meta name="viewport" content="width=device-width, initial-scale=1.0"></head>

  <link rel="stylesheet" href="style.css">
  <link rel="shortcut icon" type="image/png" href="../favicon.png"/>

  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
  <script src="createClass.js"></script>
  <script src="../scripts/optimisations.js"></script>
  <title>Administrator Portal</title>
</head>
<body>
  <div id="form_wrapper">
    <div id="form_structure">
      <img id="logo" src="../favicon.png" width="150" height="150">
      <div class="topnav" id="nav">
        <a class="active" href="./a>
        <a href="./a>
        <a href="./a>
        <a href="javascript:void(0);" class="icon" onclick="mobileBar()">
          <i class="fa fa-bars"></i>
        </a>
      </div>
      <?php

        if (include('../check-validity.php')) {

          echo ('<script>window.location.replace("./script>');

        } else {

          /* Construct HTML */

          $string = '<a href="../logout.php" style="text-align: center;">Log Out</a>
          <i>Are you sure you want to delete this class?<br />All students, calendar events and files will be delete if associated with this class</i>
          <a style="color:red;text-align:center;" href="#delete" onclick="deleteClass(event, `'.$_GET['id'].'`)">Continue</a>';

          echo ($string);

        }
       ?>
    </div>
  </div>
</body>
</html>

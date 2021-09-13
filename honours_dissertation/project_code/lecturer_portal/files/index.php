<?php session_start(); ?>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="description" content="Lecturer Portal - Study Planner">
  <meta name="viewport" content="width=device-width, initial-scale=1.0"></head>
  <link rel="stylesheet" href="style.css">
  <link rel="shortcut icon" type="image/png" href="../favicon.png"/>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
  <script src="./files.js"></script>
  <title>Lecturer Portal</title>
</head>
<body>
  <div id="form_wrapper">
    <div id="form_structure">
      <img src="../favicon.png" width="150" height="150">
      <div class="topnav" id="nav">
        <a class="active" href="./a>
        <a href="./a>
        <a href="./a>
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

          $url = './ Get All Files For Classes Lectuer Teaches
          $data = array('session_id' => $_SESSION['code']);
          $options = array(
            'http' => array(
              'method'  => 'POST',
              'content' => http_build_query($data)
             )
           );
          $context  = stream_context_create($options);
          $result = json_decode(file_get_contents($url, false, $context), true);

          $url = './ Get All Classes Lectuer Teaches
          $data = array('session_id' => $_SESSION['code']);
          $options = array(
            'http' => array(
              'method'  => 'POST',
              'content' => http_build_query($data)
             )
           );
          $context  = stream_context_create($options);
          $classes = json_decode(file_get_contents($url, false, $context), true);

          /* Construct HTML Based On Retrieved Input */

          $class = "";
          $return = "<h1 style='margin:0;'>My Class Documents</h1>";
          $flag = true;
          foreach($result["message"] as $data) {
            if ($class != $data["class"]) {
              if ($flag == false) {
                $return .= "</div>";
              }
              $flag = false;
              $return .= "<div><h3 style='margin-bottom:10px;margin-top:0;'>".$data["class"]."</h3>";
            }
            $class = $data["class"];
            $return .= "<a href='".$data["link"]."'>".$data["title"]."</a><br/>
                        <div style='margin-top: 8px;'>
                          <button id='delete' onclick='deleteFile(event, `".$data["id"]."`)' type='submit'><i class='fa fa-trash'></i></button>
                          <button id='edit' onclick='editFile(event, `".$data["id"]."`)' type='submit'><i class='fa fa-pencil-square-o'></i></button><br><br>
                        </div>";
          }

          $return .= '</div><form onSubmit="addFile(event);">
            <fieldset>
              <legend>Add A New Document</legend>
              <div id="recipient-class">
                <br>Class:<br><br>
                <select id="class">';
                $class = "";
                $flag = true;
                foreach($classes["message"] as $data) {
                  if ($class != $data["class_name"]) {
                    if ($flag) {
                      $return .= "<option id='" . $data["class_name"] . "' selected='selected'>".$data["class_name"]."</option>";
                      $flag = false;
                    } else {
                      $return .= "<option id='" . $data["class_name"] . "'>".$data["class_name"]."</option>";
                    }
                    $class = $data["class_name"];
                  }
                }
              $return .= '
                </select>
              </div>
              <br>Title:<br><br>
              <input type="text" id="title" value="" maxlength="40">
              <br><br>Link:<br><br>
              <input type="text" id="link" value="" maxlength="100">
              <br><br><input type="submit" value="Submit">
            </fieldset>
          </form>';

          echo($return);

        }
       ?>
    </div>
  </div>
</body>
</html>
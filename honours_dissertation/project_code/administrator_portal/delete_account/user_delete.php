<?php
session_start();

/**
 *
 * Send POST Request To Delete Class In Database
 *
 */
function deleteUser() {
  $url = './deleteUser.php';
  $data = array( 'session_id' => $_SESSION[ 'code' ], 'userID' => $_POST[ 'userID' ] );

  $options = array(
    'http' => array(
      'method'  => 'POST',
      'content' => http_build_query($data)
    )
  );

  $context  = stream_context_create($options);
  $result = json_decode(file_get_contents($url, false, $context), true);

  if ( $result[ "error" ] )
    return("ERROR: " . $result[ "message" ]);
  else {
    return(true);
  }
}

if( isset($_POST['userID']) ) {
   echo deleteUser();
}

?>
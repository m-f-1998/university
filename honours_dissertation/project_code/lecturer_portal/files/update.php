<?php
session_start();

/**
 *
 * Send POST Request To Update File To Database
 *
 */
function updateFile() {
  $url = './updateFile.php';
  $data = array( 'session_id' => $_SESSION[ 'code' ], 'title' => $_POST[ 'title' ], 'id' => $_POST[ 'id' ], 'link' => $_POST[ 'link' ], 'class' => $_POST[ 'class' ] );

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

if( isset($_POST['title']) && isset($_POST['link']) && isset($_POST['class']) && isset($_POST['id']) ) {
   echo updateFile();
}

?>

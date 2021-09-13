<?php
$dataArray = [];
$confArray = [];
$subdirectories = [];
//the directory to search the files from
$directory = "data-files/";
if($directory != $dirsel){
  $fulldirectory = $directory.$dirsel."/";
}else{
  $fulldirectory = $directory;
}
$files = scandir($fulldirectory);
$dirs = scandir($directory);

foreach ($dirs as $key => $dirname) {
  if(is_dir($directory.$dirname)){
		$subdirectories[] = $dirname;
	}
}
// loop over all files found in the directory
foreach ($files as $key => $fileName) {
  $ext = pathinfo($fileName, PATHINFO_EXTENSION);
    // if the file is a JSON file then push the file contents in array
	if($ext=='json')
	{
		$data = file_get_contents($fulldirectory.$fileName);
		array_push($dataArray, json_decode($data));
	}
	else if($ext=='conf'){
		$confData = file_get_contents($fulldirectory.$fileName);
		$confArray = explode("\n",$confData);
	}
}
?>

<script>
	// store the PHP data array in JavaScript variable
	var data = <?php echo json_encode($dataArray) ?>;
	var confData = <?php echo json_encode($confArray) ?>;
	
  var maindirectory = <?php echo json_encode($directory) ?>;
  var fulldirectory = <?php echo json_encode($fulldirectory) ?>;
  console.log(data);
  var subdirectories = <?php echo json_encode($subdirectories) ?>;
</script>

<?php
$directory = "data-files/";
if(isset($_POST['dir_select'])){ //check if form was submitted
  $dirsel = $_POST['dir_select'];
}else{
  $dirsel = $directory;
}
?>
<script>
var dir = <?php echo json_encode($dirsel); ?>;
var maindirectory = <?php echo json_encode($directory); ?>;
</script>

<!DOCTYPE html>
<meta charset="utf-8">

<head>
  <!-- load the php file which imports JSON data -->
  <?php require('php/loadData.php'); ?>
  <script src="js/load_directory_data.js"></script>
  <!-- Load d3.js -->
  <script src="https://d3js.org/d3.v6.js"></script>
  <!-- Load d3-word cloud -->
  <script src="https://cdn.jsdelivr.net/gh/holtzy/D3-graph-gallery@master/LIB/d3.layout.cloud.js"></script>
  <!-- Load Visualisation Scripts -->
  <script src="js/scatterplot.js"></script>
  <script src="js/display_model_data.js"></script>
  <!-- Load Topic Clouds Scripts -->
  <script src="js/display_topic_clouds.js"></script>
  <!-- Load CSS -->
  <link rel="stylesheet" href="css/general.css">
  <link rel="stylesheet" href="css/scatterplot.css">
  <link rel="stylesheet" href="css/metrics_display.css">
  <link href="https://fonts.googleapis.com/css?family=Open+Sans%7CRaleway:regular,extra-bold" rel="stylesheet">
</head>
<body>
  <div class="div-main">
    <div>
      <p id="vr-title">F21DG Visual Report</p>
    </div>
    <div id="dir_select_div">
      <p id="dir_select_label">Choose the model directory</p>
      <form name="dir_form" id="dir_form" method="POST">
        <select name="dir_select" id="dir_select" onchange="update_dir()">
          <option value="select_model_dir" id="current_dir" disabled selected>Select Model Directory</option>
        </select>
      </form>

    <script>
      // Update directory on dropdown change
      function update_dir(){
        document.getElementById("dir_form").submit()
      }
    </script>

    </div>


<div class = "main-scatterplot-container">
  <!-- A div that contains all the sections to render a new scatter plot-->
    <div class="div-scatterplot">
      <div>
      <p>Choose the x and y axes for the scatter plot</p>
      </div>
      <div class="axis-select">
      <form name="select-form" id="select-form">
      <label>x-axis:</label>
      <select name="x-axis" id="x-axis" onchange="update_axis()">
      </select>
      <br><br>
      <label>y-axis:</label>
      <select name="y-axis" id="y-axis" onchange="update_axis()">
      </select>
      <br><br>
      <label>z-axis:</label>
      <select name="z-axis" id="z-axis" onchange="update_axis()">
        <option value="fixed">fixed</option>
      </select>
      <br><br>
      </form>
      </div>

      <div id="correlation"></div>
      <!-- Create a div where the graph will take place -->
      <div id="scatter_plot_cont" class="scatter_container"></div>

    </div>

    <!-- A div that contains all the sections to render a new scatter plot-->
    <div class="div-scatterplot">
      <div>
      <p>Choose the x and y axes for the scatter plot</p>
      </div>
      <div class="axis-select">
      <form name="select-form2" id="select-form2">
      <label>x-axis:</label>
      <select name="x-axis2" id="x-axis2" onchange="update_axis()">
      </select>
      <br><br>
      <label>y-axis:</label>
      <select name="y-axis2" id="y-axis2" onchange="update_axis()">
      </select>
      <br><br>
      <label>z-axis:</label>
      <select name="z-axis2" id="z-axis2" onchange="update_axis()">
        <option value="fixed">fixed</option>
      </select>
      <br><br>
      </form>
      </div>

      <div id="correlation2"></div>

      <!-- Create a div where the graph will take place -->
      <div id="scatter_plot_cont2" class="scatter_container"></div>

    </div>
</div>


<div class="information">
<div id="conf-file-main">
<p id="config-title">Configuration File Parameters:</p>
<div id="conf-file"></div>
</div>
</div>


    <div id="model_selection_1" class="model_div empty selectionclass1">
      <h1>Selected Model 1</h1>
    </div>

    <div id="model_selection_2" class="model_div empty selectionclass2">
      <h1>Selected Model 2</h1>
    </div>

    <div id="model_selection_3" class="model_div empty selectionclass3">
      <h1>Selected Model 3</h1>
    </div>
    <!-- Call Initialisation Script -->
    <script src="js/init.js"></script>

  </div>
</body>
</html>

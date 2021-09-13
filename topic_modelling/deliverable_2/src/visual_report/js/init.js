// MAIN
/* Temporary data array
var data = [
  {model_id:"1", metrics:{log_likelihood:10, per_diff:20}},
  {model_id:"2", metrics:{log_likelihood:20, per_diff:100}},
  {model_id:"3", metrics:{log_likelihood:200, per_diff:50}},
  {model_id:"4", metrics:{log_likelihood:25, per_diff:80}},
  {model_id:"5", metrics:{log_likelihood:10, per_diff:200}},
  {model_id:"6", metrics:{log_likelihood:150, per_diff:75}},
  {model_id:"7", metrics:{log_likelihood:10, per_diff:70}},
  {model_id:"8", metrics:{log_likelihood:0, per_diff:0}},
  {model_id:"9", metrics:{log_likelihood:200, per_diff:200}}
]
*/

// Scatter Plot Arrays
// Append additional scatter plot elemets to add more graphs
// All id elements are found in index.php div-scatterplot
var scatterplots = [
  scatterplot("scatter_plot_cont"),
  scatterplot("scatter_plot_cont2")
]
var xAxisSelections = [
  document.getElementById("x-axis"),
  document.getElementById("x-axis2")
]
var yAxisSelections = [
  document.getElementById("y-axis"),
  document.getElementById("y-axis2")
]
var zAxisSelections = [
  document.getElementById("z-axis"),
  document.getElementById("z-axis2")
]
var correlations = [
  document.getElementById("correlation"),
  document.getElementById("correlation2")
]
// Initialisation of empy arrays
var x_axes = []
var y_axes = []
var z_axes = []
var x_axis_arr = []
var y_axis_arr = []
var z_axis_arr = []
var get_x_axis_values = []
var get_y_axis_values = []
var get_z_axis_values = []

// Append the Configuration file information to the report
var conf_elem = document.getElementById("conf-file")

conf_elem.innerHTML += "Number of Models: " +(data.length-1) + "<br>";
for(var i = 0; i <confData.length;i++){
	if(!confData[i].includes("#") && confData[i] != ""){
		conf_elem.innerHTML += confData[i] + "<br>";
	}
}
// load the list of directories in the selection=
load_directory_list()
// Initialise the model selection class object
var model_metrics = display_model_data()

// Get a list of the available metrics and parameters
var availableMetrics, availableParameters, baseModel

for(var model in data){
  try{
    // Get a list of the available metrics and parameters
  	availableMetrics = data[model].Metrics
  	availableParameters = data[model].Parameters
    if(availableMetrics && availableParameters){
      baseModel = data[model]
      break
    }else{
      console.log("Could not find metrics or parameters for model: ", data[model]);
      console.log("Trying new model");
    }
  }catch{
    console.log("Could not find metrics or parameters for model: ", data[model]);
    console.log("Trying new model");
  }
}

if(!baseModel){
  var text = "Unable to find models containing parameters and metrics to build scatter plot in current directory"
  console.log(text);
  document.getElementById("dir_select_label").innerHTML = text + "<br>" +
   document.getElementById("dir_select_label").innerHTML
}

// Try retreive the available metrics and parameters for the dropdown lists
try{
	// Add the metrics and parameters to the x, y, and z axes
	for (var metric in availableMetrics) {
		console.log(metric);
		if(typeof baseModel.Metrics[metric] == "number"){
			console.log(metric);
      for(var i in xAxisSelections){
			   xAxisSelections[i].options[xAxisSelections[i].options.length] = new Option(metric, metric);
			   yAxisSelections[i].options[yAxisSelections[i].options.length] = new Option(metric, metric);
				 zAxisSelections[i].options[zAxisSelections[i].options.length] = new Option(metric, metric);
      }
		}else if(typeof baseModel.Metrics[metric].mean == "number"){
			// Append the mean if the metric is an array
      for(var i in xAxisSelections){
			   xAxisSelections[i].options[xAxisSelections[i].options.length] = new Option(metric + "_mean", metric);
			   yAxisSelections[i].options[yAxisSelections[i].options.length] = new Option(metric + "_mean", metric);
				 zAxisSelections[i].options[zAxisSelections[i].options.length] = new Option(metric + "_mean", metric);
      }
		}
	}
	for (var parameter in availableParameters) {
		if(typeof baseModel.Parameters[parameter] == "number"){
      for(var i in xAxisSelections){
         xAxisSelections[i].options[xAxisSelections[i].options.length] = new Option(parameter, parameter);
         yAxisSelections[i].options[yAxisSelections[i].options.length] = new Option(parameter, parameter);
				 zAxisSelections[i].options[zAxisSelections[i].options.length] = new Option(parameter, parameter);
      }
		}
	}
}catch(e){
	var text = "Unable to find models containing parameters and metrics to build scatter plot in current directory"
	console.log(text);
	document.getElementById("dir_select_label").innerHTML = text + "<br>" +
	 document.getElementById("dir_select_label").innerHTML
}

// Set the x and y axes to be differing metrics/parameters for all scatter plots
for(var i in xAxisSelections){
	console.log(i);
  xAxisSelections[i].selectedIndex = i
  yAxisSelections[i].selectedIndex = parseInt(i)+xAxisSelections.length
}

// Try render scatter plot objects
try{
	// Loop over all scatter plots
  for(var i in scatterplots){
		// Gets the value of the selected axis e.g. log_likelihood
    x_axes[i] = xAxisSelections[i].options[xAxisSelections[i].options.selectedIndex].value
    y_axes[i] = yAxisSelections[i].options[yAxisSelections[i].options.selectedIndex].value
		z_axes[i] = zAxisSelections[i].options[zAxisSelections[i].options.selectedIndex].value

		// Calculate pearsons r rating for the x and y axis correlation
    x_axis_arr[i] = get_pearsons_data(x_axes[i])
    y_axis_arr[i] = get_pearsons_data(y_axes[i])

    var r = get_correlation(x_axis_arr[i], y_axis_arr[i])
    displayCorrelation(r, correlations[i])

		// Gets the function that will be used to get the value of the metric/parameter for each model in data
    get_x_axis_values[i] = get_axis_data(x_axes[i])
    get_y_axis_values[i] = get_axis_data(y_axes[i])
		get_z_axis_values[i] = get_axis_data(z_axes[i])

		// Sets the draw functions for the scatter plots to be the above retreival functions
    scatterplots[i].set_draw_functions(
      get_x_axis_values[i],
      get_y_axis_values[i],
			get_z_axis_values[i]
    )
		// Renders the scatter plot
    scatterplots[i].render_plot(x_axes[i], y_axes[i])
  }
}catch(e){
	console.log(e);
	console.log("Not Enough Metrics or Parameters Aviable to Create Scatter Plot, Requires 2 Metrics or Parameters");
	console.log("Number of Metrics: ", xAxisSelections[0].options.length);
}

// Function to return the function d3 will use to retreive axis plotting values
function get_axis_data(x){
	// The function to return
  get_axis_value = function(e){
		// Fixed vlaue for a unified z axis bubble sizing option
		if(x == "fixed"){
			return 5.5
		}
		// Try retrieve the value of the metric/parameter for a model e
    try{
			// get the list of child keys for the metric x in model e
      var metric_keys = Object.keys(e.Metrics[x])
			// If there exists child keys then return the mean
			// else the direct metric value
      if(metric_keys.length > 0){
        return e.Metrics[x].mean
      }else{
        return e.Metrics[x]
      }
    }catch{
			// Metric x does not exist in model e
			console.log("Could not find metric: ", x, e);
		}
    try{
      return e.Parameters[x]
    }catch{
			// Parameter x does not exist in model e
			console.log("Could not find parameter: ", x, e);
		}
		// Model e does not contain a metric or parameter x
		console.log("Could not add: ", e);
		// Return null so model will not be displayed on the new axis values
		return null
  }
	return get_axis_value
}

// Fucntion to retrieve the x and y values of models to calculate pearsons r
function get_pearsons_data(axis_value){
	var array = []
	// for all models
	for(var model in data){
		// try get the metric/parameter value for the x/y axis
		try{
			if(data[model].Metrics.hasOwnProperty(axis_value)){
				var metric_key = Object.keys(data[model].Metrics[axis_value])
				if(metric_key.length > 0){
					//console.log(data[model].Metrics[axis_value].mean)
					array.push(data[model].Metrics[axis_value].mean)
				}
				else{
					//console.log(data[model].Metrics[axis_value])
					array.push(data[model].Metrics[axis_value])
				}
			}
			else if(data[model].Parameters.hasOwnProperty(axis_value)){
				//console.log(data[model].Parameters[axis_value])
				array.push(data[model].Parameters[axis_value])
			}
		}catch{
			// Model does not contain current axis values so dont use for r calc
			console.log("Unable to retieve axis data for model: ", data[model]);
			console.log("Removing from pearson's r calculation");
		}
	}
	return array
}

// calculating the correlation value of the x and y axis
function get_correlation(xAxisArr,yAxisArr){
	var xMean = get_mean(xAxisArr)
	var yMean = get_mean(yAxisArr)

	var numerator = xDenom = yDenom = 0

	for(var i = 0; i < xAxisArr.length; i++){
		numerator += (xAxisArr[i] - xMean) * (yAxisArr[i] - yMean)
		xDenom += Math.pow(xAxisArr[i] - xMean,2)
		yDenom += Math.pow(yAxisArr[i] - yMean,2)
	}
	var denominator = Math.sqrt(xDenom * yDenom)

	var r = numerator/denominator

	return r
}

// calculating the mean of an array for pearsons r
function get_mean(arr){
	var count = 0
	for(var i in arr){
		count += arr[i]
	}
	return (count/arr.length)
}

// displaying the correlation on the visual report
function displayCorrelation(r, correlation_element){
	if(isNaN(r)){
		correlation_element.innerHTML = "Pearson's correlation coefficient: None"
	}
	else{
		correlation_element.innerHTML = "Pearson's correlation coefficient: r = " + r.toFixed(3);
	}
}

// Function called when the values in the axes are updated
function update_axis(){
	// Loop across all scatterplots
  for(var i in scatterplots){
		// Get the value fo the axes
    x_axes[i] = xAxisSelections[i].options[xAxisSelections[i].options.selectedIndex].value
    y_axes[i] = yAxisSelections[i].options[yAxisSelections[i].options.selectedIndex].value
		z_axes[i] = zAxisSelections[i].options[zAxisSelections[i].options.selectedIndex].value

		console.log(xAxisSelections[i].options[xAxisSelections[i].options.selectedIndex].value);

		// Calculate and update the pearsons r value
    x_axis_arr[i] = get_pearsons_data(x_axes[i])
    y_axis_arr[i] = get_pearsons_data(y_axes[i])

    var r = get_correlation(x_axis_arr[i], y_axis_arr[i])
    displayCorrelation(r, correlations[i])

		// Get the axis value functions
    get_x_axis_values[i] = get_axis_data(x_axes[i])
    get_y_axis_values[i] = get_axis_data(y_axes[i])
		get_z_axis_values[i] = get_axis_data(z_axes[i])

		// Update the scatterplots axis draw functions
    scatterplots[i].set_draw_functions(
      get_x_axis_values[i],
      get_y_axis_values[i],
			get_z_axis_values[i]
    )
		// Update the scatterplot to the new axes
    scatterplots[i].update_plot(x_axes[i], y_axes[i])
  }
}

// Function to render a model that weas selected on a scatterplot
function render_selected_model_data(model){
	// Loop over all the selection containers
	var selected_model_containers = document.getElementsByClassName("model_div");
  for(var i = 0;i < selected_model_containers.length; i++){
    var element = d3.select("#" + selected_model_containers[i].id)
		// If a container is empty the model can be selected
    if(element.classed("empty")){
			console.log(element);
			// Render the model into the container
      model_metrics.render_data(model, selected_model_containers[i].id);
      // function to display topic clouds
      getDataForClouds(model, selected_model_containers[i].id);
			// Update classes
      element.classed("empty", false)
      element.classed(model.ID, true)
      return i+1
    }
  }
	// No available containers model cannot be selected
  return false
}

// Deselect a model
function deselect_model(model){
	// Loop over the selection containers
	var selected_model_containers = document.getElementsByClassName("model_div");
  for(var i = 0;i < selected_model_containers.length; i++){
    var element = d3.select("#" + selected_model_containers[i].id)
		// Remove model from respective container
    if(element.classed(model.ID)){
      element.selectAll("div").remove()
      element.selectAll("svg").remove()
      element.classed("empty", true)
      element.classed(model.ID, false)
      element.classed("highlight", false)
      return i+1
    }
  }
  return false
}

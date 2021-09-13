function display_model_data(){
  var metricsObject = {}

  // Function to render the collapsible data for the selected model
  metricsObject.render_data = function(model, container){
    // Create the container to hold the collapsible model
    var model_container = document.createElement("div")
    model_container.classList.add("collapsible_container", model.ID)

    // Create the top level collapsible button for the model
    var model_button = collapsible_button("Model: " + model.ID, model.ID)

    // Create the top level content container for the model data
    var model_div_content = document.createElement("div")
    model_div_content.classList.add("collapsible_content")

    // Call the recursive parser over the model with the content container
    recursive_parse_metrics(model, model_div_content, model.ID)

    // Append the top level button and content container to the collapsible div
    model_container.appendChild(model_button)
    model_container.appendChild(model_div_content)

    // Append the entire collapsible div to the parent html container
    document.getElementById(container).appendChild(model_container)

    // Defines the listener for the model collapsible
    collapsible_listener(model)

    return metricsObject
  }

  // Function to recursivly parse a model object and create the collapsible list
  function recursive_parse_metrics(data, content_div, model_id){
    // Loop over all the keys in the model data
    for (var metric_name in data){
      // Get the data for the key
      var metric_data = data[metric_name]
      // Get the number of child keys the data has
      var data_length = Object.keys(metric_data).length

      // If the data has child keys present or the data is the model ID
      if (data_length > 0 && typeof metric_data != "string"){
        // Create a collapsible button for the metric
        var metric_button = collapsible_button(metric_name, model_id)
        // Create a content container for the metric data
        var metric_div = document.createElement("div")
        metric_div.classList.add("collapsible_content")

        // Call recursive parse for the metric data with the metric content div
        recursive_parse_metrics(metric_data, metric_div, model_id)

        // Append the metric collapsible button and content div
        content_div.appendChild(metric_button)
        content_div.appendChild(metric_div)
        // Append a blank break div to maintain vertical ordering of collapsible
        content_div.appendChild(document.createElement("div"))
      }else{
        // When metric has no child keys
        // Append a text element to display the metric and its value
        var metric = document.createElement("p")
        metric.innerHTML = "<h2>" + metric_name + "</h2>" + metric_data
        content_div.appendChild(metric)
      }
    }
  }

  // Function to create a collapsible button
  function collapsible_button(text, model_id){
    // Create button element
    var model_button = document.createElement("button")
    // Define the text on the button
    model_button.innerHTML = text
    // Add classes to the button
    model_button.classList.add("collapsible", model_id)
    // Return the button element
    return model_button
  }

  // Listener function for the collapsible buttons
  // Retrived from W3 schools: w3schools.com/howto/howto_js_collapsible.asp
  function collapsible_listener(model){
    var coll = document.getElementsByClassName("collapsible " + model.ID);
    var i;

    for (i = 0; i < coll.length; i++) {
      coll[i].addEventListener("click", function() {
        this.classList.toggle("active");
        var content = this.nextElementSibling;
        if (content.style.display === "block") {
          content.style.display = "none";
        } else {
          content.style.display = "block";
        }
      });
    }
  }

  return metricsObject
}

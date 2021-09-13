function scatterplot(container_name){

  var container = "#" + container_name

  var scatterplotObject = {}
  // Define width, height and offset for scatter plot
  var axis_offset = 100
  var width = 600
  var height = 570

  var xdraw, ydraw, zdraw
  // Create the SVG object onto the scatter plot div
  var svg = d3.select(container)
              .append("svg")
              .attr("width", width)
              .attr("height", height)
              .attr("class", "svg")

  // Define scaler objects
  // Scale input metrics values to pixel sizes/legnths
  var xscale = d3.scaleLinear()

  var yscale = d3.scaleLinear()

  var zscale = d3.scaleLinear()

  // Create x and y axes using scaler objects
  var xaxis = d3.axisBottom()
                .scale(xscale)
                .ticks(5)

  var yaxis = d3.axisLeft()
                .scale(yscale)
                .ticks(5)

  // Mouseover functions to highlight models
  var mouseover_function = function(d, i){
    d3.selectAll(".circle"+i.ID)
      .classed("highlight", true)

    d3.selectAll(".model_div."+i.ID)
      .classed("highlight", true)
  }

  var mouseout_function = function(d,i){
    d3.selectAll(".circle"+i.ID)
      .classed("highlight", false)
    d3.selectAll(".model_div."+i.ID)
      .classed("highlight", false)
  }

  // Function to process click on model circle
  var click_selection = function(d,i){
    console.log(i);
    // Get the clicked element
    var elements = d3.selectAll(".circle"+i.ID)
    console.log(elements);
    // Select or Deselect the model
    if(!elements.classed("selected")){
      // Call the render function on the selected model
      var selection_class = render_selected_model_data(i)
      // If the model was successfully selecte
      console.log(selection_class);
      if(selection_class){
        // Update element class values
        elements.classed("selected", true)
          .classed("selectionclass"+selection_class, true)
      }
    }else{
      // Call the deselect function on the model
      var selection_class = deselect_model(i)
      // If the model was successfully deselected
      if(selection_class){
        // Update element class values
        elements.classed("selected", false)
          .classed("selectionclass"+selection_class, false)
      }
    }
  }

  // Sets the functions that define the metric to build the axes off
  scatterplotObject.set_draw_functions = function(x_draw_func, y_draw_func, z_draw_func){
    xdraw = x_draw_func
    ydraw = y_draw_func
    zdraw = z_draw_func

    console.log(zdraw);

    return scatterplotObject
  }

  // Renders and updates the scatterplot object
  scatterplotObject.render_plot = function(x_label, y_label){
    // Update the axis scaler values
    xscale.domain([
        d3.min(data, xdraw),
        d3.max(data, xdraw)
      ])
      .range([0, width-axis_offset*2])

    yscale.domain([
        d3.min(data, ydraw),
        d3.max(data, ydraw)
      ])
      .range([height-axis_offset*2, 0])

    zscale.domain([
        d3.min(data, zdraw),
        d3.max(data, zdraw)
      ])
      .range([0.5, 10.5])

    // Append the axis objects to the SVG
    svg.append("g")
      .attr("class", "x axis " + container_name)
      .attr("transform", "translate(" + axis_offset + "," + (height-axis_offset) + ")")
      .call(xaxis)

    svg.append("g")
      .attr("class", "y axis " + container_name)
      .attr("transform", "translate(" + axis_offset + "," + axis_offset + ")")
      .call(yaxis)

    svg.append("text")
      .attr("class", "x label " + container_name)
      .attr("text-anchor", "end")
      .attr("x", width-axis_offset)
      .attr("y", height-axis_offset/2)
      .style("fill", "rgba(0,0,0,0.7)")
      .text(x_label)

    svg.append("text")
      .attr("class", "y label " + container_name)
      .attr("text-anchor", "start")
      .attr("x", 0)
      .attr("y", axis_offset-10)
      .style("fill", "rgba(0,0,0,0.7)")
      .text(y_label)


    var selection = svg.selectAll("circle")
       .data(data)

    // Append circles onto the scatter plot for each topic model
    var enter_sel = selection.enter()
      .append("circle")
      .attr("class", d=>"circle"+d.ID + " circle " + container_name)
      .attr("cx", function(d){return xscale(xdraw(d))+axis_offset})
      .attr("cy", function(d){return yscale(ydraw(d))+axis_offset})
      .attr("r",  function(d){return zscale(zdraw(d))})
      .attr("stroke-width", 1)

    // Append the mouse and click functions to the circles
    var merged_sel = enter_sel.merge(selection)
      .on("mouseover", mouseover_function)
      .on("mouseout", mouseout_function)
      .on("click", click_selection)

    return scatterplotObject
  }

  // Update the scatterplot to the new axis values
  scatterplotObject.update_plot = function(x_label, y_label){
    // Create scalers for the axes
    var xscale = d3.scaleLinear()
      .domain([
        d3.min(data, xdraw),
        d3.max(data, xdraw)
      ])
      .range([0, width-axis_offset*2])

    var yscale = d3.scaleLinear()
      .domain([
        d3.min(data, ydraw),
        d3.max(data, ydraw)
      ])
      .range([height-axis_offset*2, 0])

    var zscale = d3.scaleLinear()
      .domain([
        d3.min(data, zdraw),
        d3.max(data, zdraw)
      ])
      .range([0.5, 10.5])

    // Define the new values for the axes
    var xaxis = d3.axisBottom()
                  .scale(xscale)
                  .ticks(5)

    var yaxis = d3.axisLeft()
                  .scale(yscale)
                  .ticks(5)

    // Update the axes
    svg.selectAll(".x.axis."+container_name)
      .transition()
      .duration(500)
      .attr("transform", "translate(" + axis_offset + "," + (height-axis_offset) + ")")
      .call(xaxis)

    svg.selectAll(".y.axis."+container_name)
      .transition()
      .duration(500)
      .attr("transform", "translate(" + axis_offset + "," + axis_offset + ")")
      .call(yaxis)

    // Update circles to new locations/sizes
    svg.selectAll("circle."+container_name)
      .data(data)
      .transition()
      .duration(500)
      .attr("cx", function(d){return xscale(xdraw(d))+axis_offset})
      .attr("cy", function(d){return yscale(ydraw(d))+axis_offset})
      .attr("r",  function(d){return zscale(zdraw(d))})

    // Update axis labels
    svg.selectAll(".x.label")
      .transition()
      .duration(500)
      .text(x_label)

    svg.selectAll(".y.label")
      .transition()
      .duration(500)
      .text(y_label)

    return scatterplotObject
  }

  return scatterplotObject
}

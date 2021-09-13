// getting the data required for the topic clouds
function getDataForClouds(model, container) {
	for (var individual_topics in model.Topics) {
		var topicWords = model.Topics[individual_topics];
		var wordsList = [];
		// adding the words and their sizes to array
		for (var t_words in topicWords) {
			wordsList.push({
				"word": t_words,
				"size": topicWords[t_words]
			});
		}
		// sorting the array in ascending order based on their size value
		var sortedWordsList = wordsList.sort((a, b) => parseFloat(b.size) - parseFloat(a.size));
		// selecting the top 30 words to be displayed in the topic cloud
		var top30Words = sortedWordsList.slice(0, 30);
		// function call to display the topic clouds for the model
		displayTopicClouds(top30Words, container);
	}
}

function displayTopicClouds(wordsList, container) {
	var wordscale = d3.scaleLinear()
		.domain([
			d3.min(wordsList, d=>d.size),
			d3.max(wordsList, d=>d.size)
		])
		.range([10, 40])
	// setting the dimensions and margins of the graph
	var margin = {
			top: 5,
			right: 5,
			bottom: 5,
			left: 5
		},
		width = 500 - margin.left - margin.right,
		height = 500 - margin.top - margin.bottom;

	// appending the svg object to the body of the page
	var svg2 = d3.select("#" + container).append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.attr("class", "svg cloud")
		.append("g")
		.attr("transform",
			"translate(" + margin.left + "," + margin.top + ")");

	// properties that are different between the words should be implemented here
	var layout = d3.layout.cloud()
		.size([width, height])
		.words(wordsList.map(function (d) {
			return {
				text: d.word,
				size: wordscale(d.size)
			};
		}))
		.font("'Open Sans', sans-serif")
		.padding(5)
		.rotate(function () {
			return 0;
		})
		.fontSize(function (s) {
			return s.size;
		})
		.on("end", draw);
	layout.start();

	// output of above layout is used to show the words
	// properties that are same between the words should be implemented here
	function draw(words) {
		svg2
			.append("g")
			.attr("transform", "translate(" + layout.size()[0] / 2 + "," + layout.size()[1] / 2 + ")")
			.selectAll("text")
			.data(words)
			.enter().append("text")
			.style("font-size", function (d) {
				return d.size + "px";
			})
			.style("fill", "#D65076")
			.attr("text-anchor", "middle")
			.style("font-family", "'Open Sans', sans-serif")
			.attr("transform", function (d) {
				return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
			})
			.text(function (d) {
				return d.text;
			});
	}
}

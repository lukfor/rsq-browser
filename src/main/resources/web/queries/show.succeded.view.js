var d3colors = Plotly.d3.scale.category10();

function createTrace(data, index) {

  var x = [];
  var y = [];

  for (var i = 0; i < data.aggregatedBins.length; i++) {
    x.push(data.aggregatedBins[i].end);
    y.push(data.aggregatedBins[i].sum / data.aggregatedBins[i].count);
  }

  return {
    x: x,
    y: y,
    type: 'scatter',
    name: data.name,
    line: {
      width: 1.25,
      color: d3colors(index)
    }
  };

}

var data = {{ json(query.getSubDatasets()) }};
var traces = [];
for (var i = 0; i < data.length; i++) {
  traces.push(createTrace(data[i], i));
}

var layout = {
  xaxis: {
    type: 'log',
    autorange: true,
    title : "MAF"
  },
  yaxis: {
    title : "mean(R2)",
    range: [0,1]
  }
};

Plotly.newPlot('plot', traces, layout);

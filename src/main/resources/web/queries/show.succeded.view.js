var d3colors = Plotly.d3.scale.category10();

function createTrace(data, index, filter) {

  var x = [];
  var y = [];

  if (filter.population != undefined && filter.population != '') {
    if (data.subDataset.population != filter.population) {
      return undefined;
    }
  }

  if (filter.reference != undefined && filter.reference != '') {
    if (data.subDataset.reference != filter.reference) {
      return undefined;
    }
  }

  if (filter.chip != undefined && filter.chip != '') {
    if (data.subDataset.chip != filter.chip) {
      return undefined;
    }
  }

  for (var i = 0; i < data.aggregatedBins.length; i++) {
    x.push(data.aggregatedBins[i].end);
    y.push(data.aggregatedBins[i].sum / data.aggregatedBins[i].count);
  }

  return {
    x: x,
    y: y,
    type: 'scatter',
    name: data.subDataset.name + ')',
    line: {
      width: 1.25,
      color: d3colors(index)
    }
  };

}

function updatePlot(data, filter) {

  var traces = [];
  for (var i = 0; i < data.length; i++) {
    var trace = createTrace(data[i], i, filter);
    if (trace != undefined) {
      traces.push(trace);
    }
  }

  var layout = {
    xaxis: {
      type: 'log',
      autorange: true,
      title: "MAF"
    },
    yaxis: {
      title: "mean(R2)",
      range: [0, 1]
    },
    hovermode: false,
    legend: {"orientation": "h"}
  };

  Plotly.newPlot('plot', traces, layout);
}

$(".filter").change(function(){
  filter = {
    population: $("#population").val(),
    reference: $("#reference").val(),
    chip: $("#chip").val()
  }
  updatePlot(data, filter)
});

var data = {{ json(query.getResults()) }};
updatePlot(data, {});

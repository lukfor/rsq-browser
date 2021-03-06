var colors = Plotly.d3.scale.category10();
var styles = Plotly.d3.scale.ordinal().range(["dash", "dot", "dashdot", "longdash", "longdashdot", "solid"]);
var symbols = Plotly.d3.scale.ordinal().range(["circle-open", "square-open", "x","triangle-left-open-dot" ]);

var AVG_R2 = 1;

function createTrace(data, index, filter, mode) {

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
    if (mode == AVG_R2){
      y.push(data.aggregatedBins[i].sum / data.aggregatedBins[i].count);
    } else {
      y.push(data.aggregatedBins[i].count08 / data.aggregatedBins[i].count);
    }
  }

  return {
    x: x,
    y: y,
    type: 'scatter',
    name: data.subDataset.name + ' [' + data.count  + ' variants]',
    line: {
      width: 2,
      color: colors(data.subDataset.reference),
      dash: styles(data.subDataset.population)
    },
    marker: {
      width: 2,
      symbol: symbols(data.subDataset.chip)
    }
  };

}

function updatePlot(data, filter, mode) {

  var traces = [];
  for (var i = 0; i < data.length; i++) {
    var trace = createTrace(data[i], i, filter, mode);
    if (trace != undefined) {
      traces.push(trace);
    }
  }

  var layout = {
    xaxis: {
      type: 'log',
      autorange: true,
      title: "Minor Allele Frequency (MAF)"
    },
    yaxis: {
      title: mode == AVG_R2 ? "Average Imputation Quality" : "Proportion of variants with r2>0.8",
      range: [0, 1]
    },
    //legend: {"orientation": "h"},
    margin: {
     l: 50,
     r: 50,
     b: 50,
     t: 30,
     pad: 4
   }
  };

  var config = {
      displayModeBar: false
  }

  Plotly.newPlot('plot', traces, layout, config);
}

$(".filter").change(function(){
  filter = {
    population: $("#population").val(),
    reference: $("#reference").val(),
    chip: $("#chip").val()
  }
  updatePlot(data, filter, $("#metric").val())
});

var data = {{ json(query.getResults()) }};
var filter = {
  population: $("#population").val(),
  reference: $("#reference").val(),
  chip: $("#chip").val()
}
updatePlot(data, filter, AVG_R2);

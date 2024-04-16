<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Sentiment Radar Chart</title>
    <script src="https://d3js.org/d3.v7.min.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f9f9f9;
            margin: 0;
            padding: 0;
        }

        .chart-container {
            align-items: center;
        }
        .form-container{
            max-width: 1000px;
            margin: 20px auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        form {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }

        input[type="date"],
        select {
            padding: 10px;
            width: calc(100% - 20px);
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }

        input[type="submit"] {
            background-color: #4CAF50;
            color: white;
            padding: 12px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #45a049;
        }

        svg {
            max-width: 100%;
            height: auto;
            display: block;
            margin: 0 auto;
        }


        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background-color: #f9f9f9;
        }

        .navbar {
            background-color: #333;
            overflow: hidden;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 20px;
        }

        .navbar a {
            color: white;
            text-decoration: none;
            font-size: 17px;
            padding: 10px 20px;
        }

        .navbar a:hover {
            background-color: #ddd;
            color: black;
        }

        .navbar .search-container {
            display: flex;
            align-items: center;
        }

        .navbar input[type=text] {
            padding: 8px;
            font-size: 17px;
            border: none;
            border-radius: 4px;
            margin-right: 10px;
        }

        .navbar button {
            padding: 8px 20px;
            background-color: #4CAF50;
            border: none;
            border-radius: 4px;
            color: white;
            cursor: pointer;
        }

        .navbar button:hover {
            background-color: #45a049;
        }

        @media screen and (max-width: 600px) {
            .navbar {
                flex-direction: column;
                padding: 10px;
            }

            .navbar input[type=text] {
                width: 100%;
                margin: 8px 0;
            }
        }


    </style>
</head>
<body>

<div class="navbar">
    <a href="/">Home</a>
    <a href="/download-files">Download Files</a>
    <a href="/diagram">Diagramme </a>
    <a href="/redenHome">Reden</a>
    <div class="search-container">
        <form id="searchForm" action="/search" method="post">
            <input type="text" id="query" name="query" placeholder="Search..">
            <button type="submit">Search</button>
        </form>
        <div id="searchResults"></div>
    </div>
</div>

<br>

<form action="/sentiment-radar-chart" method="get" class="form-container">
    <label for="grouping">Zeitraum wählen:</label>
    <select id="grouping" name="grouping">
        <option value="week">Woche</option>
        <option value="month">Monat</option>
    </select><br>

    <label for="startDatum">Startdatum:</label>
    <input type="date" id="startDatum" name="startDate" onchange="calculateEndDate()" required><br>

    <label for="endDate">Enddatum:</label>
    <input type="date" id="endDate" name="endDate" required><br>

    <input type="submit" value="Diagramm aktualisieren">
</form>

<div id="SentimentRadarChart" class="chart-container"></div>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script>
    $(document).ready(function() {
        $('#startDatum').change(function() {
            const startDatum = $(this).val();
            const grouping = $('#grouping').val();
            let endDate = new Date(startDatum);

            switch (grouping) {
                case 'week':
                    endDate.setDate(endDate.getDate() + 7);
                    break;
                case 'month':
                    endDate.setMonth(endDate.getMonth() + 1);
                    break;
            }

            $('#endDate').val(endDate.toISOString().split('T')[0]);
        });
    });
</script>
<script>
    var sentimentAverages = JSON.parse('${sentimentAverages}');

    var data = [
        {
            name: "Sentiment",
            axes: [
                {axis: "Positive", value: sentimentAverages.Positive},
                {axis: "Negative", value: sentimentAverages.Negative},
                {axis: "Neutral", value: sentimentAverages.Neutral}
            ]
        }
    ];

    var radarChartOptions = {
        w: 500,
        h: 500,
        margin: {top: 100, right: 100, bottom: 100, left: 100},
        levels: 5,
        roundStrokes: true,
        color: d3.scaleOrdinal().range(["#26AF32", "#762712", "#dddddd"]),
        format: '.2%'
    };
    RadarChart("#SentimentRadarChart", data, radarChartOptions);
    function RadarChart(id, data, options) {
        var cfg = {
            w: 600,
            h: 600,
            margin: {top: 10, right: 10, bottom: 10, left: 10},
            levels: 3,
            max: 0,
            labelFactor: 1.25,
            wrapWidth: 60,
            opacityArea: 0.35,
            dotRadius: 4,
            opacityCircles: 0.1,
            strokeWidth: 2,
            roundStrokes: false,
            color: d3.scaleOrdinal(d3.schemeCategory10),
            format: '.2%',
            unit: '%',
            legend: { title: 'Legend', translateX: 100, translateY: 40 }
        };

        if ('undefined' !== typeof options) {
            for (var i in options) {
                if ('undefined' !== typeof options[i]) { cfg[i] = options[i]; }
            }
        }

        var radius = Math.min(cfg.w / 2, cfg.h / 2);
        var Format = d3.format(cfg.format);
        var angleSlice = Math.PI * 2 / data[0].axes.length;

        var rScale = d3.scaleLinear()
            .range([0, radius])
            .domain([0, 1]);

        var svg = d3.select(id).append("svg")
            .attr("width",  cfg.w + cfg.margin.left + cfg.margin.right)
            .attr("height", cfg.h + cfg.margin.top + cfg.margin.bottom)
            .append("g")
            .attr("transform", "translate(" + (cfg.w/2 + cfg.margin.left) + "," + (cfg.h/2 + cfg.margin.top) + ")");

        var axisGrid = svg.append("g").attr("class", "axisWrapper");

        axisGrid.selectAll(".levels")
            .data(d3.range(1, (cfg.levels + 1)).reverse())
            .enter()
            .append("circle")
            .attr("class", "gridCircle")
            .attr("r", d => radius / cfg.levels * d)
            .style("fill", "#CDCDCD")
            .style("stroke", "#CDCDCD")
            .style("fill-opacity", cfg.opacityCircles)
            .style("filter", "url(#glow)");

        axisGrid.selectAll(".axisLabel")
            .data(d3.range(1, (cfg.levels + 1)).reverse())
            .enter().append("text")
            .attr("class", "axisLabel")
            .attr("x", 4)
            .attr("y", d => -d * radius / cfg.levels)
            .attr("dy", "0.4em")
            .style("font-size", "10px")
            .attr("fill", "#737373")
            .text(d => Format(d * 1 / cfg.levels) + cfg.unit);

        var axis = axisGrid.selectAll(".axis")
            .data(data[0].axes)
            .enter()
            .append("g")
            .attr("class", "axis");
        axis.append("line")
            .attr("x1", 0)
            .attr("y1", 0)
            .attr("x2", (d, i) => rScale(1.1) * Math.cos(angleSlice * i - Math.PI/2))
            .attr("y2", (d, i) => rScale(1.1) * Math.sin(angleSlice * i - Math.PI/2))
            .attr("class", "line")
            .style("stroke", "black")
            .style("stroke-width", "2px");

        axis.append("text")
            .attr("class", "legend")
            .style("font-size", "11px")
            .attr("text-anchor", "middle")
            .attr("dy", "0.35em")
            .attr("x", (d, i) => rScale(1.25) * Math.cos(angleSlice * i - Math.PI/2))
            .attr("y", (d, i) => rScale(1.25) * Math.sin(angleSlice * i - Math.PI/2))
            .text(d => d.axis);

        // Zeichnen der Radarbereiche
        var radarLine = d3.lineRadial()
            .curve(d3.curveLinearClosed)
            .radius(d => rScale(d.value))
            .angle((d,i) => i * angleSlice);

        if(cfg.roundStrokes) {
            radarLine.curve(d3.curveCardinalClosed);
        }

        var blobWrapper = svg.selectAll(".radarWrapper")
            .data(data)
            .enter().append("g")
            .attr("class", "radarWrapper");

        blobWrapper.append("path")
            .attr("class", "radarArea")
            .attr("d", d => radarLine(d.axes))
            .style("fill", (d,i) => cfg.color(i))
            .style("fill-opacity", cfg.opacityArea)

        blobWrapper.append("path")
            .attr("class", "radarStroke")
            .attr("d", d => radarLine(d.axes))
            .style("stroke-width", cfg.strokeWidth + "px")
            .style("stroke", (d,i) => cfg.color(i))
            .style("fill", "none")
            .style("filter", "url(#glow)");

        var blobCircleWrapper = svg.selectAll(".radarCircleWrapper")
            .data(data)
            .enter().append("g")
            .attr("class", "radarCircleWrapper");

        blobCircleWrapper.selectAll(".radarInvisibleCircle")
            .data(d => d.axes)
            .enter().append("circle")
            .attr("class", "radarInvisibleCircle")
            .attr("r", cfg.dotRadius)
            .attr("cx", (d, i) => rScale(d.value) * Math.cos(angleSlice * i - Math.PI/2))
            .attr("cy", (d, i) => rScale(d.value) * Math.sin(angleSlice * i - Math.PI/2))
            .style("fill", "none")
            .style("pointer-events", "all")

        return svg;
    }
</script>
</body>
</html>

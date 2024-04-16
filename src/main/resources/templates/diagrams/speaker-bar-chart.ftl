<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Speaker Bar Chart</title>
    <script src="https://d3js.org/d3.v7.min.js"></script>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: Arial, sans-serif;
            background-color: #f9f9f9;
        }

        .chart-container {
            align-items: center;
            margin: 200px auto;
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

        .axis--x path {
            display: none;
        }

        .axis--x .tick text {
            display: none;
        }

        .tooltip img {
            max-width: 200px;
            border-radius: 4px;
        }

        .tooltip {
            position: absolute;
            text-align: center;
            padding: 6px;
            font: 12px sans-serif;
            background: lightsteelblue;
            border: 0px;
            border-radius: 8px;
            pointer-events: none;
            opacity: 0;
            transition: opacity 0.2s;
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

<form action="/speaker-bar-chart" method="get" class="form-container">
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

<div id="BarChart" class="chart-container"></div>

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
    var speakerDetails = ${speakerDetails};
    function createBarChart(speakerDetails) {
        var data = Object.entries(speakerDetails).map(([name, details]) => ({
            speaker: name,
            speeches: details.count,
            imageUrl: details.imageUrl || ''
        }));
        var svg = d3.select("#BarChart").append("svg")
                .attr("width", 1200)
                .attr("height", 500),
            margin = {top: 20, right: 20, bottom: 50, left: 40},
            width = +svg.attr("width") - margin.left - margin.right,
            height = +svg.attr("height") - margin.top - margin.bottom,
            g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        var x = d3.scaleBand().rangeRound([0, width]).padding(0.1),
            y = d3.scaleLinear().rangeRound([height, 0]);

        x.domain(data.map(function(d) { return d.speaker; }));
        y.domain([0, d3.max(data, function(d) { return d.speeches; })]);

        g.append("g")
            .attr("class", "axis axis--x")
            .attr("transform", "translate(0," + height + ")")
            .call(d3.axisBottom(x))
            .selectAll("text")
            .style("text-anchor", "end")
            .attr("dx", "-.8em")
            .attr("dy", ".15em")
            .attr("transform", "rotate(-65)");

        g.append("g")
            .attr("class", "axis axis--y")
            .call(d3.axisLeft(y).ticks(10))
            .append("text")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", "0.71em")
            .attr("text-anchor", "end")
            .text("Speeches");

        var tooltip = d3.select("body").append("div")
            .attr("class", "tooltip")
            .style("opacity", 0);

        g.selectAll(".bar")
            .data(data)
            .enter().append("rect")
            .attr("class", "bar")
            .attr("x", function(d) { return x(d.speaker); })
            .attr("width", x.bandwidth())
            .attr("y", function(d) { return y(d.speeches); })
            .attr("height", function(d) { return height - y(d.speeches); })
            .on("mouseover", function(event, d) {
                tooltip.transition()
                    .duration(200)
                    .style("opacity", .9);
                tooltip.html(d.speaker + "<br/>" + d.speeches + " speeches" + (d.imageUrl ? "<br/><img src='" + d.imageUrl + "' alt='Speaker image'>" : ''))
                    .style("left", (event.pageX) + "px")
                    .style("top", (event.pageY - 28) + "px");
            })
            .on("mouseout", function(d) {
                tooltip.transition()
                    .duration(500)
                    .style("opacity", 0);
            });
    }
    createBarChart(speakerDetails);
</script>
</body>
</html>

<!DOCTYPE html>
<html lang="de">
<head>
    <meta charset="UTF-8">
    <title>Horizental Bar Chart</title>
    <script src="https://d3js.org/d3.v6.js"></script>
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

<div id="HorizontalBarChart" class="chart-container"></div>
<form id="chartForm" action="/bar-chart" method="get" class="form-container">
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



<svg width="960" height="500"></svg>


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
    var data = (${barChartCounts});
    var dataArray = Object.keys(data).map(function(key) {
        return {name: key, value: data[key]};
    });

    var svg = d3.select("svg"),
        margin = {top: 20, right: 30, bottom: 40, left: 100},
        width = +svg.attr("width") - margin.left - margin.right,
        height = +svg.attr("height") - margin.top - margin.bottom;

    var x = d3.scaleLinear()
        .range([0, width])
        .domain([0, d3.max(dataArray, function(d) { return d.value; })]);

    var y = d3.scaleBand()
        .range([height, 0])
        .padding(0.1)
        .domain(dataArray.map(function(d) { return d.name; }));

    var g = svg.append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    g.append("g")
        .call(d3.axisLeft(y));

    g.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x))
        .append("text")
        .attr("y", 2)
        .attr("x", x(x.ticks().pop()) + 0.5)
        .attr("dy", "0.32em")
        .attr("fill", "#000")
        .attr("font-weight", "bold")
        .attr("text-anchor", "start");

    g.selectAll(".bar")
        .data(dataArray)
        .enter().append("rect")
        .attr("class", "bar")
        .attr("y", function(d) { return y(d.name); })
        .attr("height", y.bandwidth())
        .attr("x", 0)
        .attr("width", function(d) { return x(d.value); });
</script>
<body>

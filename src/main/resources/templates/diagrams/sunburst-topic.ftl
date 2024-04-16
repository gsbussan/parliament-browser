<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Sunburst Chart</title>
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

<div id="SunburstTopic" class="chart-container"></div>
<form action="/sunburst-topic" method="get" class="form-container">
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
<svg width="1060" height="900"></svg>
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
    var data = (${topicDistribution});

    var width = 960;
    var height = 700;
    var radius = Math.min(width, height) / 2;

    var svg = d3.select("svg")
        .attr("width", width)
        .attr("height", height)
        .append("g")
        .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

    var partition = d3.partition()
        .size([2 * Math.PI, radius]);

    var root = d3.hierarchy(data)
        .sum(function(d) { return d.value; })
        .sort(function(a, b) { return b.value - a.value; });

    partition(root);

    var arc = d3.arc()
        .startAngle(function(d) { return d.x0; })
        .endAngle(function(d) { return d.x1; })
        .innerRadius(function(d) { return d.y0; })
        .outerRadius(function(d) { return d.y1; });

    svg.selectAll("path")
        .data(root.descendants())
        .enter().append("path")
        .attr("display", function(d) { return d.depth ? null : "none"; })
        .attr("d", arc)
        .style("stroke", "#fff")
        .style("fill", function(d) { return d.children ? "#ccc" : "#ddd"; });

    svg.selectAll("text")
        .data(root.descendants())
        .enter().append("text")
        .attr("transform", function(d) {
            var rotation = computeTextRotation(d);
            return "translate(" + arc.centroid(d) + ")rotate(" + rotation + ")";
        })
        .attr("dx", "6") // Verschieben Sie den Text nach außen von der Mitte des Bogens
        .attr("dy", ".35em") // Verschieben Sie den Text ein wenig nach unten, um ihn zu zentrieren
        .text(function(d) { return d.parent ? d.data.name : "" })
        .style("text-anchor", "middle");

    function computeTextRotation(d) {
        var angle = (d.x0 + d.x1) / 2 * 180 / Math.PI;
        return angle < 180 ? angle - 90 : angle + 90;
    }
</script>
</body>
</html>

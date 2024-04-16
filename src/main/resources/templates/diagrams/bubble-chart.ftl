<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Bubble Chart</title>
    <script src="https://d3js.org/d3.v7.min.js"></script>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f9f9f9;
            margin: 0;
            padding: 20px;
        }

        svg {
            display: block;
            margin: auto;
        }

        .bubble {
            fill: #4CAF50; /* Green color for bubbles */
            stroke: #fff; /* White border for bubbles */
            stroke-width: 2px;
        }

        .label {
            font-size: 12px;
            fill: #333; /* Dark color for labels */
            text-anchor: middle;
        }
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

<div id="BubbleChart"></div>
<form action="/BubbleChart" method="get" class="form-container">
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
<svg width="1600" height="1600"></svg>
<script>
    var rawData = JSON.parse('${namedEntitiesCount?js_string}');
    var processedData = Object.entries(rawData).map(([name, count]) => ({
        name: name,
        frequency: count
    }));

    var svg = d3.select('svg'),
        width = +svg.attr("width"),
        height = +svg.attr("height");

    var colorScale = d3.scaleOrdinal(d3.schemeCategory10); // Color scale

    var simulation = d3.forceSimulation(processedData)
        .force('charge', d3.forceManyBody().strength(-5)) // damit bubbles sich abstoßen und nicht zu nah aneinander sind
        .force('center', d3.forceCenter(width / 2, height / 2))
        .force('collision', d3.forceCollide().radius(function(d) {
            return d.frequency * 1.5; // Bubble größe angepasst wegen der besseren Sichtbarkeit
        }))
        .on('tick', ticked);

    function ticked() {
        var bubbles = svg.selectAll('.bubble')
            .data(processedData)
            .join('circle')
            .attr('class', 'bubble')
            .attr('r', d => d.frequency * 2) // Scale radius based on frequency (reduce the factor)
            .attr('cx', d => d.x)
            .attr('cy', d => d.y)
            .style('fill', (d, i) => colorScale(i)); // Color bubbles

        var labels = svg.selectAll('.label')
            .data(processedData)
            .join('text')
            .attr('class', 'label')
            .attr('x', d => d.x)
            .attr('y', d => d.y)
            .attr('dy', '.35em') // Center text inside the bubble
            .text(d => d.name);
    }
</script>

</body>
</html>


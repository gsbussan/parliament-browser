<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Datumsauswahl</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f2f2f2;
            margin: 0;
            padding: 0;
        }

        form {
            max-width: 600px;
            margin: 20px auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        label {
            font-weight: bold;
            color: #333;
            font-size: 16px;
            margin-bottom: 10px;
        }

        input[type="date"] {
            padding: 8px;
            width: calc(100% - 20px);
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }

        input[type="submit"] {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #45a049;
        }

        form > input[type="submit"] {
            margin-top: 20px;
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

<form action="/bar-chart" method="get">
    <label for="startDate">Startdatum:</label>
    <input type="date" id="startDate" name="startDate" required>

    <label for="endDate">Enddatum:</label>
    <input type="date" id="endDate" name="endDate" required>

    <input type="submit" value="Horizontal Bar Chart generieren">
</form>

<form action="/speaker-bar-chart" method="get">
    <label for="startDate">Startdatum:</label>
    <input type="date" id="startDate" name="startDate" required>

    <label for="endDate">Enddatum:</label>
    <input type="date" id="endDate" name="endDate" required>

    <input type="submit" value="Speaker Bar Chart generieren">
</form>

<form action="/bubble-chart" method="get">
    <label for="startDate">Startdatum:</label>
    <input type="date" id="startDate" name="startDate" required>

    <label for="endDate">Enddatum:</label>
    <input type="date" id="endDate" name="endDate" required>

    <input type="submit" value="Bubble Chart generieren">
</form>

<form action="/sentiment-radar-chart" method="get">
    <label for="startDate">Startdatum:</label>
    <input type="date" id="startDate" name="startDate" required>

    <label for="endDate">Enddatum:</label>
    <input type="date" id="endDate" name="endDate" required>

    <input type="submit" value="Radar Chart generieren">
</form>

<form action="/sunburst-topic" method="get">
    <label for="startDate">Startdatum:</label>
    <input type="date" id="startDate" name="startDate" required>

    <label for="endDate">Enddatum:</label>
    <input type="date" id="endDate" name="endDate" required>

    <input type="submit" value="Sunburst Chart generieren">
</form>

</body>
</html>

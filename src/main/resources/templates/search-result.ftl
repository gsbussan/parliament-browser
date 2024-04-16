<!DOCTYPE html>
<html>
<head>
<style>
    .panel {
        border: 1px solid #ccc;
        border-radius: 5px;
        margin-bottom: 20px;
        overflow: hidden;
    }
    .panel-header {
        background-color: #f0f0f0;
        padding: 10px;
        font-weight: bold;
        border-bottom: 1px solid #ccc;
    }
    .panel-content {
        padding: 10px;
    }
    body {
        font-family: Arial, sans-serif;
        margin: 0;
        padding: 0;
        background-color: #f5f5f5;
    }
    .container {
        width: 80%;
        margin: 0 auto;
        padding: 20px;
        background-color: #fff;
        border-radius: 5px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        margin-top: 50px;
    }
    h1 {
        color: #333;
    }
    ul {
        list-style-type: none;
        padding: 0;
    }
    li {
        padding: 10px;
        margin-bottom: 10px;
        background-color: #f9f9f9;
        border-radius: 5px;
        box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
    }
    li:hover {
        background-color: #e9e9e9;
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

<div class="panel">
    <div class="panel-header">${query}</div>
    <div class="panel-content">
        <div class="container">
            <h1>Suchergebnisse</h1>
            <ul>
                <#list results as result>
                    <li>${result}</li>
                </#list>
            </ul>
        </div>
    </div>
</div>
</body>
</html>


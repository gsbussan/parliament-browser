<!DOCTYPE html>
<html>
<head>
    <title>TOP Suche</title>
    <link rel="stylesheet" type="text/css" href="/style/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <!-- Laden Sie jQuery2 -->
    <script src="https://code.jquery.com/jquery-2.2.4.min.js"></script>

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
    <div class="login-container">
        <#if loggedIn>
            <a href="/logout">Logout</a>
        <#else>
            <a href="/login">Login</a>
        </#if>
    </div>
    </div>

    <br>

    <h1>Wahlperiode ${wp}, Sitzung Nr. ${sitzung}, ${top}</h1>

    <ul>
        <h2>Reden: </h2>
        <#list redenIDs as rede>
            <li>
                <a href='/reden/:${rede}'>Rede: ${rede}</a>
            </li>
        </#list>
    </ul>
</body>
</html>
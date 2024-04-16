<!DOCTYPE html>
<html>
<head>
    <title>Create User</title>

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
</div>

<br>
    <#if message??>
        <h2>${message}</h2>
    </#if>
    <form action="/createUser" method="post">
        <label for="username">Username:</label><br>
        <input type="text" id="username" name="username"><br>
        <label for="password">Password:</label><br>
        <input type="password" id="password" name="password"><br>
        <label for="role">Role:</label><br>
        <select id="role" name="role">
            <option value="admin">Admin</option>
            <option value="manager">Manager</option>
            <option value="normal">Normal</option>
        </select><br>
        <input type="submit" value="Submit">
    </form>
</body>
</html>

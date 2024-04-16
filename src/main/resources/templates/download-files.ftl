<!DOCTYPE html>
<html>
<head>
    <title>Fortschrittsanzeige</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script>
        var stopDownload = false;

        $(document).ready(function () {
            $("#startDownloadButton").click(function () {
                stopDownload = false; // Reset stop flag
                $.get("/start-download", function (data) {
                    // Handle the response if needed
                    console.log("Download started");
                });
            });

            $("#stopDownloadButton").click(function () {
                stopDownload = true;
                $.get("/stop-download", function (data) {
                    console.log("Download stopped");
                });
            });

            function updateProgress() {
                if (!stopDownload) {
                    $.get("/progress", function (data) {
                        var progress = JSON.parse(data);
                        $("#progressBar").css("width", progress + "%").attr("aria-valuenow", progress).text(progress + "%");
                    });

                    $.get("/file-name", function (data) {
                        $("#downloadFileName").text("Downloading: " + data);
                    });
                }
            }

            setInterval(updateProgress, 1000); // Update progress every second
        });
    </script>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background-color: #f4f4f4;
        }

        .container {
            text-align: center;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
            width: 400px;
        }

        h2 {
            margin-top: 0;
        }

        .progress {
            height: 30px;
            background-color: #f0f0f0;
            border-radius: 15px;
            margin-bottom: 20px;
            overflow: hidden;
        }

        #progressBar {
            height: 100%;
            background-color: #4CAF50;
            text-align: center;
            line-height: 30px;
            color: white;
            transition: width 0.3s ease;
        }

        #downloadFileName {
            font-size: 16px;
            margin-bottom: 20px;
        }

        .actionButton {
            background-color: #4CAF50;
            color: white;
            border: none;
            padding: 10px 20px;
            font-size: 16px;
            cursor: pointer;
            border-radius: 4px;
            transition: background-color 0.3s ease;
            margin: 0 10px;
        }

        .actionButton:hover {
            background-color: #45a049;
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

<div class="container">

    <h2>Fortschrittsanzeige Bundestagsreden</h2>

    <div class="progress">
        <div id="progressBar">0%</div>
        <p>Status:
            <#if isDownloading>
                Downloading
            <#else>
                Not Downloading
            </#if>
        </p>
    </div>

    <div id="downloadFileName">Downloading: </div>

    <button id="startDownloadButton" class="actionButton">Start Download</button>
    <button id="stopDownloadButton" class="actionButton" style="background-color: red">Stop Download</button>
</div>

</body>
</html>

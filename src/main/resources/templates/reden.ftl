<!DOCTYPE html>
<html>
<head>
    <title>Rede</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <!-- Laden Sie jQuery2 -->
    <script src="https://code.jquery.com/jquery-2.2.4.min.js"></script>
    <style>
        .person {
            color : #05f2db;
        }

        .location {
            color : #2af56d;
        }

        .organisation {
            color : #ea1aed;
        }

        .misc {
            color : #f50519;
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

    <div class="login-container">
        <#if loggedIn>
            <a href="/logout">Logout</a>
        <#else>
            <a href="/login">Login</a>
        </#if>
    </div>
</div>

<br>

<h1>Rede: ${redeID}</h1>

<!-- NamedEntities coloring legend -->
<div id="coloringMap">
    The Named Entities are colored as follows:
    <br>
    <span class='person'>Person Entity</span> ||
    <span class='location'>Location Entity</span> ||
    <span class='organisation'>Organisation Entity</span> ||
    <span class='misc'>Miscellaneous  Entity</span>
</div>

<!-- Personenbezogene Daten -->
<div id="speakerData">
    <#list abgeordneterList as speaker>
        <p>
            <#if picURL?has_content>
                <img src='${picURL}' alt='Image of ${speaker.getVorname()} ${speaker.getNachname()}'>
            </#if>
            <br>

            <#if speaker.getAnrede()?has_content>
                ${speaker.getAnrede()} ${speaker.getVorname()} ${speaker.getNachname()}
                <br>
                <#if partei?has_content>
                    Partei: ${partei}
                </#if>
                <br>
                <#if fraktion?has_content>
                    Fraktion: ${fraktion}
                </#if>

                <#else>
                ${speaker.getVorname()} ${speaker.getNachname()}
                <br>
                <#if partei?has_content>
                    Partei: ${partei}
                </#if>
                <br>
                <#if fraktion?has_content>
                    Fraktion: ${fraktion}
                </#if>
            </#if>
        </p>

    </#list>
</div>

<!-- Speech Data -->
<div id="speechData">
    <#list redenListe as rede>
        <#assign sentiment_values = rede.getSentimentValues()>
            <#if rede.getHtmlTextList()?has_content>

                <!-- Displays comments given before the speech started -->
                <#if fstComm?has_content>
                    Kommentar:
                    <i class="fas fa-comment" title="${fstComm}"></i>
                    <br>
                </#if>

                <#list rede.getHtmlTextList() as sentence>
                    <span>
                        ${sentence}
                        <#if sentiment_values?has_content && (sentence?index + 1 < sentiment_values?size)>
                            <i class="fas fa-info-circle" title="Sentiment-Wert: ${rede.getSentimentValues()[sentence?index + 1]}"></i>
                        </#if>

                        <!-- accessing the comments given -->
                        <#if comments[sentence?index?string]??>
                            <br>
                            Kommentar:
                            <i class="fas fa-comment" title="${comments[sentence?index?string]}"></i>
                        </#if>
                    </span>
                    <br>
                </#list>

            <#else>
                <h2>Diese Rede wurde noch nicht analysiert!</h2>
                <span>
                    ${rede.getText()}
                </span>
            </#if>
    </#list>
</div>





<!-- jQuery-Code für Interaktionen -->
<script>

</script>

</body>
</html>
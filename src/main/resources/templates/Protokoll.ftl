\documentclass{article}
\usepackage[utf8]{inputenc}
\usepackage{graphicx} % For including images
\usepackage{color}    % For colored text
\usepackage{hyperref} % For hyperlinks
\usepackage{enumitem} % For better lists
\usepackage{geometry} % For page margin distances
\geometry{a4paper, margin=1in}

\definecolor{personcolor}{RGB}{0, 0, 255}
\definecolor{locationcolor}{RGB}{255, 0, 0}
\definecolor{organisationcolor}{RGB}{0, 128, 0}
\definecolor{misccolor}{RGB}{128, 128, 128}

\begin{document}

<#list redenListe as rede>
    \title{Rede: ${rede.id}}
    \date{}
    \maketitle

    The Named Entities are colored as follows:\\
    \textcolor{personcolor}{Person Entity} ||
    \textcolor{locationcolor}{Location Entity} ||
    \textcolor{organisationcolor}{Organisation Entity} ||
    \textcolor{misccolor}{Miscellaneous Entity}

    \section*{Sprecherdaten}
    <#assign speakerIndex = redenListe?index_of(rede)>
    <#if abgeordneterList[speakerIndex]??>
    \begin{itemize}
        <#assign speaker = abgeordneterList>
        <#assign picUrl = picURLs[speakerIndex]>
        <#assign parteiName = parteiNamen[speakerIndex]>
        <#assign fractionName = fractionNamen[speakerIndex]>
        \item \textbf{Anrede:} ${speaker.anrede}
        \item \textbf{Name:} ${speaker.name}
        \item \textbf{Partei:} ${parteiName}
        \item \textbf{Fraktion:} ${fractionName}
        \includegraphics[width=0.25\textwidth]{${picUrl}}
    \end{itemize>
    </#if>

    \section*{Rededaten}
    <#list rede.sentences as sentence>
    \begin{enumerate}
        \item ${sentence}
    \end{enumerate>
    </#list>

    \subsection*{Kommentare und Sentimentwerte}
    <#list rede.commentsMap?keys as key>
    Some sentence.\footnote{Kommentar: ${rede.commentsMap[key]}}
    </#list>
</#list>

\end{document}

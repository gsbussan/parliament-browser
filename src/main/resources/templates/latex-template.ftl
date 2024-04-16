\documentclass{article}

\begin{document}
\tableofcontents

\section{Tagesordnung}
<#list protocol as protokoll>
    <#list protokoll.getTagesordnungspunkten() as agendaItem>
        \subsection{${agendaItem.getTopId()}}
    </#list>
    \newpage
</#list>

\newpage
<#list protokolleByChapter?keys as chapter>
    \section{${chapter}}
    <#list protokolleByChapter[chapter] as protokoll>

<#--        Hier hatten die Reden haze Zeit OutOfMemory verursacht. Haben erstmal deswegen nur mit 5 Reden gearbeitet-->

    <#--        \section{Reden}-->
<#--        <#list speeches as speech>-->
<#--            \subsection{${speech.getRedner().getVorname()}}-->

<#--            ${speech.getText()}-->
<#--            <#list speech.getKommentar() as comment>-->
<#--                \textbf{Kommentar:} ${comment.getText()}-->
<#--            </#list>-->
<#--        </#list>-->
    </#list>
</#list>



\end{document}

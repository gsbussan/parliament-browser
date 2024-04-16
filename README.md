# Parliament_Browser_01_Montag_2



## Wichtige Informationen

1. Zum Start der Webservices muss man einmal die InsightBundestagApi(api package) ausführen.
2. Spark port: 4567/home
3. Swagger Visualisierung unter http://localhost:4567/index.html#/ oder http://localhost:4567/
4. Die Aufgabe 1 und Aufgabe 4 ist in dem Ordner "abschlussprojekt-aufgabe1-aufgabe4" zu finden.
5. Die dtd Datei muss in den Resources ordner angelegt werden, und wird nicht von der Bundestag Opendata Seite
    heruntergeladen.
6. Um alles das, was Frontend angeht anzusehen, braucht man die InsightBundestagApi zu starten.
   Die Klasse findet man unter dem api package.
7. Die Fortschrittsanzeige von dem Herunterladen der Detain werden pro Legislaturperiode visualisiert.
   Das heißt, wenn das Download übers Frontend startet, dann wird erstmal die Fortschrittsanzeige von einer LP 
   und dann der andere.
8. Die Datum-Eingabefelder beim Diagrams sind nicht so benutzerfreundlich, daher bitte schauen, dass das Enddatum immer
    größer als Beginndatum ist bzw. in Zukunft liegt.
9.  Um den Protokoll-Export auszuführen, muss TexLive im Rechner installiert werden.
10. Manchmal sind die Zwischenschritte oder die Klassenimplementierungen auf Englisch dokumentiert worden.
11. Latex Export dauert bei uns 20-40 sekunden.
12. Die Größe des Fensters, in dem die Diagramme erstellt werden, wird nicht dynamisch ermittelt.
    Daher kann es dazu führen, dass die Diagramme geschnitten werden.(sieh. Bubble Chart Generierung)
13. Für Swagger.io haben wir die Dateien lokal gespeichert in dem resources ordner.(sieh. resources → swagger-ui-master)
14. Fürs Swagger Frontend haben wir das offizielle GitHub Repo von swagger-ui verwendet. 
    Quelle: https://github.com/swagger-api/swagger-ui
    
<%@ page import="model.Entity.UtenteBean" %>
<%@ page import="model.Entity.FilmBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Base64" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rated - About Us</title>
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/favicon.ico">
    <link rel="stylesheet" href="static/css/HomePage.css">
    <script src="static/scripts/homePageWarn.js" defer></script>
    <script src="static/scripts/homePageRecs.js" defer></script>
</head>
<body>
    <%@ include file="header.jsp" %>

    <%
        // Recupero l'utente dalla sessione
        UtenteBean utente = (UtenteBean) session.getAttribute("user");
        int nWarning = 0;
        if (utente != null) {
            nWarning = utente.getNWarning();
        }

        // Recupero la lista dei consigliati
        List<FilmBean> filmConsigliati = (List<FilmBean>) session.getAttribute("filmConsigliati");

        // Controllo se c'è il parametro loginSuccess (true) nella query string
        String loginSuccessParam = request.getParameter("loginSuccess");
        boolean loginSuccess = "true".equals(loginSuccessParam);
    %>

    <main>
        <div class="about-container">
            <img src="static/images/RATED_icon.png" alt="Rated Logo" class="logo-large">
            <p class="description">
                Rated è una piattaforma pensata per chi ama il cinema e vuole condividere opinioni sui film, 
                scoprire nuove recensioni e interagire con altri appassionati.
                Il nostro obiettivo è promuovere discussioni di qualità e valorizzare i contenuti più apprezzati 
                dalla community. Unisciti a noi, pubblica le tue recensioni e diventa parte della nostra famiglia di cinefili!
            </p>
            <a href="catalogo">
                <button class="catalogue-button">Scopri il catalogo di film</button>
            </a>
        </div>

        <%
            if (utente != null && "RECENSORE".equals(utente.getTipoUtente()) && filmConsigliati != null && !filmConsigliati.isEmpty()) {
        %>
        <div class="recommendations-section">
            <h2 class="rec-title">Consigliati per te</h2>
            
            <div class="carousel-container">
                <button class="scroll-btn left" onclick="scrollRecs(-1)">&#10094;</button>

                <div class="rec-wrapper" id="recWrapper">
                    <%
                        for (FilmBean film : filmConsigliati) {
                            String dettaglioUrl = "film?idFilm=" + film.getIdFilm();
                    %>
                    <div class="rec-card" id="film-card-<%= film.getIdFilm() %>">
                        <div class="rec-poster" onclick="window.location.href='<%= dettaglioUrl %>'">
                            <img src="<%= film.getLocandina() != null
                                    ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(film.getLocandina())
                                    : request.getContextPath() + "/static/images/RATED_icon.png" 
                              %>" alt="<%= film.getNome() %>">
                        </div>
                        <div class="rec-info">
                            <h4 onclick="window.location.href='<%= dettaglioUrl %>'"><%= film.getNome() %></h4>
                            <button class="not-interested-btn" onclick="ignoraFilm(<%= film.getIdFilm() %>)">
                                Non mi interessa
                            </button>
                        </div>
                    </div>
                    <%
                        }
                    %>
                </div>

                <button class="scroll-btn right" onclick="scrollRecs(1)">&#10095;</button>
            </div>
        </div>
        <%
            }
        %>
    </main>

    <script>
        // Passaggio dei dati al file JavaScript esistente
        const data = {
            loginSuccess: <%= loginSuccess ? "true" : "false" %>,
            nWarning: <%= nWarning %>
        };
    </script>
</body>
</html>
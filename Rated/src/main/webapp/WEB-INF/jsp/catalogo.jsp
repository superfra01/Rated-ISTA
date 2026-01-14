<%@ page import="java.util.List" %>
<%@ page import="model.Entity.FilmBean" %>
<%@ page import="model.Entity.UtenteBean" %>
<%@ page import="java.util.Base64" %>

<%
    // Recupero i film dalla sessione
    List<FilmBean> films = (List<FilmBean>) session.getAttribute("films");
    if (films == null) {
        films = java.util.Collections.emptyList();
    }

    // Recupero l'utente dalla sessione
    UtenteBean user = (UtenteBean) session.getAttribute("user");

    // Gestisco eventuali parametri di sorting passati via GET (sort=asc o sort=desc)
    String sort = request.getParameter("sort");
    if (sort != null && !sort.isEmpty()) {
        if (sort.equals("asc")) {
            films.sort((f1, f2) -> Integer.compare(f1.getValutazione(), f2.getValutazione()));
        } else if (sort.equals("desc")) {
            films.sort((f1, f2) -> Integer.compare(f2.getValutazione(), f1.getValutazione()));
        }
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8" />
    <title>Catalogo</title>
	<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/static/images/favicon.ico">
    <link rel="stylesheet" href="static/css/Catalogo.css" />
    <script src="static/scripts/catalogoFunctions.js" defer></script>
</head>

<jsp:include page="header.jsp" />

<body>

<div class="catalog-container">
    <div class="catalog-header">
        <div class="sorting">
            <span>Sorted by: Rating score</span>
            <a href="?sort=desc">decrescente</a>
            <a href="?sort=asc">crescente</a>
        </div>

        <%
            if (user != null && "GESTORE".equals(user.getTipoUtente())) {
        %>
            <button class="add-film-btn" onclick="openAddFilmForm()">Aggiungi film al catalogo</button>
        <%
            }
        %>
    </div>

    <!-- Se la lista films è vuota o nulla, mostriamo il messaggio -->
    <%
        if (films == null || films.isEmpty()) {
    %>
        <p style="color: white; text-align: center; font-size: 1.2rem;">
            Nessun film trovato.
        </p>
    <%
        } else {
    %>
    
    <div class="film-grid">
        <%
            for (FilmBean film : films) {
                String dettaglioUrl = "film?idFilm=" + film.getIdFilm();
        %>
            <div class="film-card" onclick="window.location.href='<%= dettaglioUrl %>'">
                <div class="film-poster">
					<img src="<%= film.getLocandina() != null
									? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(film.getLocandina())
									: request.getContextPath() + "/static/images/RATED_icon.png" 
							  %>"
						alt="Locandina" />
                    
                </div>
                <div class="film-info">
                    <h3><%= film.getNome() %></h3>
                    <p class="film-genres"><%= film.getGeneri() %></p>
                    <div class="film-rating">
                        <%
                            int stars = film.getValutazione();
                            int maxStars = 5; // Numero massimo di stelle
                        
                            // Stelle piene
                            for (int i = 0; i < stars; i++) {
                                out.print("&#9733; "); // Stella piena
                            }
                        
                            // Stelle vuote
                            for (int i = stars; i < maxStars; i++) {
                                out.print("&#9734; "); // Stella vuota
                            }
                        %>
                    </div>
                </div>
            </div>
        <%
            }
        %>
    </div>
    <%
        } // fine else
    %>
</div>

<%
    if (user != null && "GESTORE".equals(user.getTipoUtente())) {
%>
<div id="addFilmOverlay" class="overlay">
    <div class="overlay-content">
        <span class="close-btn" onclick="closeAddFilmForm()">&times;</span>
        <h2>Aggiungi un nuovo film</h2>
        <form action="<%= request.getContextPath() %>/addFilm" method="post" enctype="multipart/form-data">
            <label for="nomeFilm">Nome:</label>
            <input type="text" name="nomeFilm" id="nomeFilm" required />

            <label for="annoFilm">Anno:</label>
            <input type="number" name="annoFilm" id="annoFilm" required />

            <label for="durataFilm">Durata (min):</label>
            <input type="number" name="durataFilm" id="durataFilm" required />

            <label for="generiFilm">Generi:</label>
            <input type="text" name="generiFilm" id="generiFilm" required />

            <label for="registaFilm">Regista:</label>
            <input type="text" name="registaFilm" id="registaFilm" required />
            
            <label for="tramaFilm">Trama:</label>
            <input type="text" name="tramaFilm" id="tramaFilm" required />

            <label for="attoriFilm">Attori:</label>
            <input type="text" name="attoriFilm" id="attoriFilm" required />

            <label for="locandinaFilm">Locandina (file immagine):</label>
            <input type="file" name="locandinaFilm" id="locandinaFilm" accept="image/*" />

            <button type="submit">Aggiungi</button>
        </form>
    </div>
</div>
<%
    }
%>

</body>
</html>

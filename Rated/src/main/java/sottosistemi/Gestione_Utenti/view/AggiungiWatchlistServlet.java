package sottosistemi.Gestione_Utenti.view;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Entity.UtenteBean;
import sottosistemi.Gestione_Utenti.service.ProfileService;

@WebServlet("/AggiungiWatchlistServlet")
public class AggiungiWatchlistServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AggiungiWatchlistServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("catalogo.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Verifica Sessione e Utente
        HttpSession session = request.getSession();
        UtenteBean utenteSessione = (UtenteBean) session.getAttribute("user");

        if (utenteSessione == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            return;
        }

        // 2. Recupera ID film dalla REQUEST (inviato dal JS)
        String filmIdStr = request.getParameter("filmId");
        int filmId = -1;

        try {
            if (filmIdStr != null && !filmIdStr.isEmpty()) {
                filmId = Integer.parseInt(filmIdStr);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 3. Logica Service
        if (filmId != -1) {
            ProfileService profileService = new ProfileService();
            
            // Controlla lo stato attuale nel DB
            boolean isPresent = profileService.isFilmInWatchlist(utenteSessione.getEmail(), filmId);
            
            if (isPresent) {
                profileService.rimuoviDallaWatchlist(utenteSessione.getEmail(), filmId);
                // Aggiorna la sessione: ora NON è più in watchlist
                session.setAttribute("inwatchlist", false);
            } else {
                profileService.aggiungiAllaWatchlist(utenteSessione.getEmail(), filmId);
                // Aggiorna la sessione: ora È in watchlist
                session.setAttribute("inwatchlist", true);
            }
            
            // 4. Risposta OK per AJAX (Nessun Redirect)
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
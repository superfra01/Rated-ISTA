package sottosistemi.Gestione_Utenti.view;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Entity.FilmBean;
import model.Entity.UtenteBean;
import sottosistemi.Gestione_Utenti.service.ProfileService;

/**
 * Servlet implementation class AggiungiWatchlistServlet
 */
@WebServlet("/AggiungiWatchlistServlet")
public class AggiungiWatchlistServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public AggiungiWatchlistServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirige al catalogo se si tenta un accesso diretto via GET
        response.sendRedirect("catalogo.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Verifica Sessione e Utente
        HttpSession session = request.getSession();
        UtenteBean utenteSessione = (UtenteBean) session.getAttribute("user");

        if (utenteSessione == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Controllo di Sicurezza (Ownership Check)
        // Recuperiamo l'email (o username) inviata dal form, se presente
        String emailTarget = utenteSessione.getEmail();

        // Se il parametro 'emailUtente' è stato inviato, DEVE coincidere con l'utente in sessione.
        if (emailTarget != null && !emailTarget.isEmpty() && !emailTarget.equals(utenteSessione.getEmail())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso negato: Non puoi modificare la watchlist di un altro utente.");
            return;
        }

        // 3. Recupera parametri del film
        FilmBean film = (FilmBean) session.getAttribute("film");
        
        // Verifica che l'oggetto film sia presente in sessione
        if (film == null) {
            response.sendRedirect("catalogo.jsp"); // O gestisci l'errore diversamente
            return;
        }
        
        int filmId = film.getIdFilm();

        // 4. Chiama il Service (Business Logic)
        if (filmId != -1) {
            ProfileService profileService = new ProfileService();
            
            // Logica Toggle: Controlla se esiste, se sì rimuove, altrimenti aggiunge
            boolean isPresent = profileService.isFilmInWatchlist(utenteSessione.getEmail(), filmId);
            
            if (isPresent) {
                profileService.rimuoviDallaWatchlist(utenteSessione.getEmail(), filmId);
            } else {
                profileService.aggiungiAllaWatchlist(utenteSessione.getEmail(), filmId);
            }
        }

        // 5. Reindirizzamento (ritorna alla pagina precedente o al film)
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect("VisualizzaFilmServlet?filmId=" + filmId);
        }
    }
}
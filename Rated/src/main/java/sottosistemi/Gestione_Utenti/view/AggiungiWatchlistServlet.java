package sottosistemi.Gestione_Utenti.view;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
        UtenteBean utenteSessione = (UtenteBean) session.getAttribute("utente");

        if (utenteSessione == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Controllo di Sicurezza (Ownership Check)
        // Recuperiamo l'email (o username) inviata dal form, se presente
        String emailTarget = request.getParameter("emailUtente");

        // Se il parametro 'emailUtente' è stato inviato, DEVE coincidere con l'utente in sessione.
        // Se non coincide, qualcuno sta provando a manipolare la richiesta (IDOR).
        if (emailTarget != null && !emailTarget.isEmpty() && !emailTarget.equals(utenteSessione.getEmail())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso negato: Non puoi modificare la watchlist di un altro utente.");
            return;
        }

        // 3. Recupera parametri del film
        String filmIdStr = request.getParameter("filmId");
        int filmId = -1;
        
        try {
            if (filmIdStr != null && !filmIdStr.isEmpty()) {
                filmId = Integer.parseInt(filmIdStr);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // 4. Chiama il Service (Business Logic)
        if (filmId != -1) {
            ProfileService profileService = new ProfileService();
            
            // Utilizziamo l'oggetto utenteSessione per garantire che l'operazione
            // venga effettuata sull'account autenticato
            profileService.aggiungiAllaWatchlist(utenteSessione.getEmail(), filmId);
                
            
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
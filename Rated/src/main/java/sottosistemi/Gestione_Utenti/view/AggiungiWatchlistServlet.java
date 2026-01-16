package sottosistemi.Gestione_Utenti.view;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.DAO.InteresseDAO;
import model.Entity.InteresseBean;
import model.Entity.UtenteBean;

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
        
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Verifica Sessione e Utente
        HttpSession session = request.getSession();
        UtenteBean utente = (UtenteBean) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Recupera parametri
        String filmIdStr = request.getParameter("filmId");
        int filmId = -1;
        
        try {
            if (filmIdStr != null && !filmIdStr.isEmpty()) {
                filmId = Integer.parseInt(filmIdStr);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // 3. Chiama il Service (Business Logic)
        if (filmId != -1) {
            ProfileService profileService = new ProfileService();
            try {

                profileService.aggiungiAllaWatchlist(utente.getEmail(), filmId);
                
            } catch (SQLException e) {
                e.printStackTrace();
                // Opzionale: Gestione errore
            }
        }

        // 4. Reindirizzamento (ritorna alla pagina precedente o al film)
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect("VisualizzaFilmServlet?filmId=" + filmId);
        }
    }

    
}
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

@WebServlet("/SegnaComeVistoServlet")
public class SegnaComeVistoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public SegnaComeVistoServlet() {
        super();
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

        // 3. Chiama il Service
        if (filmId != -1) {
            ProfileService profileService = new ProfileService();
            try {
                // Passa lo username (o email) e l'id del film
                profileService.aggiungiFilmVisto(utente.getUsername(), filmId);
            } catch (SQLException e) {
                e.printStackTrace();
                // Gestione errore (es. log o messaggio utente)
            }
        }

        // 4. Reindirizzamento alla pagina precedente
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect("VisualizzaFilmServlet?filmId=" + filmId);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Impedisce chiamate GET dirette per operazioni di scrittura
        response.sendRedirect("catalogo.jsp");
    }
}
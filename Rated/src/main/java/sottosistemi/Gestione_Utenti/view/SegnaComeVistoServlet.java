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
        // 1. Verifica Sessione e Utente (Autenticazione)
        HttpSession session = request.getSession();
        UtenteBean utenteSessione = (UtenteBean) session.getAttribute("utente");

        if (utenteSessione == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Controllo Autorizzazione (Check Ownership)
        // Recuperiamo l'identificativo dell'utente che la richiesta dichiara di voler modificare
        String emailTarget = request.getParameter("emailUtente");

        // Se il parametro è presente, DEVE corrispondere all'utente loggato
        if (emailTarget != null && !emailTarget.isEmpty() && !emailTarget.equals(utenteSessione.getEmail())) {
            // Tentativo di IDOR (Insecure Direct Object Reference):
            // L'utente loggato sta provando a modificare i dati di un altro utente
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Operazione non consentita: non puoi agire per conto di altri utenti.");
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

        // 4. Chiama il Service
        if (filmId != -1) {
            ProfileService profileService = new ProfileService();
            
            // Utilizziamo sempre l'oggetto della sessione per garantire che l'operazione
            // venga eseguita sull'account autenticato
            profileService.aggiungiFilmVisto(utenteSessione.getEmail(), filmId);
            
        }

        // 5. Reindirizzamento alla pagina precedente
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
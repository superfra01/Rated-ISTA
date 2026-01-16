package sottosistemi.Gestione_Utenti.view;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Entity.RecensioneBean;
import model.Entity.UtenteBean;
import sottosistemi.Gestione_Recensioni.service.RecensioniService;
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
        UtenteBean utenteSessione = (UtenteBean) session.getAttribute("user");

        if (utenteSessione == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 2. Controllo Autorizzazione
        String emailTarget = request.getParameter("emailUtente");
        if (emailTarget != null && !emailTarget.isEmpty() && !emailTarget.equals(utenteSessione.getEmail())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Operazione non consentita.");
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

        // 4. Logica di Business tramite Service
        if (filmId != -1) {
            ProfileService profileService = new ProfileService();
            RecensioniService recensioniService = new RecensioniService();
            
            // Verifica stato attuale
            boolean giaVisto = profileService.isFilmVisto(utenteSessione.getEmail(), filmId);
            
            if (giaVisto) {
                // PRIMA di rimuovere, usa RecensioniService per verificare se esiste una recensione
                // Nota: Assumiamo che RecensioniService abbia un metodo per recuperare la recensione specifica
                RecensioneBean recensione = recensioniService.getRecensione(filmId, utenteSessione.getEmail());
                
                if (recensione != null) {
                    // BLOCCO: L'utente ha una recensione attiva per questo film
                    session.setAttribute("errorMessage", "Impossibile rimuovere dai 'Visti': hai scritto una recensione per questo film. Elimina prima la recensione.");
                } else {
                    // PROCEDI: Nessuna recensione, rimozione sicura
                    profileService.rimuoviFilmVisto(utenteSessione.getEmail(), filmId);
                }
            } else {
                // Aggiungi ai visti
                profileService.aggiungiFilmVisto(utenteSessione.getEmail(), filmId);
            }
        }

        // 5. Redirect
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
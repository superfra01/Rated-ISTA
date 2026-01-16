package sottosistemi.Gestione_Utenti.view;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.DAO.FilmDAO;
import model.DAO.FilmGenereDAO;
import model.DAO.InteresseDAO;
import model.DAO.UtenteDAO;
import model.DAO.VistoDAO;
import model.Entity.FilmBean;
import model.Entity.FilmGenereBean;
import model.Entity.UtenteBean;

/**
 * Servlet implementation class UserFilmsServlet
 */
@WebServlet("/userFilms")
public class ViewUserFilmsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ViewUserFilmsServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Recupero parametri
        String username = request.getParameter("username");
        HttpSession session = request.getSession();

        // Gestione caso username null (fallback su utente loggato o errore)
        if (username == null || username.trim().isEmpty()) {
            UtenteBean utenteLoggato = (UtenteBean) session.getAttribute("utenteLoggato");
            if (utenteLoggato != null) {
                username = utenteLoggato.getUsername();
            } else {
                response.sendRedirect("login.jsp"); // O gestisci errore
                return;
            }
        }

        // Istanziazione DAO
        UtenteDAO utenteDAO = new UtenteDAO();
        VistoDAO vistoDAO = new VistoDAO();
        InteresseDAO interesseDAO = new InteresseDAO();
        FilmGenereDAO filmGenereDAO = new FilmGenereDAO();
        // FilmDAO filmDAO = new FilmDAO(); // Decommentare se i DAO Visto/Interesse restituiscono ID e non FilmBean

        try {
            // 1. Recupero dati Utente visitato
            UtenteBean visitedUser = utenteDAO.findByUsername(username);
            if (visitedUser == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Utente non trovato");
                return;
            }
            session.setAttribute("visitedUser", visitedUser);

            // 2. Recupero liste film
            // NOTA: Assumiamo che i DAO restituiscano direttamente una List<FilmBean>.
            // Se restituiscono List<VistoBean> o List<InteresseBean>, dovrai iterare la lista 
            // e recuperare i FilmBean usando filmDAO.doRetrieveByKey(id).
            
            List<FilmBean> watchedList = vistoDAO.doRetrieveFilmsByUtente(username); 
            List<FilmBean> watchlist = interesseDAO.doRetrieveFilmsByUtente(username);

            // Inizializza le liste se i DAO tornano null
            if (watchedList == null) watchedList = new ArrayList<>();
            if (watchlist == null) watchlist = new ArrayList<>();

            session.setAttribute("watchedList", watchedList);
            session.setAttribute("watchlist", watchlist);

            // 3. Recupero Generi per i film
            // Il JSP si aspetta attributi di sessione con chiave: idFilm + "Generi"
            
            
            populateGenres(session, watchedList, filmGenereDAO);
            populateGenres(session, watchlist, filmGenereDAO);

            // 4. Forward alla pagina JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/userFilms.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore durante il recupero dei dati utente.");
        }
    }

    /**
     * Metodo helper per popolare i generi in sessione per una lista di film.
     */
    private void populateGenres(HttpSession session, List<FilmBean> films, FilmGenereDAO dao) throws SQLException {
        for (FilmBean film : films) {
            String key = film.getIdFilm() + "Generi";
            // Controllo opzionale: se è già in sessione evitiamo la query (dipende dai requisiti di freschezza dati)
            if (session.getAttribute(key) == null) {
                List<FilmGenereBean> generi = dao.findByIdFilm(film.getIdFilm());
                session.setAttribute(key, generi);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
package sottosistemi.Gestione_Catalogo.view;

import model.Entity.FilmBean;
import model.Entity.RecensioneBean;
import model.Entity.UtenteBean;
import model.Entity.ValutazioneBean;
import sottosistemi.Gestione_Catalogo.service.CatalogoService;
import sottosistemi.Gestione_Recensioni.service.RecensioniService;
import sottosistemi.Gestione_Utenti.service.ProfileService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/film")
public class VisualizzaFilmServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CatalogoService CatalogoService;
	private RecensioniService RecensioniService;
	private ProfileService ProfileService;

    @Override
    public void init() {
        CatalogoService = new CatalogoService();
        RecensioniService = new RecensioniService();
        ProfileService = new ProfileService();
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException { // Parametri final
    	final HttpSession session = request.getSession(true); // Locale final
    	final int idFilm= Integer.parseInt(request.getParameter("idFilm")); // Locale final
    	
    	final FilmBean film = CatalogoService.getFilm(idFilm); // Locale final
    	session.setAttribute("film", film);
    	
    	final List<RecensioneBean> recensioni = RecensioniService.GetRecensioni(idFilm); // Locale final
    	session.setAttribute("recensioni", recensioni);
    	
    	if(recensioni!= null) {
    		final HashMap<String, String> utenti = ProfileService.getUsers(recensioni); // Locale final
    		session.setAttribute("users", utenti);
    	}
    	
    	final UtenteBean user = (UtenteBean) session.getAttribute("user"); // Locale final
    	if(user!=null) {
    		final String email = user.getEmail(); // Locale final
        	final HashMap<String, ValutazioneBean> valutazioni = RecensioniService.GetValutazioni(idFilm, email); // Locale final
        	session.setAttribute("valutazioni", valutazioni);
    	}
    	
        request.getRequestDispatcher("/WEB-INF/jsp/film.jsp").forward(request, response);
    }

    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException { // Parametri final
    	
    }
}
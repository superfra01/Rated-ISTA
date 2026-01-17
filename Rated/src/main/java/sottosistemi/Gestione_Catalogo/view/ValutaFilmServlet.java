package sottosistemi.Gestione_Catalogo.view;



import model.Entity.UtenteBean;
import sottosistemi.Gestione_Recensioni.service.RecensioniService;


import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ValutaFilm")
public class ValutaFilmServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RecensioniService RecensioniService;

    @Override
    public void init() {
        RecensioniService = new RecensioniService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	HttpSession session = request.getSession(true);
    	UtenteBean user = (UtenteBean) session.getAttribute("user");
    	int idFilm = Integer.parseInt(request.getParameter("idFilm"));
    	String titolo = request.getParameter("titolo");
    	String recensione = request.getParameter("recensione");
    	int valutazione = Integer.parseInt(request.getParameter("valutazione"));

    	
    	RecensioniService.addRecensione(user.getEmail(), idFilm, recensione, titolo, valutazione);
    	
    	response.sendRedirect(request.getContextPath() + "/film?idFilm="+idFilm);
        
    	
    }
}

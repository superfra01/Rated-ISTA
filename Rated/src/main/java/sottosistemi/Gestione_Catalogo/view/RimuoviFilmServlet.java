package sottosistemi.Gestione_Catalogo.view;



import model.Entity.UtenteBean;
import sottosistemi.Gestione_Catalogo.service.CatalogoService;


import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/deleteFilm")
public class RimuoviFilmServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CatalogoService CatalogoService;

    @Override
    public void init() {
        CatalogoService = new CatalogoService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	HttpSession session = request.getSession(true);
    	UtenteBean user = (UtenteBean) session.getAttribute("user");
    	if(user.getTipoUtente().equals("GESTORE")) {
    		
    		int idFilm = Integer.parseInt(request.getParameter("idFilm"));
    		
    		CatalogoService.removeFilm(idFilm);
    		//response.sendRedirect(request.getContextPath() + "/catalogo");
    		response.sendRedirect(request.getContextPath() + "/catalogo");
    		
    	}else {
    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Non hai i permessi per effettuare la seguente operazione");
    	}
    }
}

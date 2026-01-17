package sottosistemi.Gestione_Catalogo.view;


import model.Entity.FilmBean;
import sottosistemi.Gestione_Catalogo.service.CatalogoService;


import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/catalogo")
public class VisualizzaCatalogoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CatalogoService CatalogoService;

    @Override
    public void init() {
        CatalogoService = new CatalogoService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession(true);

    	List<FilmBean> films = CatalogoService.getFilms();
    	session.setAttribute("films", films);
    	
        
        request.getRequestDispatcher("/WEB-INF/jsp/catalogo.jsp").forward(request, response);
        
        
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        
    }
}

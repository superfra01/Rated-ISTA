package sottosistemi.Gestione_Recensioni.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Entity.UtenteBean;
import sottosistemi.Gestione_Recensioni.service.RecensioniService;

@WebServlet("/ReportReview")
public class ReportReviewServlet extends HttpServlet{
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
	    		
    		String email = user.getEmail();
    		String emailRecensore = request.getParameter("reviewerEmail");
			int idFilm = Integer.parseInt(request.getParameter("idFilm"));
			RecensioniService RecensioniService = new RecensioniService();
			RecensioniService.report(email, emailRecensore, idFilm);
			response.sendRedirect(request.getContextPath() + "/film?idFilm=" + idFilm);
	    }
	}

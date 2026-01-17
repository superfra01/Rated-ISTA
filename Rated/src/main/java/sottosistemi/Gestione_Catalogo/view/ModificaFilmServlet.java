package sottosistemi.Gestione_Catalogo.view;

import model.Entity.UtenteBean;
import sottosistemi.Gestione_Catalogo.service.CatalogoService;


import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@WebServlet("/filmModify")
@MultipartConfig(maxFileSize = 16177215)
public class ModificaFilmServlet extends HttpServlet {
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
    		int anno = Integer.parseInt(request.getParameter("annoFilm"));
    		String Attori = request.getParameter("attoriFilm");
    		int durata = Integer.parseInt(request.getParameter("durataFilm"));
    		String Generi = request.getParameter("generiFilm");
    		
    		String Nome = request.getParameter("nomeFilm");
    		String Regista = request.getParameter("registaFilm");
    		String Trama = request.getParameter("tramaFilm");
    		
    		byte[] locandina = null;
    		Part filePart = request.getPart("locandinaFilm");
            if (filePart != null && filePart.getSize() > 0) {
                try (InputStream inputStream = filePart.getInputStream()) {
                    locandina = inputStream.readAllBytes();
                }
            }
            
    		
    		CatalogoService.modifyFilm(idFilm, anno, Attori, durata, Generi, locandina, Nome, Regista, Trama);
    		response.sendRedirect(request.getContextPath() + "/film?idFilm=" + idFilm);
    	}else {
    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Non hai i permessi per effettuare la seguente operazione");
    	}
    }
}


package sottosistemi.Gestione_Utenti.view;

import model.Entity.UtenteBean;
import sottosistemi.Gestione_Utenti.service.AutenticationService;
import utilities.FieldValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/register")
@MultipartConfig(maxFileSize = 16177215)
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1879879L;
	private AutenticationService authService;

    @Override
    public void init() {
        authService = new AutenticationService();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    	String username = request.getParameter("username");
    	String email = request.getParameter("email");
    	String password = request.getParameter("password");
    	String confirmPassword = request.getParameter("confirm_password");
    	String biography = request.getParameter("biography");
    	byte[] icon = null;
    	
    	

    	 Part filePart = request.getPart("profile_icon");
         if (filePart != null && filePart.getSize() > 0) {
             try (InputStream inputStream = filePart.getInputStream()) {
                 icon = inputStream.readAllBytes();
             }
         }



        if (FieldValidator.validateUsername(username) &&
            FieldValidator.validateEmail(email) &&
            FieldValidator.validatePassword(password) &&
            password.equals(confirmPassword)) {
        		

            UtenteBean utente = authService.register(username, email, password, biography, icon);

            if (utente != null) {
                response.sendRedirect(request.getContextPath() + "/login"); // Redirect to login after successful registration
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Registration failed. User may already exist.");
            }

        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid form data. Check your inputs.");
        }
    }
}

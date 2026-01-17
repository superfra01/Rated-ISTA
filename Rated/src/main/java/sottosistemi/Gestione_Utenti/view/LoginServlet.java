package sottosistemi.Gestione_Utenti.view;

import model.Entity.UtenteBean;
import sottosistemi.Gestione_Utenti.service.AutenticationService;
import utilities.FieldValidator;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AutenticationService authService;

    @Override
    public void init() {
        authService = new AutenticationService();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        UtenteBean utente = authService.login(email, password);

        // Validazione dei campi
        boolean isEmailValid = FieldValidator.validateEmail(email);
        boolean isPasswordValid = FieldValidator.validatePassword(password);

        if (isEmailValid && isPasswordValid) {
            if (utente != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", utente);
                response.sendRedirect(request.getContextPath() + "/?loginSuccess=true");
            } else {
                // Imposta un attributo di errore e inoltra la richiesta alla JSP
                request.setAttribute("loginError", "Email o password non valide.");
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            }
        } else {
            // Imposta un attributo di errore per input non validi
            String errorMessage = "Errore di LogIn"; 
            request.setAttribute("loginError", errorMessage);
            request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
        }
    }
}

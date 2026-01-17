package unit.test_Gestione_utenti;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import model.DAO.UtenteDAO;
import model.Entity.UtenteBean;
import sottosistemi.Gestione_Utenti.service.AutenticationService;
import utilities.PasswordUtility;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AutenticationServiceTest {

    private AutenticationService autenticationService;
    private UtenteDAO mockUtenteDAO;
    private HttpSession mockSession;

    @BeforeEach
    void setUp() {
        // Mock di UtenteDAO
        mockUtenteDAO = mock(UtenteDAO.class);

        // Mock di HttpSession
        mockSession = mock(HttpSession.class);

        // Inizializza il servizio con il DAO mockato
        autenticationService = new AutenticationService(mockUtenteDAO);
    }

    @Test
    void testLogin_Success() {
        String email = "test@example.com";
        String password = "password123";
        String hashedPassword = PasswordUtility.hashPassword(password);

        // Simula un utente esistente
        UtenteBean user = new UtenteBean();
        user.setEmail(email);
        user.setPassword(hashedPassword);

        when(mockUtenteDAO.findByEmail(email)).thenReturn(user);

        // Esegui il metodo
        UtenteBean result = autenticationService.login(email, password);

        // Verifica
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void testLogin_Failure_InvalidPassword() {
        String email = "test@example.com";
        String password = "wrongPassword";

        // Simula un utente esistente
        UtenteBean user = new UtenteBean();
        user.setEmail(email);
        user.setPassword(PasswordUtility.hashPassword("password123"));

        when(mockUtenteDAO.findByEmail(email)).thenReturn(user);

        // Esegui il metodo
        UtenteBean result = autenticationService.login(email, password);

        // Verifica
        assertNull(result);
    }

    @Test
    void testLogin_Failure_UserNotFound() {
        String email = "nonexistent@example.com";
        String password = "password123";

        when(mockUtenteDAO.findByEmail(email)).thenReturn(null);

        // Esegui il metodo
        UtenteBean result = autenticationService.login(email, password);

        // Verifica
        assertNull(result);
    }

    @Test
    void testLogout() {
        // Esegui il metodo
        autenticationService.logout(mockSession);

        // Verifica che la sessione sia invalidata
        verify(mockSession).invalidate();
    }

    @Test
    void testRegister_Success() {
        String username = "newUser";
        String email = "newuser@example.com";
        String password = "password123";
        String biografia = "This is a biography.";
        byte[] icon = new byte[]{1, 2, 3};

        when(mockUtenteDAO.findByEmail(email)).thenReturn(null);
        when(mockUtenteDAO.findByUsername(username)).thenReturn(null);

        // Esegui il metodo
        UtenteBean result = autenticationService.register(username, email, password, biografia, icon);

        // Verifica
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals(PasswordUtility.hashPassword(password), result.getPassword());
        assertEquals(biografia, result.getBiografia());
        assertArrayEquals(icon, result.getIcona());

        // Verifica che il metodo save sia stato chiamato
        verify(mockUtenteDAO).save(result);
    }

    @Test
    void testRegister_Failure_EmailExists() {
        String username = "newUser";
        String email = "existinguser@example.com";
        String password = "password123";
        String biografia = "This is a biography.";
        byte[] icon = new byte[]{1, 2, 3};

        when(mockUtenteDAO.findByEmail(email)).thenReturn(new UtenteBean());

        // Esegui il metodo
        UtenteBean result = autenticationService.register(username, email, password, biografia, icon);

        // Verifica
        assertNull(result);
    }

    @Test
    void testRegister_Failure_UsernameExists() {
        String username = "existingUser";
        String email = "newuser@example.com";
        String password = "password123";
        String biografia = "This is a biography.";
        byte[] icon = new byte[]{1, 2, 3};

        when(mockUtenteDAO.findByUsername(username)).thenReturn(new UtenteBean());

        // Esegui il metodo
        UtenteBean result = autenticationService.register(username, email, password, biografia, icon);

        // Verifica
        assertNull(result);
    }
}

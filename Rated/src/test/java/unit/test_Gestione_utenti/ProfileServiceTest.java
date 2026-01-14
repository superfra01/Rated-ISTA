package unit.test_Gestione_utenti;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.DAO.UtenteDAO;
import model.Entity.RecensioneBean;
import model.Entity.UtenteBean;
import sottosistemi.Gestione_Utenti.service.ProfileService;

import utilities.PasswordUtility;

class ProfileServiceTest {

    private ProfileService profileService;
    private UtenteDAO mockUtenteDAO;
    private DataSource mockDataSource;
    private Connection mockConnection;

    @BeforeEach
    void setUp() throws Exception {
        // Mock del DataSource
        mockDataSource = mock(DataSource.class);
        mockConnection = mock(Connection.class);

        // Configura il mock DataSource per restituire una connessione valida
        when(mockDataSource.getConnection()).thenReturn(mockConnection);

        // Mock UtenteDAO
        mockUtenteDAO = mock(UtenteDAO.class);

        // ProfileService utilizza un DAO mockato
        profileService = new ProfileService(mockUtenteDAO); 
    }

    @Test
    void testProfileUpdate_Success() {
        final String email = "test@example.com";
        final String username = "newUsername";
        final String password = "newPassword"; // Password in chiaro
        final String biografia = "New biography";
        final byte[] icon = new byte[]{1, 2, 3};

        // Simula un utente esistente
        final UtenteBean existingUser = new UtenteBean();
        existingUser.setEmail(email);
        existingUser.setPassword("oldPassword"); 

        when(mockUtenteDAO.findByEmail(email)).thenReturn(existingUser);
        // Importante: simulo che il *nuovo* username non esista già per un altro utente
        when(mockUtenteDAO.findByUsername(username)).thenReturn(null);

        // Esegui il metodo
        final UtenteBean updatedUser = profileService.ProfileUpdate(username, email, password, biografia, icon);

        // Verifica
        assertNotNull(updatedUser);
        assertEquals(username, updatedUser.getUsername());
        
        // Verifica hash password
        final String expectedHash = PasswordUtility.hashPassword(password);
        assertEquals(expectedHash, updatedUser.getPassword()); 
        
        assertEquals(biografia, updatedUser.getBiografia());
        assertArrayEquals(icon, updatedUser.getIcona());

        verify(mockUtenteDAO).update(existingUser);
    }
    
    @Test
    void testProfileUpdate_UsernameAlreadyExists() {
        final String email = "test@example.com";
        final String username = "existingUsername";

        // Simulo che findByUsername trovi un utente diverso da quello che sta facendo l'update
        final UtenteBean anotherUser = new UtenteBean();
        anotherUser.setUsername(username);
        anotherUser.setEmail("another@example.com"); // Email DIVERSA

        when(mockUtenteDAO.findByUsername(username)).thenReturn(anotherUser);

        // Esegui il metodo
        final UtenteBean result = profileService.ProfileUpdate(username, email, "password", "bio", new byte[]{1});

        // Verifica
        assertNull(result);
    }

    @Test
    void testPasswordUpdate_Success() {
        final String email = "test@example.com";
        final String newPassword = "newPassword";

        // Simula un utente esistente
        final UtenteBean existingUser = new UtenteBean();
        existingUser.setEmail(email);
        existingUser.setPassword("oldPassword");

        when(mockUtenteDAO.findByEmail(email)).thenReturn(existingUser);

        // Esegui il metodo
        final UtenteBean updatedUser = profileService.PasswordUpdate(email, newPassword);

        // Verifica
        assertNotNull(updatedUser);
        
        final String expectedHash = PasswordUtility.hashPassword(newPassword);
        assertEquals(expectedHash, updatedUser.getPassword());

        verify(mockUtenteDAO).update(existingUser);
    }

    @Test
    void testPasswordUpdate_UserNotFound() {
        final String email = "nonexistent@example.com";

        // Simula l'assenza dell'utente
        when(mockUtenteDAO.findByEmail(email)).thenReturn(null);

        // Esegui il metodo
        final UtenteBean result = profileService.PasswordUpdate(email, "newPassword");

        // Verifica
        assertNull(result);
    }

    @Test
    void testFindByUsername() {
        final String username = "testUser";

        // Simula un utente esistente
        final UtenteBean user = new UtenteBean();
        user.setUsername(username);

        when(mockUtenteDAO.findByUsername(username)).thenReturn(user);

        // Esegui il metodo
        final UtenteBean result = profileService.findByUsername(username);

        // Verifica
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void testGetUsers() {
        final List<RecensioneBean> recensioni = new ArrayList<>();

        // Simula una lista di recensioni
        final RecensioneBean recensione1 = new RecensioneBean();
        recensione1.setEmail("email1@example.com");
        final RecensioneBean recensione2 = new RecensioneBean();
        recensione2.setEmail("email2@example.com");
        recensioni.add(recensione1);
        recensioni.add(recensione2);

        // Simula utenti corrispondenti alle email
        final UtenteBean user1 = new UtenteBean();
        user1.setEmail("email1@example.com");
        user1.setUsername("user1");

        final UtenteBean user2 = new UtenteBean();
        user2.setEmail("email2@example.com");
        user2.setUsername("user2");

        when(mockUtenteDAO.findByEmail("email1@example.com")).thenReturn(user1);
        when(mockUtenteDAO.findByEmail("email2@example.com")).thenReturn(user2);

        // Esegui il metodo
        final HashMap<String, String> users = profileService.getUsers(recensioni);

        // Verifica
        assertEquals(2, users.size());
        assertEquals("user1", users.get("email1@example.com"));
        assertEquals("user2", users.get("email2@example.com"));
    }
}
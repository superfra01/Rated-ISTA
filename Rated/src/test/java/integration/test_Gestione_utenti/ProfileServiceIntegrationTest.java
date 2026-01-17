package integration.test_Gestione_utenti;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import integration.DatabaseSetupForTest;
import utilities.PasswordUtility;

import sottosistemi.Gestione_Utenti.service.ProfileService;
import model.DAO.UtenteDAO;
import model.Entity.UtenteBean;

import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.Entity.RecensioneBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProfileServiceIntegrationTest {

    private static DataSource testDataSource;

    private UtenteDAO utenteDAO;
    private ProfileService profileService;

    @BeforeAll
    static void beforeAll() {
    	testDataSource = DatabaseSetupForTest.getH2DataSource();
    }

    @BeforeEach
    void setUp() {
        utenteDAO = new UtenteDAO(testDataSource);
        profileService = new ProfileService(testDataSource);
    }

    @AfterEach
    void tearDown() {
        // List of all emails used in your tests
        String[] emailsToDelete = {
            "test@example.com", 
            "a@example.com", 
            "b@example.com", 
            "pw@example.com", 
            "mail1@example.com", 
            "mail2@example.com"
        };

        try (Connection conn = testDataSource.getConnection()) {
            // Disable auto-commit for batch performance (optional but good practice)
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Utente_Registrato WHERE email = ?")) {
                for (String email : emailsToDelete) {
                    ps.setString(1, email);
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    void testProfileUpdate_Success() {
        // 1. Creo un utente esistente
        String email = "test@example.com";
        UtenteBean existingUser = new UtenteBean();
        existingUser.setEmail(email);
        existingUser.setUsername("olduser");
        existingUser.setPassword(PasswordUtility.hashPassword("oldpass")); // Salvo già hashata per coerenza
        existingUser.setBiografia("vecchia bio");
        utenteDAO.save(existingUser);

        // 2. Dati per la modifica
        String newUsername = "newUsername";
        String newPasswordClear = "newPassword"; // Password in chiaro
        String newBio = "New biography";
        byte[] icon = new byte[]{1,2,3};

        // 3. Eseguo l'update
        UtenteBean updatedUser = profileService.ProfileUpdate(newUsername, email, newPasswordClear, newBio, icon);

        assertNotNull(updatedUser);
        assertEquals(newUsername, updatedUser.getUsername());
        
        // --- IMPLEMENTAZIONE HASH NEL TEST ---
        // Calcolo l'hash della password in chiaro per confrontarlo con quello nel DB
        String expectedHash = PasswordUtility.hashPassword(newPasswordClear);
        assertEquals(expectedHash, updatedUser.getPassword(), "La password nel DB deve corrispondere all'hash della nuova password");
        // -------------------------------------

        assertEquals(newBio, updatedUser.getBiografia());
        assertArrayEquals(icon, updatedUser.getIcona());
    }
    
    @Test
    void testProfileUpdate_UsernameAlreadyExists() {
        // Creo due utenti
        UtenteBean userA = new UtenteBean();
        userA.setEmail("a@example.com");
        userA.setUsername("UserA");
        userA.setPassword("passA");
        utenteDAO.save(userA);

        UtenteBean userB = new UtenteBean();
        userB.setEmail("b@example.com");
        userB.setUsername("UserB");
        userB.setPassword("passB");
        utenteDAO.save(userB);

        // B cerca di cambiare username in "UserA" (già preso)
        UtenteBean result = profileService.ProfileUpdate("UserA", "b@example.com", "passB", "Bio", null);
        assertNull(result, "Se l'username è già in uso, il metodo ritorna null.");
    }
    @Test
    void testPasswordUpdate_Success() {
        String email = "pw@example.com";
        UtenteBean user = new UtenteBean();
        user.setEmail(email);
        user.setUsername("pwUser");
        user.setPassword("oldPass");
        utenteDAO.save(user);

        String newPasswordClear = "newPass";
        
        // Eseguo l'update
        UtenteBean updated = profileService.PasswordUpdate(email, newPasswordClear);
        
        assertNotNull(updated);
        
        // --- IMPLEMENTAZIONE HASH NEL TEST ---
        String expectedHash = PasswordUtility.hashPassword(newPasswordClear);
        assertEquals(expectedHash, updated.getPassword());
        // -------------------------------------
    }

    @Test
    void testPasswordUpdate_UserNotFound() {
        UtenteBean result = profileService.PasswordUpdate("nonexistent@example.com", "pass");
        assertNull(result);
    }

    @Test
    void testGetUsers() {
        // Simuliamo qualche recensione
        List<RecensioneBean> recensioni = new ArrayList<>();

        RecensioneBean r1 = new RecensioneBean();
        r1.setEmail("mail1@example.com");
        RecensioneBean r2 = new RecensioneBean();
        r2.setEmail("mail2@example.com");
        recensioni.add(r1);
        recensioni.add(r2);

        // Creiamo due utenti
        UtenteBean user1 = new UtenteBean();
        user1.setEmail("mail1@example.com");
        user1.setUsername("user1");
        utenteDAO.save(user1);

        UtenteBean user2 = new UtenteBean();
        user2.setEmail("mail2@example.com");
        user2.setUsername("user2");
        utenteDAO.save(user2);

        HashMap<String, String> usersMap = profileService.getUsers(recensioni);

        assertEquals(2, usersMap.size());
        assertEquals("user1", usersMap.get("mail1@example.com"));
        assertEquals("user2", usersMap.get("mail2@example.com"));
    }
}

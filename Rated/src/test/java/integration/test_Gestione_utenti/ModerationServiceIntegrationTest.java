package integration.test_Gestione_utenti;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import integration.DatabaseSetupForTest;
import sottosistemi.Gestione_Utenti.service.ModerationService;
import model.DAO.UtenteDAO;
import model.Entity.UtenteBean;

import org.apache.commons.dbcp2.BasicDataSource;
import javax.sql.DataSource;

public class ModerationServiceIntegrationTest {

    private static DataSource testDataSource;

    private UtenteDAO utenteDAO;
    private ModerationService moderationService;

    @BeforeAll
    static void beforeAll() {
    	testDataSource = DatabaseSetupForTest.getH2DataSource();
    }

    @BeforeEach
    void setUp() {
        utenteDAO = new UtenteDAO(testDataSource);
        moderationService = new ModerationService(utenteDAO);
    }

    @AfterEach
    void tearDown() {
        // pulizia se necessario
    }

   
    @Test
    void testWarn_UserNotFound() {
        assertDoesNotThrow(() -> {
            moderationService.warn("non.esiste@example.com");
        });
    }
}

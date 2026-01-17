package integration.test_Gestione_catalogo;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;

import integration.DatabaseSetupForTest;
import sottosistemi.Gestione_Catalogo.service.CatalogoService;
import model.DAO.FilmDAO;
import model.Entity.FilmBean;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CatalogoServiceIntegrationTest {

    private static DataSource testDataSource; 
    private FilmDAO filmDAO;
    private CatalogoService catalogoService;

    // 1. QUESTO MANCAVA: Inizializzazione del DataSource
    @BeforeAll
    static void beforeAll() {
    	testDataSource = DatabaseSetupForTest.getH2DataSource();
    }

    // 2. Metodo per pulire il DB (Consigliato per evitare errori di ID duplicati)
    private void cleanDb() {
        try (Connection conn = testDataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0");
            // Cancelliamo solo i Film e le tabelle collegate, se necessario
            // Se cancelli un film, devi assicurarti che non ci siano recensioni collegate
            stmt.executeUpdate("TRUNCATE TABLE Valutazione");
            stmt.executeUpdate("TRUNCATE TABLE Report");
            stmt.executeUpdate("TRUNCATE TABLE Recensione");
            stmt.executeUpdate("TRUNCATE TABLE Film");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUp() {
        // Pulizia prima di ogni test
        cleanDb();
        
        // Ora testDataSource NON è null
        filmDAO = new FilmDAO(testDataSource);
        catalogoService = new CatalogoService(filmDAO);
    }

    @Test
    void testGetFilms_ReturnsAllFilms() {
        List<FilmBean> films = catalogoService.getFilms();
        assertNotNull(films, "La lista dei film non dovrebbe essere null.");
    }

    @Test
    void testAggiungiFilm_ValidData_ShouldSaveToDatabase() {
        String nome = "Test Film";
        int anno = 2022;
        int durata = 120;
        String generi = "Azione";
        String regista = "John Doe";
        String attori = "Attore1, Attore2";
        byte[] locandina = null; 
        String trama = "Trama di test.";

        catalogoService.aggiungiFilm(nome, anno, durata, generi, regista, attori, locandina, trama);

        // Verifichiamo su DB
        List<FilmBean> allFilms = filmDAO.findAll();
        boolean found = allFilms.stream()
                .anyMatch(f -> f.getNome().equals(nome) && f.getAnno() == anno);
        assertTrue(found, "Il film appena aggiunto deve essere presente nel catalogo.");
    }

    @Test
    void testRicercaFilm_ExistingTitle_ShouldReturnResult() {
        // Inseriamo un film di test
        FilmBean film = new FilmBean();
        film.setNome("Inception");
        film.setAnno(2010);
        // Assicurati di usare il metodo add/save corretto che hai sistemato prima
        filmDAO.save(film); // O 'save' se non hai rinominato, ma ricorda il RETURN_GENERATED_KEYS

        // Ora cerchiamo
        List<FilmBean> risultati = catalogoService.ricercaFilm("Inception");
        assertFalse(risultati.isEmpty(), "Dovrebbe trovare almeno un film con titolo 'Inception'.");
        assertEquals("Inception", risultati.get(0).getNome());
    }

    @Test
    void testRimuoviFilm_ShouldDeleteFromDB() {
        String nome = "FilmToRemove";
        int anno = 2022;
        int durata = 120;
        String generi = "Azione";
        String regista = "John Doe";
        String attori = "Attore1, Attore2";
        byte[] locandina = "s".getBytes(); 
        String trama = "Trama di test.";
        
        catalogoService.aggiungiFilm(nome, anno, durata, generi, regista, attori, locandina, trama);

        List<FilmBean> all = filmDAO.findAll();
        FilmBean toRemove = all.stream()
                .filter(f -> "FilmToRemove".equals(f.getNome()))
                .findFirst()
                .orElse(null);
        assertNotNull(toRemove);

        catalogoService.rimuoviFilm(toRemove);

        FilmBean check = filmDAO.findById(toRemove.getIdFilm());
        assertNull(check, "Il film dovrebbe essere stato rimosso dal database.");
    }
}
package integration.test_Gestione_catalogo;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;

import integration.DatabaseSetupForTest;
import sottosistemi.Gestione_Catalogo.service.CatalogoService;
import model.DAO.FilmDAO;
import model.DAO.FilmGenereDAO;
import model.DAO.GenereDAO;
import model.Entity.FilmBean;
import model.Entity.FilmGenereBean;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CatalogoServiceIntegrationTest {

    private static DataSource testDataSource; 
    private FilmDAO filmDAO;
    private FilmGenereDAO filmGenereDAO;
    private CatalogoService catalogoService;

    // 1. QUESTO MANCAVA: Inizializzazione del DataSource
    @BeforeAll
    static void beforeAll() {
    	testDataSource = DatabaseSetupForTest.getH2DataSource();
    }

    // 2. Metodo per pulire il DB (Consigliato per evitare errori di ID duplicati)
    private void cleanDb() {
        try (final Connection conn = testDataSource.getConnection();
             final Statement stmt = conn.createStatement()) {
            
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
        filmGenereDAO = new FilmGenereDAO();
        catalogoService = new CatalogoService(new FilmDAO(), new FilmGenereDAO(), new GenereDAO());
    }

    @Test
    void testGetFilms_ReturnsAllFilms() {
        final List<FilmBean> films = catalogoService.getFilms();
        assertNotNull(films, "La lista dei film non dovrebbe essere null.");
    }

    @Test
    void testAggiungiFilm_ValidData_ShouldSaveToDatabase() {
        final String nome = "Test Film";
        final int anno = 2022;
        final int durata = 120;
        final String generi[] = new String[]{"Azione"};
        final String regista = "John Doe";
        final String attori = "Attore1, Attore2";
        final byte[] locandina = null; 
        final String trama = "Trama di test.";

        catalogoService.aggiungiFilm(nome, anno, durata, generi, regista, attori, locandina, trama);

        // Verifichiamo su DB
        final List<FilmBean> allFilms = filmDAO.findAll();
        final boolean found = allFilms.stream()
                .anyMatch(f -> f.getNome().equals(nome) && f.getAnno() == anno);
        assertTrue(found, "Il film appena aggiunto deve essere presente nel catalogo.");
    }

    @Test
    void testRicercaFilm_ExistingTitle_ShouldReturnResult() {
        // Inseriamo un film di test
        final FilmBean film = new FilmBean();
        film.setNome("Inception");
        film.setAnno(2010);
        // Assicurati di usare il metodo add/save corretto che hai sistemato prima
        filmDAO.save(film); // O 'save' se non hai rinominato, ma ricorda il RETURN_GENERATED_KEYS

        // Ora cerchiamo
        final List<FilmBean> risultati = catalogoService.ricercaFilm("Inception");
        assertFalse(risultati.isEmpty(), "Dovrebbe trovare almeno un film con titolo 'Inception'.");
        assertEquals("Inception", risultati.get(0).getNome());
    }

    @Test
    void testRimuoviFilm_ShouldDeleteFromDB() {
        final String nome = "FilmToRemove";
        final int anno = 2022;
        final int durata = 120;
        final String[] generi = new String[]{"Azione"};
        final String regista = "John Doe";
        final String attori = "Attore1, Attore2";
        final byte[] locandina = "s".getBytes(); 
        final String trama = "Trama di test.";
        
        catalogoService.aggiungiFilm(nome, anno, durata, generi, regista, attori, locandina, trama);

        final List<FilmBean> all = filmDAO.findAll();
        final FilmBean toRemove = all.stream()
                .filter(f -> "FilmToRemove".equals(f.getNome()))
                .findFirst()
                .orElse(null);
        assertNotNull(toRemove);

        catalogoService.rimuoviFilm(toRemove);

        final FilmBean check = filmDAO.findById(toRemove.getIdFilm());
        assertNull(check, "Il film dovrebbe essere stato rimosso dal database.");
    }

    @Test
    void testAddFilm_AddsGeneriAssociations() {
        final String nome = "Film Generi";
        final int anno = 2020;
        final int durata = 100;
        final String[] generi = new String[]{"Azione", "Drammatico"};
        final String regista = "Regista";
        final String attori = "Attori";
        final byte[] locandina = null;
        final String trama = "Trama";

        catalogoService.addFilm(anno, attori, durata, generi, locandina, nome, regista, trama);

        final FilmBean film = filmDAO.findByName(nome).get(0);
        final List<FilmGenereBean> generiSalvati = filmGenereDAO.findByIdFilm(film.getIdFilm());
        assertTrue(generiSalvati.size() >= 2);
        final boolean hasAzione = generiSalvati.stream().anyMatch(g -> "Azione".equals(g.getNomeGenere()));
        final boolean hasDrammatico = generiSalvati.stream().anyMatch(g -> "Drammatico".equals(g.getNomeGenere()));
        assertTrue(hasAzione);
        assertTrue(hasDrammatico);
    }

    @Test
    void testModifyFilm_UpdatesGeneri() {
        final String nome = "Film Modifica";
        final int anno = 2019;
        final int durata = 110;
        final String[] generi = new String[]{"Azione"};
        final String regista = "Regista";
        final String attori = "Attori";
        final byte[] locandina = null;
        final String trama = "Trama";

        catalogoService.addFilm(anno, attori, durata, generi, locandina, nome, regista, trama);
        final FilmBean film = filmDAO.findByName(nome).get(0);

        final String[] nuoviGeneri = new String[]{"Commedia", "Thriller"};
        catalogoService.modifyFilm(film.getIdFilm(), durata, nome, anno, nuoviGeneri, locandina, regista, attori, trama);

        final List<FilmGenereBean> generiAggiornati = filmGenereDAO.findByIdFilm(film.getIdFilm());
        assertEquals(2, generiAggiornati.size());
    }

    @Test
    void testGetGeneri_ReturnsAssociations() {
        final List<FilmGenereBean> generi = catalogoService.getGeneri(1);
        assertFalse(generi.isEmpty(), "Il film con ID 1 deve avere generi associati.");
    }

    @Test
    void testGetAllGeneri_ReturnsList() {
        final List<String> generi = catalogoService.getAllGeneri();
        assertTrue(generi.contains("Azione"));
    }
}

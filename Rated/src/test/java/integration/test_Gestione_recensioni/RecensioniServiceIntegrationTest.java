package integration.test_Gestione_recensioni;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import integration.DatabaseSetupForTest;
import sottosistemi.Gestione_Recensioni.service.RecensioniService;
import model.DAO.RecensioneDAO;
import model.DAO.ReportDAO;
import model.DAO.UtenteDAO;
import model.DAO.FilmDAO;
import model.DAO.ValutazioneDAO;
import model.Entity.RecensioneBean;
import model.Entity.UtenteBean;
import model.Entity.FilmBean;

import javax.sql.DataSource;

import java.util.List;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class RecensioniServiceIntegrationTest {

    private static DataSource testDataSource;

    private RecensioneDAO recensioneDAO;
    private UtenteDAO utenteDAO;
    private ReportDAO reportDAO;
    private FilmDAO filmDAO;
    private ValutazioneDAO valutazioneDAO;
    private RecensioniService recensioniService;

    @BeforeAll
    static void beforeAll() {
    	testDataSource = DatabaseSetupForTest.getH2DataSource();
    }

    @BeforeEach
    void setUp() {
        recensioneDAO = new RecensioneDAO(testDataSource);
        filmDAO = new FilmDAO(testDataSource);
        valutazioneDAO = new ValutazioneDAO(testDataSource);
        utenteDAO = new UtenteDAO(testDataSource);
        reportDAO = new ReportDAO(testDataSource);
        recensioniService = new RecensioniService(recensioneDAO, valutazioneDAO, reportDAO, filmDAO);
        

        try (final Connection conn = testDataSource.getConnection();
             final Statement stmt = conn.createStatement()) {
               
               // Ordine CRITICO: dai figli ai padri
               stmt.executeUpdate("DELETE FROM Valutazione");
               stmt.executeUpdate("DELETE FROM Report");
               stmt.executeUpdate("DELETE FROM Recensione");
               
               // Ora puoi cancellare i padri
               stmt.executeUpdate("DELETE FROM Film");
               stmt.executeUpdate("DELETE FROM Utente_Registrato");
               
           } catch (SQLException e) {
               e.printStackTrace();
           }
    }


    @Test
    void testAddRecensione_Valid_ShouldCreateAndUpdateFilmRating() {
    	final UtenteBean alice = new UtenteBean();
        alice.setEmail("alice@example.com");
        alice.setUsername("Alice");
        alice.setPassword("password");
        utenteDAO.save(alice);
        
        FilmBean film = new FilmBean();
        film.setNome("FilmTest");
        filmDAO.save(film);
        
        final List<FilmBean> lista = (List<FilmBean>) filmDAO.findByName(film.getNome());
        film = lista.get(0); // Riassegnazione, quindi 'film' non è final
        
        recensioniService.addRecensione("alice@example.com", film.getIdFilm(), "Ottimo film", "Recensione Alice", 5);

        final RecensioneBean rec = recensioneDAO.findById("alice@example.com", film.getIdFilm());
        assertNotNull(rec);
        assertEquals("Recensione Alice", rec.getTitolo());
        assertEquals(5, rec.getValutazione());

        final FilmBean updatedFilm = filmDAO.findById(film.getIdFilm());
        assertEquals(5, updatedFilm.getValutazione());
    }

    @Test
    void testAddRecensione_Duplicate_ShouldNotCreate() {
    	final UtenteBean bob = new UtenteBean();
        bob.setEmail("bob@example.com");
        bob.setUsername("bob");
        bob.setPassword("password");
        utenteDAO.save(bob);
        
        FilmBean film = new FilmBean();
        film.setNome("Film DoubleRec");
        filmDAO.save(film);
        
        final List<FilmBean> lista = (List<FilmBean>) filmDAO.findByName(film.getNome());
        film = lista.get(0); // Riassegnazione
        
        recensioniService.addRecensione("bob@example.com", film.getIdFilm(), "Prima", "Titolo1", 3);
        // Riprovo con la stessa email + stesso film
        recensioniService.addRecensione("bob@example.com", film.getIdFilm(), "Seconda", "Titolo2", 5);

        final List<RecensioneBean> recs = recensioneDAO.findByIdFilm(film.getIdFilm());
        assertEquals(1, recs.size());
        assertEquals("Prima", recs.get(0).getContenuto());
    }

    @Test
    void testAddValutazione_NewLike_ShouldIncrementNLike() {
    	final UtenteBean y = new UtenteBean();
    	y.setEmail("y@example.com");
        y.setUsername("y");
        y.setPassword("password");
        utenteDAO.save(y);
        
    	final UtenteBean x = new UtenteBean();
    	x.setEmail("x@example.com");
        x.setUsername("x");
        x.setPassword("password");
        utenteDAO.save(x);
        
        FilmBean film = new FilmBean();
        film.setNome("FilmLikeTest");
        filmDAO.save(film);
        
        final List<FilmBean> lista = (List<FilmBean>) filmDAO.findByName(film.getNome());
        film = lista.get(0); // Riassegnazione

        recensioniService.addRecensione("y@example.com", film.getIdFilm(), "Rec di Y", "Titolo Rec Y", 3);

        // X mette like
        recensioniService.addValutazione("x@example.com", film.getIdFilm(), "y@example.com", true);

        final RecensioneBean recY = recensioneDAO.findById("y@example.com", film.getIdFilm());
        assertEquals(1, recY.getNLike());
        assertEquals(0, recY.getNDislike());
    }

    @Test
    void testDeleteRecensione_ShouldRemoveAndUpdateFilmRating() {
    	final UtenteBean bob = new UtenteBean();
        bob.setEmail("bob@example.com");
        bob.setUsername("bob");
        bob.setPassword("password");
        utenteDAO.save(bob);
        
        final UtenteBean alice = new UtenteBean();
        alice.setEmail("alice@example.com");
        alice.setUsername("Alice");
        alice.setPassword("password");
        utenteDAO.save(alice);
        
        FilmBean film = new FilmBean();
        film.setNome("Film DoubleRec");
        filmDAO.save(film);
        
        final List<FilmBean> lista = (List<FilmBean>) filmDAO.findByName(film.getNome());
        film = lista.get(0); // Riassegnazione
        

        recensioniService.addRecensione("alice@example.com", film.getIdFilm(), "Rec Alice", "Tit1", 4);
        recensioniService.addRecensione("bob@example.com", film.getIdFilm(), "Rec Bob", "Tit2", 2);

        recensioniService.deleteRecensione("alice@example.com", film.getIdFilm());

        assertNull(recensioneDAO.findById("alice@example.com", film.getIdFilm()));

        final FilmBean updatedFilm = filmDAO.findById(film.getIdFilm());
        assertEquals(2, updatedFilm.getValutazione()); // solo Bob rimane
    }
}
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
import model.Entity.ReportBean;
import model.Entity.ValutazioneBean;

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

    @Test
    void testReportAndDeleteReports() {
        final UtenteBean reporter = new UtenteBean();
        reporter.setEmail("reporter@example.com");
        reporter.setUsername("reporter");
        reporter.setPassword("password");
        utenteDAO.save(reporter);

        final UtenteBean author = new UtenteBean();
        author.setEmail("author@example.com");
        author.setUsername("author");
        author.setPassword("password");
        utenteDAO.save(author);

        FilmBean film = new FilmBean();
        film.setNome("Film Report");
        filmDAO.save(film);
        final List<FilmBean> lista = (List<FilmBean>) filmDAO.findByName(film.getNome());
        film = lista.get(0);

        recensioniService.addRecensione(author.getEmail(), film.getIdFilm(), "Recensione", "Titolo", 4);

        recensioniService.report(reporter.getEmail(), author.getEmail(), film.getIdFilm());

        final RecensioneBean recensione = recensioneDAO.findById(author.getEmail(), film.getIdFilm());
        assertEquals(1, recensione.getNReports());
        final ReportBean report = reportDAO.findById(reporter.getEmail(), author.getEmail(), film.getIdFilm());
        assertNotNull(report);

        recensioniService.deleteReports(author.getEmail(), film.getIdFilm());

        final RecensioneBean aggiornata = recensioneDAO.findById(author.getEmail(), film.getIdFilm());
        assertEquals(0, aggiornata.getNReports());
        assertNull(reportDAO.findById(reporter.getEmail(), author.getEmail(), film.getIdFilm()));
    }

    @Test
    void testAddValutazione_SwitchToDislike() {
        final UtenteBean reviewer = new UtenteBean();
        reviewer.setEmail("reviewer@example.com");
        reviewer.setUsername("reviewer");
        reviewer.setPassword("password");
        utenteDAO.save(reviewer);

        final UtenteBean voter = new UtenteBean();
        voter.setEmail("voter@example.com");
        voter.setUsername("voter");
        voter.setPassword("password");
        utenteDAO.save(voter);

        FilmBean film = new FilmBean();
        film.setNome("Film Valutazione");
        filmDAO.save(film);
        final List<FilmBean> lista = (List<FilmBean>) filmDAO.findByName(film.getNome());
        film = lista.get(0);

        recensioniService.addRecensione(reviewer.getEmail(), film.getIdFilm(), "Recensione", "Titolo", 3);

        recensioniService.addValutazione(voter.getEmail(), film.getIdFilm(), reviewer.getEmail(), true);
        recensioniService.addValutazione(voter.getEmail(), film.getIdFilm(), reviewer.getEmail(), false);

        final RecensioneBean recensione = recensioneDAO.findById(reviewer.getEmail(), film.getIdFilm());
        assertEquals(0, recensione.getNLike());
        assertEquals(1, recensione.getNDislike());

        final ValutazioneBean valutazione = valutazioneDAO.findById(voter.getEmail(), reviewer.getEmail(), film.getIdFilm());
        assertNotNull(valutazione);
        assertFalse(valutazione.isLikeDislike());
    }
}

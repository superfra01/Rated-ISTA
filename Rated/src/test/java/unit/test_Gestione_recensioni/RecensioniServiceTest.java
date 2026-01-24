package unit.test_Gestione_recensioni;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import model.DAO.FilmDAO;
import model.DAO.RecensioneDAO;
import model.DAO.ReportDAO;
import model.DAO.ValutazioneDAO;
import model.Entity.FilmBean;
import model.Entity.RecensioneBean;
import model.Entity.ReportBean;
import model.Entity.ValutazioneBean;
import sottosistemi.Gestione_Recensioni.service.RecensioniService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecensioniServiceTest {

    private RecensioniService recensioniService;
    private RecensioneDAO mockRecensioneDAO;
    private ValutazioneDAO mockValutazioneDAO;
    private ReportDAO mockReportDAO;
    private FilmDAO mockFilmDAO;

    @BeforeEach
    void setUp() {
        mockRecensioneDAO = mock(RecensioneDAO.class);
        mockValutazioneDAO = mock(ValutazioneDAO.class);
        mockReportDAO = mock(ReportDAO.class);
        mockFilmDAO = mock(FilmDAO.class);

        // Inietta tutti i DAO mockati tramite il costruttore
        recensioniService = new RecensioniService(mockRecensioneDAO, mockValutazioneDAO, mockReportDAO, mockFilmDAO);
    }

    @Test
    void testAddRecensione() {
        final String email = "user@example.com";
        final int idFilm = 1;
        final String contenuto = "Great movie!";
        final String titolo = "My Review";
        final int valutazione = 5;

        when(mockRecensioneDAO.findById(email, idFilm)).thenReturn(null);
        when(mockFilmDAO.findById(idFilm)).thenReturn(new FilmBean());
        when(mockRecensioneDAO.findByIdFilm(idFilm)).thenReturn(new ArrayList<>());

        recensioniService.addRecensione(email, idFilm, contenuto, titolo, valutazione);

        verify(mockRecensioneDAO).save(any(RecensioneBean.class));
        verify(mockFilmDAO).update(any(FilmBean.class));
    }

    @Test
    void testDeleteRecensione() {
        final String email = "user@example.com";
        final int idFilm = 1;

        final FilmBean film = new FilmBean();
        film.setIdFilm(idFilm);
        when(mockFilmDAO.findById(idFilm)).thenReturn(film);
        when(mockRecensioneDAO.findByIdFilm(idFilm)).thenReturn(new ArrayList<>());

        recensioniService.deleteRecensione(email, idFilm);

        verify(mockRecensioneDAO).delete(email, idFilm);
        verify(mockValutazioneDAO).deleteValutazioni(email, idFilm);
        verify(mockReportDAO).deleteReports(email, idFilm);
        verify(mockFilmDAO).update(film);
    }

    @Test
    void testAddValutazione_New() {
        final String email = "user@example.com";
        final int idFilm = 1;
        final String emailRecensore = "reviewer@example.com";
        final boolean nuovaValutazione = true;

        final RecensioneBean recensione = new RecensioneBean();
        recensione.setNLike(0);
        recensione.setNDislike(0);
        when(mockRecensioneDAO.findById(emailRecensore, idFilm)).thenReturn(recensione);
        when(mockValutazioneDAO.findById(email, emailRecensore, idFilm)).thenReturn(null);

        recensioniService.addValutazione(email, idFilm, emailRecensore, nuovaValutazione);

        verify(mockValutazioneDAO).save(any(ValutazioneBean.class));
        verify(mockRecensioneDAO).update(recensione);
        assertEquals(1, recensione.getNLike());
    }

    @Test
    void testFindRecensioni() {
        final String email = "user@example.com";
        final List<RecensioneBean> mockRecensioni = new ArrayList<>();
        mockRecensioni.add(new RecensioneBean());

        when(mockRecensioneDAO.findByUser(email)).thenReturn(mockRecensioni);

        final List<RecensioneBean> result = recensioniService.FindRecensioni(email);

        assertEquals(1, result.size());
        assertSame(mockRecensioni, result);
    }

    @Test
    void testGetAllRecensioniSegnalate() {
        final List<RecensioneBean> allRecensioni = new ArrayList<>();
        final RecensioneBean recensione1 = new RecensioneBean();
        recensione1.setNReports(0);
        final RecensioneBean recensione2 = new RecensioneBean();
        recensione2.setNReports(1);
        allRecensioni.add(recensione1);
        allRecensioni.add(recensione2);

        when(mockRecensioneDAO.findAll()).thenReturn(allRecensioni);

        final List<RecensioneBean> result = recensioniService.GetAllRecensioniSegnalate();

        assertEquals(1, result.size());
        assertSame(recensione2, result.get(0));
    }

    @Test
    void testReport() {
        final String email = "user@example.com";        // The user reporting the review
        final String emailRecensore = "reviewer@example.com"; // The author of the review
        final int idFilm = 1;

        // 1. Mock that the user hasn't reported this review yet
        when(mockReportDAO.findById(email, emailRecensore, idFilm)).thenReturn(null);

        // 2. FIX: Mock the existence of the review being reported
        final RecensioneBean recensioneTarget = new RecensioneBean();
        recensioneTarget.setNReports(0); 
        
        when(mockRecensioneDAO.findById(emailRecensore, idFilm)).thenReturn(recensioneTarget);

        // Action
        recensioniService.report(email, emailRecensore, idFilm);

        // Verify
        verify(mockReportDAO).save(any(ReportBean.class));
        
        // Verify that the review's report count was actually updated
        verify(mockRecensioneDAO).update(recensioneTarget); 
    }
}
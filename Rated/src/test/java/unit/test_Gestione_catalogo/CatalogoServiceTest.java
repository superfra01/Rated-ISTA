package unit.test_Gestione_catalogo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import model.DAO.FilmDAO;
import model.DAO.FilmGenereDAO;
import model.DAO.GenereDAO;
import model.Entity.FilmBean;
import model.Entity.RecensioneBean;
import sottosistemi.Gestione_Catalogo.service.CatalogoService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogoServiceTest {

    private CatalogoService catalogoService;
    private FilmDAO mockFilmDAO;
    private FilmGenereDAO mockFilmGenereDAO;
    private GenereDAO mockGenereDAO;

    @BeforeEach
    void setUp() {
        // Mock di FilmDAO
        mockFilmDAO = mock(FilmDAO.class);
        mockFilmGenereDAO = mock(FilmGenereDAO.class);
        mockGenereDAO = mock(GenereDAO.class);

        // Inizializza il servizio con il DAO mockato tramite il costruttore
        catalogoService = new CatalogoService(mockFilmDAO, mockFilmGenereDAO, mockGenereDAO);
    }

    @Test
    void testGetFilms() throws SQLException {
        // Simula una lista di film
        final List<FilmBean> mockFilms = new ArrayList<>();
        final FilmBean film1 = new FilmBean();
        final FilmBean film2 = new FilmBean();
        mockFilms.add(film1);
        mockFilms.add(film2);

        when(mockFilmDAO.findAll()).thenReturn(mockFilms);

        // Esegui il metodo
        final List<FilmBean> result = catalogoService.getFilms();

        // Verifica
        assertEquals(2, result.size());
        assertSame(mockFilms, result);
    }

    @Test
    void testAggiungiFilm() throws SQLException {
        final String nome = "Film Test";
        final int anno = 2023;
        final int durata = 120;
        final String[] generi = new String[]{"Azione"};
        final String regista = "Regista Test";
        final String attori = "Attore Test";
        final byte[] locandina = new byte[]{1, 2, 3};
        final String trama = "Trama del film.";

        // Esegui il metodo
        catalogoService.aggiungiFilm(nome, anno, durata, generi, regista, attori, locandina, trama);

        // Verifica
        verify(mockFilmDAO).save(any(FilmBean.class));
    }

    @Test
    void testRimuoviFilm() throws SQLException {
        final FilmBean film = new FilmBean();
        film.setIdFilm(1);

        // Esegui il metodo
        catalogoService.rimuoviFilm(film);

        // Verifica
        verify(mockFilmDAO).delete(1);
    }

    @Test
    void testRicercaFilm() throws SQLException {
        final String name = "Film Test";

        // Simula una lista di film trovati
        final List<FilmBean> mockFilms = new ArrayList<>();
        final FilmBean film = new FilmBean();
        mockFilms.add(film);

        when(mockFilmDAO.findByName(name)).thenReturn(mockFilms);

        // Esegui il metodo
        final List<FilmBean> result = catalogoService.ricercaFilm(name);

        // Verifica
        assertEquals(1, result.size());
        assertSame(mockFilms, result);
    }

    @Test
    void testGetFilm() throws SQLException {
        final int idFilm = 1;

        // Simula un film trovato
        final FilmBean film = new FilmBean();
        when(mockFilmDAO.findById(idFilm)).thenReturn(film);

        // Esegui il metodo
        final FilmBean result = catalogoService.getFilm(idFilm);

        // Verifica
        assertSame(film, result);
    }

    @Test
    void testModifyFilm() throws SQLException {
        // 1. Setup dei dati di input (argomenti scalari come richiesto dal metodo)
        int idFilm = 1;
        int durata = 120;
        String titolo = "Titolo Modificato";
        int anno = 2022;
        String generi[] = new String[]{"Drammatico"};
        byte[] image = "path/to/image.jpg".getBytes();
        String regista = "Regista Modificato";
        String attori = "Attori Modificati";
        String descrizione = "Nuova descrizione";

        // 2. Setup del Mock (CORREZIONE)
        // Creiamo un oggetto "filmAttuale" che simula quello già presente nel DB
        FilmBean filmAttuale = new FilmBean();
        filmAttuale.setIdFilm(idFilm);
        filmAttuale.setValutazione(4); // Valutazione (int) esistente da preservare

        // Istruiamo il Mock: quando il servizio chiede il film ID 1, restituisci filmAttuale
        when(mockFilmDAO.findById(idFilm)).thenReturn(filmAttuale);

        // 3. Esecuzione del metodo sotto test con i parametri corretti
        catalogoService.modifyFilm(idFilm, durata, titolo, anno, generi, image, regista, attori, descrizione);

        // 4. Verifica che il DAO abbia ricevuto un update
        verify(mockFilmDAO).update(any(FilmBean.class));
    }

    @Test
    void testRemoveFilm() throws SQLException {
        final int idFilm = 1;

        // Esegui il metodo
        catalogoService.removeFilm(idFilm);

        // Verifica
        verify(mockFilmDAO).delete(idFilm);
    }

    @Test
    void testGetFilmsFromRecensioni() throws SQLException {
        // Crea una lista di recensioni
        final List<RecensioneBean> recensioni = new ArrayList<>();
        final RecensioneBean recensione1 = new RecensioneBean();
        recensione1.setIdFilm(1);
        recensioni.add(recensione1);

        // Simula un film corrispondente
        final FilmBean film = new FilmBean();
        when(mockFilmDAO.findById(1)).thenReturn(film);

        // Esegui il metodo
        final HashMap<Integer, FilmBean> result = catalogoService.getFilms(recensioni);

        // Verifica
        assertEquals(1, result.size());
        assertSame(film, result.get(1));
    }
}
package unit.test_Gestione_catalogo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import model.DAO.FilmDAO;
import model.Entity.FilmBean;
import model.Entity.RecensioneBean;
import sottosistemi.Gestione_Catalogo.service.CatalogoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogoServiceTest {

    private CatalogoService catalogoService;
    private FilmDAO mockFilmDAO;

    @BeforeEach
    void setUp() {
        // Mock di FilmDAO
        mockFilmDAO = mock(FilmDAO.class);

        // Inizializza il servizio con il DAO mockato
        catalogoService = new CatalogoService(mockFilmDAO);
    }

    @Test
    void testGetFilms() {
        // Simula una lista di film
        List<FilmBean> mockFilms = new ArrayList<>();
        FilmBean film1 = new FilmBean();
        FilmBean film2 = new FilmBean();
        mockFilms.add(film1);
        mockFilms.add(film2);

        when(mockFilmDAO.findAll()).thenReturn(mockFilms);

        // Esegui il metodo
        List<FilmBean> result = catalogoService.getFilms();

        // Verifica
        assertEquals(2, result.size());
        assertSame(mockFilms, result);
    }

    @Test
    void testAggiungiFilm() {
        String nome = "Film Test";
        int anno = 2023;
        int durata = 120;
        String generi = "Azione";
        String regista = "Regista Test";
        String attori = "Attore Test";
        byte[] locandina = new byte[]{1, 2, 3};
        String trama = "Trama del film.";

        // Esegui il metodo
        catalogoService.aggiungiFilm(nome, anno, durata, generi, regista, attori, locandina, trama);

        // Verifica
        verify(mockFilmDAO).save(any(FilmBean.class));
    }

    @Test
    void testRimuoviFilm() {
        FilmBean film = new FilmBean();
        film.setIdFilm(1);

        // Esegui il metodo
        catalogoService.rimuoviFilm(film);

        // Verifica
        verify(mockFilmDAO).delete(1);
    }

    @Test
    void testRicercaFilm() {
        String name = "Film Test";

        // Simula una lista di film trovati
        List<FilmBean> mockFilms = new ArrayList<>();
        FilmBean film = new FilmBean();
        mockFilms.add(film);

        when(mockFilmDAO.findByName(name)).thenReturn(mockFilms);

        // Esegui il metodo
        List<FilmBean> result = catalogoService.ricercaFilm(name);

        // Verifica
        assertEquals(1, result.size());
        assertSame(mockFilms, result);
    }

    @Test
    void testGetFilm() {
        int idFilm = 1;

        // Simula un film trovato
        FilmBean film = new FilmBean();
        when(mockFilmDAO.findById(idFilm)).thenReturn(film);

        // Esegui il metodo
        FilmBean result = catalogoService.getFilm(idFilm);

        // Verifica
        assertSame(film, result);
    }

    @Test
    void testModifyFilm() {
        int idFilm = 1;
        int anno = 2023;
        String attori = "Attore Test";
        int durata = 120;
        String generi = "Azione";
        byte[] locandina = new byte[]{1, 2, 3};
        String nome = "Film Test";
        String regista = "Regista Test";
        String trama = "Trama modificata.";

        // Esegui il metodo
        catalogoService.modifyFilm(idFilm, anno, attori, durata, generi, locandina, nome, regista, trama);

        // Verifica
        verify(mockFilmDAO).update(any(FilmBean.class));
    }

    @Test
    void testRemoveFilm() {
        int idFilm = 1;

        // Esegui il metodo
        catalogoService.removeFilm(idFilm);

        // Verifica
        verify(mockFilmDAO).delete(idFilm);
    }
    @Test
    void testGetFilmsFromRecensioni() {
        // Crea una lista di recensioni
        List<RecensioneBean> recensioni = new ArrayList<>();
        RecensioneBean recensione1 = new RecensioneBean();
        recensione1.setIdFilm(1);
        recensioni.add(recensione1);

        // Simula un film corrispondente
        FilmBean film = new FilmBean();
        when(mockFilmDAO.findById(1)).thenReturn(film);

        // Esegui il metodo
        HashMap<Integer, FilmBean> result = catalogoService.getFilms(recensioni);

        // Verifica
        assertEquals(1, result.size());
        assertSame(film, result.get(1));
    }

}
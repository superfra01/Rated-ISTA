package sottosistemi.Gestione_Catalogo.service;

import model.DAO.FilmDAO;
import model.Entity.FilmBean;
import model.Entity.RecensioneBean;

import java.util.HashMap;
import java.util.List;

public class CatalogoService {
    private final FilmDAO FilmDAO; // Reso final
    
    public CatalogoService() {
        this.FilmDAO = new FilmDAO();
    }
    
    // Costruttore per il test o configurazioni personalizzate
    public CatalogoService(final FilmDAO filmDAO) { // Parametro final
        this.FilmDAO = filmDAO;
    }
    
    public List<FilmBean> getFilms(){
    	final List<FilmBean> films = FilmDAO.findAll(); // Variabile locale final
    	return films;
    }

    public void aggiungiFilm(final String nome, final int anno, final int durata, final String generi, final String regista, final String attori, final byte[] locandina, final String trama) { // Parametri final
        final FilmBean film = new FilmBean();
        film.setNome(nome);
        film.setAnno(anno);
        film.setDurata(durata);
        film.setGeneri(generi);
        film.setRegista(regista);
        film.setAttori(attori);
        film.setLocandina(locandina);
        film.setTrama(trama);
        FilmDAO.save(film);
    }

    public void rimuoviFilm(final FilmBean film) { // Parametro final
        FilmDAO.delete(film.getIdFilm());
    }

    public List<FilmBean> ricercaFilm(final String name) { // Parametro final
        return FilmDAO.findByName(name);
    }

    public FilmBean getFilm(final int idFilm) { // Parametro final
        return FilmDAO.findById(idFilm);
    }
    
    public HashMap<Integer, FilmBean> getFilms(final List<RecensioneBean> recensioni) { // Parametro final
    	
    	final HashMap<Integer, FilmBean> FilmMap = new HashMap<>(); // Variabile locale final
    	for(final RecensioneBean Recensione : recensioni) {
    		final int key = Recensione.getIdFilm();
    		final FilmBean Film = this.getFilm(key);
    		FilmMap.put(key, Film);
    	}
        return FilmMap;
    }
    
    public void addFilm(final int anno, final String Attori, final int durata, final String Generi, final byte[] Locandina, final String Nome, final String Regista, final String Trama){ // Parametri final
    	final FilmBean film = new FilmBean();
    	film.setAnno(anno);
    	film.setAttori(Attori);
    	film.setDurata(durata);
    	film.setGeneri(Generi);
    	film.setLocandina(Locandina);
    	film.setNome(Nome);
    	film.setRegista(Regista);
    	film.setTrama(Trama);
    	FilmDAO.save(film);
    }
    
    public void modifyFilm(final int idFilm, final int anno, final String Attori, final int durata, final String Generi, final byte[] Locandina, final String Nome, final String Regista, final String Trama){ // Parametri final
    	final FilmBean film = new FilmBean();
    	film.setIdFilm(idFilm);
    	film.setAnno(anno);
    	film.setAttori(Attori);
    	film.setDurata(durata);
    	film.setGeneri(Generi);
    	film.setLocandina(Locandina);
    	film.setNome(Nome);
    	film.setRegista(Regista);
    	film.setTrama(Trama);
    	FilmDAO.update(film);
    }
    
    public void removeFilm(final int idFilm) { // Parametro final
    	FilmDAO.delete(idFilm);
    }
}
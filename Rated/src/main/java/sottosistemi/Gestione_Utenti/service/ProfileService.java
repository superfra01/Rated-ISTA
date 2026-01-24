package sottosistemi.Gestione_Utenti.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import model.DAO.UtenteDAO;
import model.DAO.PreferenzaDAO;
import model.DAO.InteresseDAO;
import model.DAO.VistoDAO;
import model.Entity.UtenteBean;
import model.Entity.VistoBean;
import utilities.PasswordUtility;
import model.Entity.FilmBean;
import model.Entity.InteresseBean;
import model.Entity.PreferenzaBean;
import model.Entity.RecensioneBean;

public class ProfileService {
    public final UtenteDAO UtenteDAO; // Reso final
    public final PreferenzaDAO PreferenzaDAO;
    public final InteresseDAO InteresseDAO;
    public final VistoDAO VistoDAO;

    public ProfileService() {
        this.UtenteDAO = new UtenteDAO();
        this.PreferenzaDAO = new PreferenzaDAO();
        this.InteresseDAO = new InteresseDAO();
        this.VistoDAO = new VistoDAO();
    }
    
    //test
    public ProfileService(final DataSource dataSource) { // Parametro final
        this.UtenteDAO = new UtenteDAO(dataSource);
        this.PreferenzaDAO = new PreferenzaDAO(dataSource);
        this.InteresseDAO = new InteresseDAO(dataSource);
        this.VistoDAO = new VistoDAO(dataSource);
    }

    public ProfileService(final UtenteDAO utenteDAO, final PreferenzaDAO preferenzaDAO) {
        this.UtenteDAO = utenteDAO;
        this.PreferenzaDAO = preferenzaDAO;
        this.InteresseDAO = new InteresseDAO();
        this.VistoDAO = new VistoDAO();
    }
    
    public ProfileService(final UtenteDAO utenteDAO, final PreferenzaDAO PreferenzaDAO, final InteresseDAO InteresseDAO, final VistoDAO VistoDAO) { // Parametro final
        this.UtenteDAO = utenteDAO;
        this.PreferenzaDAO = PreferenzaDAO;
        this.InteresseDAO = InteresseDAO;
        this.VistoDAO = VistoDAO;
    }
    
    public UtenteBean ProfileUpdate(final String username, final String email, final String password, final String biografia, final byte[] icon) { // Parametri final
    	
    	final UtenteBean u = UtenteDAO.findByUsername(username); // Variabile locale final
    	if(u != null && !(u.getEmail().equals(email)))
    		return null;
    	
    	final UtenteBean user = UtenteDAO.findByEmail(email); // Variabile locale final
    	user.setUsername(username);
    	user.setPassword(PasswordUtility.hashPassword(password));
    	user.setBiografia(biografia);
    	user.setIcona(icon);
    	UtenteDAO.update(user);
    	
    	return user;
    }
    
    public UtenteBean PasswordUpdate(final String email, final String password) { // Parametri final
    	
    	final UtenteBean user = UtenteDAO.findByEmail(email); // Variabile locale final
    	if(user == null)
    		return null;
    	
    	user.setPassword(PasswordUtility.hashPassword(password));
    	UtenteDAO.update(user);
    	
    	return user;
    }
    
    public UtenteBean findByUsername(final String username) { // Parametro final
    	return UtenteDAO.findByUsername(username);
    }
    
    public HashMap<String, String> getUsers(final List<RecensioneBean> recensioni) { // Parametro final
    	final HashMap<String, String> users = new HashMap<String, String>(); // Variabile locale final
    	for(final RecensioneBean recensione: recensioni) { // Variabile loop final
    		final String email = recensione.getEmail(); // Variabile locale final
    		final String username = UtenteDAO.findByEmail(email).getUsername(); // Variabile locale final
    		users.put(email, username);
    	}
    	return users;
    }
    
    public List<String> getPreferenze(final String email){
    	List<PreferenzaBean> preferenze = PreferenzaDAO.findByEmail(email);
    	List<String> preferenzeString = new ArrayList<String>();
    	for(PreferenzaBean b : preferenze)
    		preferenzeString.add(b.getNomeGenere());
    	return preferenzeString;
    }
    
    public void addPreferenza(final String email, final String genere) {
    	PreferenzaBean preferenza = new PreferenzaBean(email, genere);
    	PreferenzaDAO.save(preferenza);
    }
    
    public void aggiungiAllaWatchlist(String email, int filmId){

        InteresseBean interesse = new InteresseBean();

        // Configurazione del bean
        interesse.setEmail(email);
        interesse.setIdFilm(filmId);
        
        // Imposta true per indicare che è una "Watchlist" (o interesse positivo)
        interesse.setInteresse(true);

        // Chiama il DAO per il salvataggio
        InteresseDAO.save(interesse);
    }
    
    public void aggiungiFilmVisto(String email, int filmId){
        
        VistoBean visto = new VistoBean();
        visto.setEmail(email);
        visto.setIdFilm(filmId);

        // Salva nel database
        VistoDAO.save(visto);
    }
    
    public void aggiornaPreferenzeUtente(String email, String[] idGeneri){
        

        // 1. Elimina TUTTE le preferenze precedenti per questo utente
        // Questo garantisce che se l'utente deseleziona tutto, il DB rifletta lo stato vuoto
        PreferenzaDAO.deleteByEmail(email);

        	
        // 2. Se ci sono nuovi generi selezionati, inseriscili uno per uno
        if (idGeneri != null && idGeneri.length > 0) {
            for (String idGenereStr : idGeneri) {
                try {
                    
                    PreferenzaBean preferenza = new PreferenzaBean();
                    preferenza.setEmail(email);
                    preferenza.setNomeGenere(idGenereStr);;
                    
                    PreferenzaDAO.save(preferenza);
                    
                } catch (NumberFormatException e) {
                    // Logga l'errore ma continua con gli altri generi
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void ignoreFilm(String email, int filmId){

        InteresseBean interesse = new InteresseBean();

        // Configurazione del bean
        interesse.setEmail(email);
        interesse.setIdFilm(filmId);
        
        // Imposta true per indicare che è una "Watchlist" (o interesse positivo)
        interesse.setInteresse(false);

        // Chiama il DAO per il salvataggio
        InteresseDAO.save(interesse);
    }
    
    public List<FilmBean> retrieveWatchedFilms(final String username) {

    	return VistoDAO.doRetrieveFilmsByUtente(username);
        
    }


    public List<FilmBean> retrieveWatchlist(final String username1) {
        
        return this.InteresseDAO.doRetrieveFilmsByUtente(username1);

    }
    
    public boolean isFilmInWatchlist(String email, int filmId) {
        InteresseDAO interesseDAO = new InteresseDAO();
            InteresseBean interesseBean = interesseDAO.findByEmailAndIdFilm(email, filmId);
            return interesseBean.isInteresse();

    }

    public void rimuoviDallaWatchlist(String email, int filmId) {
        InteresseDAO interesseDAO = new InteresseDAO();
        interesseDAO.delete(email, filmId);
    }
    
 // Inserire dentro ProfileService.java

    /**
     * Verifica se un film è presente nella lista dei visti dell'utente.
     */
    public boolean isFilmVisto(String email, int filmId) {
        model.DAO.VistoDAO vistoDAO = new model.DAO.VistoDAO();
        return vistoDAO.findByEmailAndIdFilm(email, filmId) != null;
    }

    /**
     * Rimuove un film dalla lista dei visti dell'utente.
     */
    public void rimuoviFilmVisto(String email, int filmId) {
        model.DAO.VistoDAO vistoDAO = new model.DAO.VistoDAO();
        vistoDAO.delete(email, filmId);

    }
}

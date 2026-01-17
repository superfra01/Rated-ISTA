package sottosistemi.Gestione_Utenti.service;

import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import model.DAO.UtenteDAO;
import model.Entity.UtenteBean;
import utilities.PasswordUtility;
import model.Entity.RecensioneBean;

public class ProfileService {
    public final UtenteDAO UtenteDAO; // Reso final

    public ProfileService() {
        this.UtenteDAO = new UtenteDAO();
    }
    
    //test
    public ProfileService(final DataSource dataSource) { // Parametro final
        this.UtenteDAO = new UtenteDAO(dataSource);
    }
    
    public ProfileService(final UtenteDAO utenteDAO) { // Parametro final
        this.UtenteDAO = utenteDAO;
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
}
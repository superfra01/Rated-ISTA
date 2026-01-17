package sottosistemi.Gestione_Utenti.service;

import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import model.DAO.UtenteDAO;
import model.Entity.UtenteBean;
import utilities.PasswordUtility;
import model.Entity.RecensioneBean;



public class ProfileService {
    public UtenteDAO UtenteDAO;
    

    public ProfileService() {
        this.UtenteDAO = new UtenteDAO();
        
    }
    //test
    public ProfileService(DataSource dataSource) {
        this.UtenteDAO = new UtenteDAO(dataSource);
    }
    
    public UtenteBean ProfileUpdate(String username, String email, String password, String biografia, byte[] icon) {
    	
    	UtenteBean u= UtenteDAO.findByUsername(username);
    	if(u!=null&& !(u.getEmail().equals(email)))
    		return null;
    	UtenteBean user = UtenteDAO.findByEmail(email);
    	user.setUsername(username);
    	user.setPassword(PasswordUtility.hashPassword(password));
    	user.setBiografia(biografia);
    	user.setIcona(icon);
    	UtenteDAO.update(user);
    	
    	return user;
    }
    
    public UtenteBean PasswordUpdate(String email, String password) {
    	
    	UtenteBean user = UtenteDAO.findByEmail(email);
    	if(user==null)
    		return null;
    	
    	user.setPassword(PasswordUtility.hashPassword(password));
    	UtenteDAO.update(user);
    	
    	return user;
    }
    
    
    public UtenteBean findByUsername(String username) {
    	return UtenteDAO.findByUsername(username);
    }
    
    public HashMap<String, String> getUsers(List<RecensioneBean> recensioni){
    	HashMap<String, String> users = new HashMap<String, String>();
    	for(RecensioneBean recensione: recensioni) {
    		String email = recensione.getEmail();
    		String username = UtenteDAO.findByEmail(email).getUsername();
    		users.put(email, username);
    		
    	}
    	return users;
    }
}

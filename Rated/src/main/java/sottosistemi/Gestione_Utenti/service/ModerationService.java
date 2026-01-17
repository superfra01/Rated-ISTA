package sottosistemi.Gestione_Utenti.service;

import model.DAO.UtenteDAO;
import model.Entity.UtenteBean;

public class ModerationService {
    public UtenteDAO UtenteDAO;
    

    public ModerationService() {
        this.UtenteDAO = new UtenteDAO();
        
    }
    
    // Costruttore per il test
    public ModerationService(UtenteDAO utenteDAO) {
        this.UtenteDAO = utenteDAO;
    }
    
    public void warn(String email) {
    	UtenteBean user = UtenteDAO.findByEmail(email);
    	if(user!=null) {
    		user.setNWarning(user.getNWarning()+1);
        	UtenteDAO.update(user);
    	}
    	
    }
    
    
    
}
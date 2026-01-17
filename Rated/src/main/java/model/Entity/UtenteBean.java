package model.Entity;

import java.io.Serializable;

public class UtenteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email;
    private byte[] icona;
    private String username;
    private String password;
    private String tipoUtente;
    private int nWarning;
    private String biografia;

    public UtenteBean() {
        email = "";
        icona = null;
        username = "";
        password = "";
        tipoUtente = "";
        nWarning = 0;
        biografia = "";
    }

    public UtenteBean(String email, byte[] icona, String username, String password, String tipoUtente, int nWarning, String biografia) {
        this.email = email;
        this.icona = icona;
        this.username = username;
        this.password = password;
        this.tipoUtente = tipoUtente;
        this.nWarning = nWarning;
        this.biografia = biografia;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getIcona() {
        return icona;
    }

    public void setIcona(byte[] icona) {
        this.icona = icona;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTipoUtente() {
        return tipoUtente;
    }

    public void setTipoUtente(String tipoUtente) {
        this.tipoUtente = tipoUtente;
    }

    public int getNWarning() {
        return nWarning;
    }

    public void setNWarning(int nWarning) {
        this.nWarning = nWarning;
    }
    
    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }
}
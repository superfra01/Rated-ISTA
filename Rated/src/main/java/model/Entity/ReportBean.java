package model.Entity;

import java.io.Serializable;

public class ReportBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String email;
    private String emailRecensore;
    private int idFilm;

    public ReportBean() {
        email = "";
        emailRecensore = "";
        idFilm = 0;
    }

    public ReportBean(String email, String emailRecensore, int idFilm) {
        this.email = email;
        this.emailRecensore = emailRecensore;
        this.idFilm = idFilm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailRecensore() {
        return emailRecensore;
    }

    public void setEmailRecensore(String emailRecensore) {
        this.emailRecensore = emailRecensore;
    }

    public int getIdFilm() {
        return idFilm;
    }

    public void setIdFilm(int idFilm) {
        this.idFilm = idFilm;
    }
}
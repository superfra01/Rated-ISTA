package model.Entity;

import java.io.Serializable;

public class RecensioneBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String titolo;
    private String contenuto;
    private int valutazione;
    private int nLike;
    private int nDislike;
    private int nReports;
    private String email;
    private int idFilm;

    public RecensioneBean() {
        titolo = "";
        contenuto = "";
        valutazione = 0;
        nLike = 0;
        nDislike = 0;
        nReports = 0;
        email = "";
        idFilm = 0;
    }

    public RecensioneBean(String titolo, String contenuto, int valutazione, int nLike, int nDislike, int nReports, String email, int idFilm) {
        this.titolo = titolo;
        this.contenuto = contenuto;
        this.valutazione = valutazione;
        this.nLike = nLike;
        this.nDislike = nDislike;
        this.nReports = nReports;
        this.email = email;
        this.idFilm = idFilm;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getContenuto() {
        return contenuto;
    }

    public void setContenuto(String contenuto) {
        this.contenuto = contenuto;
    }

    public int getValutazione() {
        return valutazione;
    }

    public void setValutazione(int valutazione) {
        this.valutazione = valutazione;
    }

    public int getNLike() {
        return nLike;
    }

    public void setNLike(int nLike) {
        this.nLike = nLike;
    }

    public int getNDislike() {
        return nDislike;
    }

    public void setNDislike(int nDislike) {
        this.nDislike = nDislike;
    }

    public int getNReports() {
        return nReports;
    }

    public void setNReports(int nReports) {
        this.nReports = nReports;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getIdFilm() {
        return idFilm;
    }

    public void setIdFilm(int idFilm) {
        this.idFilm = idFilm;
    }
}

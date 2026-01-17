package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import model.Entity.ValutazioneBean;

public class ValutazioneDAO {

    private DataSource dataSource;

    public ValutazioneDAO() {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            this.dataSource = (DataSource) envCtx.lookup("jdbc/RatedDB");
        } catch (NamingException e) {
            throw new RuntimeException("Error initializing DataSource: " + e.getMessage());
        }
    }
    //test
    public ValutazioneDAO(DataSource testDataSource) {
    	dataSource= testDataSource;
	}

	public void save(ValutazioneBean valutazione) {
        String selectQuery = "SELECT * FROM Valutazione WHERE email = ? AND email_Recensore = ? AND ID_Film = ?";
        String insertQuery = "INSERT INTO Valutazione (Like_Dislike, email, email_Recensore, ID_Film) VALUES (?, ?, ?, ?)";
        String updateQuery = "UPDATE Valutazione SET Like_Dislike = ? WHERE email = ? AND email_Recensore = ? AND ID_Film = ?";
        String deleteQuery = "DELETE FROM Valutazione WHERE email = ? AND email_Recensore = ? AND ID_Film = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectPs = connection.prepareStatement(selectQuery);
             PreparedStatement insertPs = connection.prepareStatement(insertQuery);
             PreparedStatement updatePs = connection.prepareStatement(updateQuery);
             PreparedStatement deletePs = connection.prepareStatement(deleteQuery)) {

            // Check if the record exists
            selectPs.setString(1, valutazione.getEmail());
            selectPs.setString(2, valutazione.getEmailRecensore());
            selectPs.setInt(3, valutazione.getIdFilm());

            try (ResultSet rs = selectPs.executeQuery()) {
                if (rs.next()) {
                    // Record exists, check if the value is the same
                    boolean existingLikeDislike = rs.getBoolean("Like_Dislike");
                    if (existingLikeDislike == valutazione.isLikeDislike()) {
                        // If the same, delete the record
                        deletePs.setString(1, valutazione.getEmail());
                        deletePs.setString(2, valutazione.getEmailRecensore());
                        deletePs.setInt(3, valutazione.getIdFilm());
                        deletePs.executeUpdate();
                    } else {
                        // If different, update the record
                        updatePs.setBoolean(1, valutazione.isLikeDislike());
                        updatePs.setString(2, valutazione.getEmail());
                        updatePs.setString(3, valutazione.getEmailRecensore());
                        updatePs.setInt(4, valutazione.getIdFilm());
                        updatePs.executeUpdate();
                    }
                } else {
                    // Record does not exist, insert it
                    insertPs.setBoolean(1, valutazione.isLikeDislike());
                    insertPs.setString(2, valutazione.getEmail());
                    insertPs.setString(3, valutazione.getEmailRecensore());
                    insertPs.setInt(4, valutazione.getIdFilm());
                    insertPs.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ValutazioneBean findById(String email, String emailRecensore, int idFilm) {
        String query = "SELECT * FROM Valutazione WHERE email = ? AND email_Recensore = ? AND ID_Film = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, emailRecensore);
            ps.setInt(3, idFilm);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ValutazioneBean valutazione = new ValutazioneBean();
                    valutazione.setLikeDislike(rs.getBoolean("Like_Dislike"));
                    valutazione.setEmail(rs.getString("email"));
                    valutazione.setEmailRecensore(rs.getString("email_Recensore"));
                    valutazione.setIdFilm(rs.getInt("ID_Film"));
                    return valutazione;
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public HashMap<String, ValutazioneBean> findByIdFilmAndEmail(int idFilm, String email) {
        String query = "SELECT * FROM Valutazione WHERE ID_Film = ? AND email = ?";
        HashMap<String, ValutazioneBean> valutazioni = new HashMap<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idFilm);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ValutazioneBean valutazione = new ValutazioneBean();
                    valutazione.setLikeDislike(rs.getBoolean("Like_Dislike"));
                    valutazione.setEmail(rs.getString("email"));
                    valutazione.setEmailRecensore(rs.getString("email_Recensore"));
                    valutazione.setIdFilm(rs.getInt("ID_Film"));
                    valutazioni.put(rs.getString("email_Recensore"), valutazione);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return valutazioni;
    }
    

    public void delete(String email, String emailRecensore, int idFilm) {
        String query = "DELETE FROM Valutazione WHERE email = ? AND email_Recensore = ? AND ID_Film = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, emailRecensore);
            ps.setInt(3, idFilm);
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //cancella le valutazioni di un recensione
    public void deleteValutazioni(String emailRecensore, int idFilm) {
        String query = "DELETE FROM Valutazione WHERE email_Recensore = ? AND ID_Film = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, emailRecensore);
            ps.setInt(2, idFilm);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
package model.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import model.Entity.FilmBean;

public class FilmDAO {

    private DataSource dataSource;

    public FilmDAO() {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            this.dataSource = (DataSource) envCtx.lookup("jdbc/RatedDB");
        } catch (NamingException e) {
            throw new RuntimeException("Error initializing DataSource: " + e.getMessage());
        }
    }

    public FilmDAO(DataSource testDataSource) {
    	dataSource = testDataSource;
	}

	public void save(FilmBean film) {
        String query = "INSERT INTO Film (ID_Film, locandina, nome, anno, durata, generi, regista, attori, valutazione, trama) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, film.getIdFilm());
            ps.setBytes(2, film.getLocandina());
            ps.setString(3, film.getNome());
            ps.setInt(4, film.getAnno());
            ps.setInt(5, film.getDurata());
            ps.setString(6, film.getGeneri());
            ps.setString(7, film.getRegista());
            ps.setString(8, film.getAttori());
            ps.setInt(9, film.getValutazione());
            ps.setString(10, film.getTrama());
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public FilmBean findById(int idFilm) {
        String query = "SELECT * FROM Film WHERE ID_Film = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idFilm);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FilmBean film = new FilmBean();
                    film.setIdFilm(rs.getInt("ID_Film"));
                    film.setLocandina(rs.getBytes("locandina"));
                    film.setNome(rs.getString("nome"));
                    film.setAnno(rs.getInt("anno"));
                    film.setDurata(rs.getInt("durata"));
                    film.setGeneri(rs.getString("generi"));
                    film.setRegista(rs.getString("regista"));
                    film.setAttori(rs.getString("attori"));
                    film.setValutazione(rs.getInt("valutazione"));
                    film.setTrama(rs.getString("trama"));
                    return film;
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<FilmBean> findByName(String name) {
        String query = "SELECT * FROM Film WHERE nome LIKE ?";
        List<FilmBean> films = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FilmBean film = new FilmBean();
                    film.setIdFilm(rs.getInt("ID_Film"));
                    film.setLocandina(rs.getBytes("locandina"));
                    film.setNome(rs.getString("nome"));
                    film.setAnno(rs.getInt("anno"));
                    film.setDurata(rs.getInt("durata"));
                    film.setGeneri(rs.getString("generi"));
                    film.setRegista(rs.getString("regista"));
                    film.setAttori(rs.getString("attori"));
                    film.setValutazione(rs.getInt("valutazione"));
                    film.setTrama(rs.getString("trama"));
                    films.add(film);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }

    public List<FilmBean> findAll() {
        String query = "SELECT * FROM Film";
        List<FilmBean> films = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FilmBean film = new FilmBean();
                film.setIdFilm(rs.getInt("ID_Film"));
                film.setLocandina(rs.getBytes("locandina"));
                film.setNome(rs.getString("nome"));
                film.setAnno(rs.getInt("anno"));
                film.setDurata(rs.getInt("durata"));
                film.setGeneri(rs.getString("generi"));
                film.setRegista(rs.getString("regista"));
                film.setAttori(rs.getString("attori"));
                film.setValutazione(rs.getInt("valutazione"));
                film.setTrama(rs.getString("trama"));
                films.add(film);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }

    public void update(FilmBean film) {
        String query = "UPDATE Film SET locandina = ?, nome = ?, anno = ?, durata = ?, generi = ?, regista = ?, attori = ?, valutazione = ?, trama = ? WHERE ID_Film = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setBytes(1, film.getLocandina());
            ps.setString(2, film.getNome());
            ps.setInt(3, film.getAnno());
            ps.setInt(4, film.getDurata());
            ps.setString(5, film.getGeneri());
            ps.setString(6, film.getRegista());
            ps.setString(7, film.getAttori());
            ps.setInt(8, film.getValutazione());
            ps.setString(9, film.getTrama());
            ps.setInt(10, film.getIdFilm());
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int idFilm) {
        String query = "DELETE FROM Film WHERE ID_Film = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idFilm);
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

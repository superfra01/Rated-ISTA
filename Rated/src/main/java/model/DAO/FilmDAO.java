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
            final Context initCtx = new InitialContext();
            final Context envCtx = (Context) initCtx.lookup("java:comp/env");
            this.dataSource = (DataSource) envCtx.lookup("jdbc/RatedDB");
        } catch (final NamingException e) {
            throw new RuntimeException("Error initializing DataSource: " + e.getMessage());
        }
    }

    public FilmDAO(final DataSource testDataSource) { // Parametro final
        dataSource = testDataSource;
    }

    protected FilmDAO(final boolean testMode) { // Parametro final
        // Vuoto: non fa nulla
    }

    public void save(final FilmBean film) {
    // Rimuoviamo ID_Film dalla insert, ci pensa il database a generarlo
    final String query = "INSERT INTO Film (locandina, nome, anno, durata, regista, attori, valutazione, trama) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (final Connection connection = dataSource.getConnection();
         // Aggiungiamo il flag RETURN_GENERATED_KEYS
         final PreparedStatement ps = connection.prepareStatement(query, java.sql.Statement.RETURN_GENERATED_KEYS)) {

        // Nota: gli indici scalano di 1 perché abbiamo tolto l'ID dalla query
        ps.setBytes(1, film.getLocandina());
        ps.setString(2, film.getNome());
        ps.setInt(3, film.getAnno());
        ps.setInt(4, film.getDurata());
        ps.setString(5, film.getRegista());
        ps.setString(6, film.getAttori());
        ps.setInt(7, film.getValutazione());
        ps.setString(8, film.getTrama());

        ps.executeUpdate();

        // RECUPERO DELL'ID GENERATO
        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                // Aggiorniamo il bean con il vero ID creato dal DB
                film.setIdFilm(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creazione film fallita, nessun ID ottenuto.");
            }
        }

    } catch (final SQLException e) {
        e.printStackTrace();
    }
}

    public FilmBean findById(final int idFilm) { 
        final String query = "SELECT * FROM Film WHERE ID_Film = ?";

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idFilm);

            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final FilmBean film = new FilmBean();
                    film.setIdFilm(rs.getInt("ID_Film"));
                    film.setLocandina(rs.getBytes("locandina"));
                    film.setNome(rs.getString("nome"));
                    film.setAnno(rs.getInt("anno"));
                    film.setDurata(rs.getInt("durata"));
                    film.setRegista(rs.getString("regista"));
                    film.setAttori(rs.getString("attori"));
                    film.setValutazione(rs.getInt("valutazione"));
                    film.setTrama(rs.getString("trama"));
                    return film;
                }
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<FilmBean> findByName(final String name) { 
        final String query = "SELECT * FROM Film WHERE nome LIKE ?";
        final List<FilmBean> films = new ArrayList<>();

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, name + "%");

            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final FilmBean film = new FilmBean();
                    film.setIdFilm(rs.getInt("ID_Film"));
                    film.setLocandina(rs.getBytes("locandina"));
                    film.setNome(rs.getString("nome"));
                    film.setAnno(rs.getInt("anno"));
                    film.setDurata(rs.getInt("durata"));
                    film.setRegista(rs.getString("regista"));
                    film.setAttori(rs.getString("attori"));
                    film.setValutazione(rs.getInt("valutazione"));
                    film.setTrama(rs.getString("trama"));
                    films.add(film);
                }
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return films;
    }

    public List<FilmBean> findAll() {
        final String query = "SELECT * FROM Film";
        final List<FilmBean> films = new ArrayList<>();

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(query);
             final ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                final FilmBean film = new FilmBean();
                film.setIdFilm(rs.getInt("ID_Film"));
                film.setLocandina(rs.getBytes("locandina"));
                film.setNome(rs.getString("nome"));
                film.setAnno(rs.getInt("anno"));
                film.setDurata(rs.getInt("durata"));
                film.setRegista(rs.getString("regista"));
                film.setAttori(rs.getString("attori"));
                film.setValutazione(rs.getInt("valutazione"));
                film.setTrama(rs.getString("trama"));
                films.add(film);
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return films;
    }

    public void update(final FilmBean film) { 
        final String query = "UPDATE Film SET locandina = ?, nome = ?, anno = ?, durata = ?, regista = ?, attori = ?, valutazione = ?, trama = ? "
                           + "WHERE ID_Film = ?";

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setBytes(1, film.getLocandina());
            ps.setString(2, film.getNome());
            ps.setInt(3, film.getAnno());
            ps.setInt(4, film.getDurata());
            ps.setString(5, film.getRegista());
            ps.setString(6, film.getAttori());
            ps.setInt(7, film.getValutazione());
            ps.setString(8, film.getTrama());
            ps.setInt(9, film.getIdFilm());

            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(final int idFilm) { 
        final String query = "DELETE FROM Film WHERE ID_Film = ?";

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idFilm);
            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recupera qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq
     */
    public List<String> findGeneriByIdFilm(final int idFilm) {
    final String query = "SELECT Nome_Genere FROM Film_Genere WHERE ID_Film = ? ORDER BY Nome_Genere";
    final List<String> generi = new ArrayList<>();

    try (final Connection connection = dataSource.getConnection();
         final PreparedStatement ps = connection.prepareStatement(query)) {

        ps.setInt(1, idFilm);

        try (final ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                generi.add(rs.getString("Nome_Genere"));
            }
        }

    } catch (final SQLException e) {
        e.printStackTrace();
    }

    return generi;
}

}

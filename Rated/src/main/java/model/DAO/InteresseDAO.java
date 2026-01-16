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

import model.Entity.InteresseBean;

public class InteresseDAO {

    private DataSource dataSource;

    public InteresseDAO() {
        try {
            final Context initCtx = new InitialContext();
            final Context envCtx = (Context) initCtx.lookup("java:comp/env");
            this.dataSource = (DataSource) envCtx.lookup("jdbc/RatedDB");
        } catch (final NamingException e) {
            throw new RuntimeException("Error initializing DataSource: " + e.getMessage());
        }
    }
    
    public InteresseDAO(final DataSource testDataSource) { // Parametro final
        dataSource = testDataSource;
    }

    public void save(final InteresseBean interesseBean) {
        final String selectQuery = "SELECT 1 FROM Interesse WHERE email = ? AND ID_Film = ?";
        final String insertQuery = "INSERT INTO Interesse (email, ID_Film, interesse) VALUES (?, ?, ?)";
        final String updateQuery = "UPDATE Interesse SET interesse = ? WHERE email = ? AND ID_Film = ?";

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement selectPs = connection.prepareStatement(selectQuery);
             final PreparedStatement insertPs = connection.prepareStatement(insertQuery);
             final PreparedStatement updatePs = connection.prepareStatement(updateQuery)) {

            selectPs.setString(1, interesseBean.getEmail());
            selectPs.setInt(2, interesseBean.getIdFilm());

            try (final ResultSet rs = selectPs.executeQuery()) {
                if (rs.next()) {
                    updatePs.setBoolean(1, interesseBean.isInteresse());
                    updatePs.setString(2, interesseBean.getEmail());
                    updatePs.setInt(3, interesseBean.getIdFilm());
                    updatePs.executeUpdate();
                } else {
                    insertPs.setString(1, interesseBean.getEmail());
                    insertPs.setInt(2, interesseBean.getIdFilm());
                    insertPs.setBoolean(3, interesseBean.isInteresse());
                    insertPs.executeUpdate();
                }
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public InteresseBean findByEmailAndIdFilm(final String email, final int idFilm) {
        final String query = "SELECT * FROM Interesse WHERE email = ? AND ID_Film = ?";

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, email);
            ps.setInt(2, idFilm);

            try (final ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    final InteresseBean interesse = new InteresseBean();
                    interesse.setEmail(rs.getString("email"));
                    interesse.setIdFilm(rs.getInt("ID_Film"));
                    interesse.setInteresse(rs.getBoolean("interesse"));
                    return interesse;
                }
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<InteresseBean> findByEmail(final String email) {
        final String query = "SELECT * FROM Interesse WHERE email = ?";
        final List<InteresseBean> interessi = new ArrayList<>();

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, email);

            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final InteresseBean interesse = new InteresseBean();
                    interesse.setEmail(rs.getString("email"));
                    interesse.setIdFilm(rs.getInt("ID_Film"));
                    interesse.setInteresse(rs.getBoolean("interesse"));
                    interessi.add(interesse);
                }
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return interessi;
    }

    public List<InteresseBean> findInterestedByEmail(final String email) {
        final String query = "SELECT * FROM Interesse WHERE email = ? AND interesse = TRUE";
        final List<InteresseBean> interessi = new ArrayList<>();

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, email);

            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final InteresseBean interesse = new InteresseBean();
                    interesse.setEmail(rs.getString("email"));
                    interesse.setIdFilm(rs.getInt("ID_Film"));
                    interesse.setInteresse(rs.getBoolean("interesse"));
                    interessi.add(interesse);
                }
            }

        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return interessi;
    }

    public void delete(final String email, final int idFilm) {
        final String query = "DELETE FROM Interesse WHERE email = ? AND ID_Film = ?";

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, email);
            ps.setInt(2, idFilm);
            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByEmail(final String email) {
        final String query = "DELETE FROM Interesse WHERE email = ?";

        try (final Connection connection = dataSource.getConnection();
             final PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, email);
            ps.executeUpdate();

        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }
}

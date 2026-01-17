package integration;

import org.h2.jdbcx.JdbcDataSource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseSetupForTest {

    // Modifica: Il metodo non è più void, ma restituisce un DataSource
    public static DataSource getH2DataSource() {
        try {
            JdbcDataSource ds = new JdbcDataSource();
            // Aggiunto MODE=MySQL per supportare tipi come YEAR e LONGBLOB
            ds.setURL("jdbc:h2:mem:RatedDB;DB_CLOSE_DELAY=-1;MODE=MySQL");
            ds.setUser("sa");
            ds.setPassword("");

            // Creazione dello schema (Tabelle)
            try (Connection conn = ds.getConnection()) {
                String schemaCreationScript = """
                    -- 1. Tabella Utente_Registrato
                    CREATE TABLE IF NOT EXISTS Utente_Registrato (
                        email VARCHAR(255) NOT NULL PRIMARY KEY,
                        Icona LONGBLOB,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        Password VARCHAR(255) NOT NULL,
                        Tipo_Utente VARCHAR(50),
                        N_Warning INT,
                        Biografia TEXT
                    );

                    -- 2. Tabella Film
                    CREATE TABLE IF NOT EXISTS Film (
                        ID_Film INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
                        Locandina LONGBLOB,
                        Nome VARCHAR(255) NOT NULL,
                        Anno YEAR NOT NULL,
                        Durata INT NOT NULL,
                        Generi VARCHAR(255),
                        Regista VARCHAR(255),
                        Trama VARCHAR(255),
                        Valutazione INT DEFAULT 1 CHECK (Valutazione BETWEEN 1 AND 5),
                        Attori TEXT
                    );

                    -- 3. Tabella Recensione
                    CREATE TABLE IF NOT EXISTS Recensione (
                        Titolo VARCHAR(255) NOT NULL,
                        Contenuto TEXT,
                        Valutazione INT NOT NULL CHECK (Valutazione BETWEEN 1 AND 5),
                        N_Like INT DEFAULT 0,
                        N_DisLike INT DEFAULT 0,
                        N_Reports INT DEFAULT 0,
                        email VARCHAR(255) NOT NULL,
                        ID_Film INT NOT NULL,
                        PRIMARY KEY (email, ID_Film),
                        FOREIGN KEY (email) REFERENCES Utente_Registrato(email) ON DELETE CASCADE,
                        FOREIGN KEY (ID_Film) REFERENCES Film(ID_Film) ON DELETE CASCADE
                    );

                    -- 4. Tabella Valutazione
                    CREATE TABLE IF NOT EXISTS Valutazione (
                        Like_Dislike BOOLEAN NOT NULL,
                        email VARCHAR(255) NOT NULL,
                        email_Recensore VARCHAR(255) NOT NULL,
                        ID_Film INT NOT NULL,
                        PRIMARY KEY (email, email_Recensore, ID_Film),
                        FOREIGN KEY (email) REFERENCES Utente_Registrato(email),
                        FOREIGN KEY (email_Recensore, ID_Film) REFERENCES Recensione(email, ID_Film) ON DELETE CASCADE
                    );

                    -- 5. Tabella Report
                    CREATE TABLE IF NOT EXISTS Report (
                        email VARCHAR(255) NOT NULL,
                        email_Recensore VARCHAR(255) NOT NULL,
                        ID_Film INT NOT NULL,
                        PRIMARY KEY (email, email_Recensore, ID_Film),
                        FOREIGN KEY (email) REFERENCES Utente_Registrato(email) ON DELETE CASCADE,
                        FOREIGN KEY (email_Recensore, ID_Film) REFERENCES Recensione(email, ID_Film) ON DELETE CASCADE
                    );
                """;
                conn.createStatement().execute(schemaCreationScript);
            }
            
            // Ritorniamo l'oggetto DataSource configurato
            return ds;

        } catch (SQLException e) {
            throw new RuntimeException("Errore nella configurazione del DataSource H2", e);
        }
    }
}
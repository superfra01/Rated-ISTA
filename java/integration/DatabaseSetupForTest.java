package integration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

public class DatabaseSetupForTest {

    private static JdbcDataSource dataSource;

    public static void configureH2() {
        try {
            // 1. Imposta le proprietà di sistema per dire a Java di usare il JNDI di Tomcat (catalina)
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");

            // 2. Crea e configura il DataSource H2 (database in memoria)
            // Lo salviamo nel campo statico 'dataSource' per poterlo restituire con getH2DataSource()
            dataSource = new JdbcDataSource();
            dataSource.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MYSQL");
            dataSource.setUser("sa");
            dataSource.setPassword("");

            // 3. Inizializza il contesto JNDI
            Context context = new InitialContext();

            // 4. Crea i sotto-contesti necessari (java:comp/env/jdbc)
            try {
                context.createSubcontext("java:");
            } catch (NamingException e) { /* Ignora se esiste già */ }
            try {
                context.createSubcontext("java:comp");
            } catch (NamingException e) { /* Ignora se esiste già */ }
            try {
                context.createSubcontext("java:comp/env");
            } catch (NamingException e) { /* Ignora se esiste già */ }
            try {
                context.createSubcontext("java:comp/env/jdbc");
            } catch (NamingException e) { /* Ignora se esiste già */ }

            // 5. Collega (bind) il DataSource H2 al nome che DriverManagerConnectionPool cerca
            context.rebind("java:comp/env/jdbc/RatedDB", dataSource);

        } catch (NamingException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore durante la configurazione JNDI per i test", e);
        }
    }

    /**
     * Metodo reintrodotto per compatibilità con i test esistenti.
     * Restituisce il DataSource configurato.
     */
    public static DataSource getH2DataSource() {
        if (dataSource == null) {
            configureH2();
        }
        return dataSource;
    }
}
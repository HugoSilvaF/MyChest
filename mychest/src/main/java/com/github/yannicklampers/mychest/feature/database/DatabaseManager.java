package com.github.yannicklampers.mychest.feature.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database manager for the MySQL feature module.
 * Initializes HikariCP connection pool from config.yml settings.
 */
public class DatabaseManager {

    private final JavaPlugin plugin;
    private final Logger logger;
    private HikariDataSource dataSource;

    /**
     * Creates a new DatabaseManager instance.
     *
     * @param plugin the JavaPlugin instance
     */
    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    /**
     * Initialize the connection pool using config.yml settings.
     * Expected config structure:
     * <pre>
     * mysql:
     *   host: localhost
     *   port: 3306
     *   database: mychest
     *   user: root
     *   password: password
     *   poolSize: 10
     *   useSSL: false
     * </pre>
     */
    public void initialize() {
        FileConfiguration config = plugin.getConfig();

        String host = config.getString("mysql.host", "localhost");
        int port = config.getInt("mysql.port", 3306);
        String database = config.getString("mysql.database", "mychest");
        String user = config.getString("mysql.user", config.getString("mysql.username", "root"));
        String password = config.getString("mysql.password", "");
        int poolSize = config.getInt("mysql.poolSize", 10);
        boolean useSSL = config.getBoolean("mysql.useSSL", false);

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(useSSL));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");
        hikariConfig.setPoolName("MyChest-HikariPool");

        try {
            dataSource = new HikariDataSource(hikariConfig);
            logger.info("HikariCP connection pool initialized successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize HikariCP connection pool", e);
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    /**
     * Get the DataSource for database operations.
     *
     * @return the HikariCP DataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Get a connection from the pool.
     *
     * @return a database connection
     * @throws SQLException if a connection cannot be obtained
     */
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new IllegalStateException("DatabaseManager not initialized. Call initialize() first.");
        }
        return dataSource.getConnection();
    }

    /**
     * Executes the embedded schema SQL file if present in resources.
     * This method reads sql/schema.sql from the plugin resources and executes
     * each statement separated by semicolons.
     * 
     * <p><strong>Note:</strong> This method should be called explicitly in onEnable()
     * if you want to auto-create tables. It does NOT run automatically.</p>
     */
    public void runEmbeddedSchemaIfPresent() {
        try (InputStream is = plugin.getResource("sql/schema.sql")) {
            if (is == null) {
                logger.info("No embedded schema found at sql/schema.sql - skipping schema execution.");
                return;
            }

            String schema = readInputStream(is);
            executeSchema(schema);
            logger.info("Embedded schema executed successfully.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to read embedded schema", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to execute embedded schema", e);
        }
    }

    /**
     * Execute a schema string containing multiple SQL statements separated by semicolons.
     * 
     * <p><strong>Note:</strong> This method splits statements by semicolons, which is a simple
     * approach that works for standard CREATE TABLE statements. For complex schemas with
     * stored procedures or strings containing semicolons, consider running the schema manually.</p>
     *
     * @param schema the SQL schema string
     * @throws SQLException if execution fails
     */
    private void executeSchema(String schema) throws SQLException {
        // Remove SQL comments (lines starting with --)
        StringBuilder cleanedSchema = new StringBuilder();
        for (String line : schema.split("\n")) {
            String trimmedLine = line.trim();
            if (!trimmedLine.startsWith("--") && !trimmedLine.isEmpty()) {
                cleanedSchema.append(line).append("\n");
            }
        }
        
        String[] statements = cleanedSchema.toString().split(";");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            for (String sql : statements) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                }
            }
        }
    }

    /**
     * Read an InputStream to a String.
     *
     * @param is the input stream
     * @return the content as a string
     * @throws IOException if reading fails
     */
    private String readInputStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Close the connection pool.
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("HikariCP connection pool closed.");
        }
    }

    /**
     * Check if the connection pool is initialized and active.
     *
     * @return true if the pool is active, false otherwise
     */
    public boolean isActive() {
        return dataSource != null && !dataSource.isClosed();
    }
}

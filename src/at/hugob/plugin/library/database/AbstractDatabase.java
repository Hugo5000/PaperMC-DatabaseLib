package at.hugob.plugin.library.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

/**
 * the common code for databases
 */
public abstract class AbstractDatabase<T extends JavaPlugin> implements Closeable {
    private final HikariDataSource dataSource;

    /**
     * the plugin who initiated the database, mostly used for logging
     */
    protected final T plugin;
    /**
     * the prefix for the tabels in the database
     */
    protected final String tablePrefix;

    /**
     * @param plugin      The plugin that initiates the database, mostly used for logging
     * @param dataSource  The SQL DataSource that is used to interact with the Database
     * @param tablePrefix The general Table prefix used for SQL Tables
     */
    public AbstractDatabase(@NotNull final T plugin, @NotNull final DataSource dataSource, @NotNull String tablePrefix) {
        this.tablePrefix = tablePrefix;
        this.plugin = plugin;
        var config = new HikariConfig();
        config.setDataSource(dataSource);
        this.dataSource = new HikariDataSource(config);
    }

    /**
     * Gets a connection from the DataSource to execute SQL queries on
     *
     * @return the Database Connection
     * @throws SQLException gets thrown if a database access error occurs
     */
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * A dummy method so I don't forget to create the SQL tables
     * This needs to be executed on your own!
     */
    abstract protected void createTables();
    /**
     * Sets up the Hikari Config for the specific database configurations
     *
     * @param config The Created Config
     */
    abstract protected void setupHikariConfig(HikariConfig config);

    @Override
    public void close() {
        dataSource.close();
    }

    protected final void prepareStatement(String query, ISQLConsumer<PreparedStatement> preparedStatementConsumer, String errorMessage) {
        useDatabase(con -> {
            try (var statement = con.prepareStatement(query)){
                preparedStatementConsumer.accept(statement);
            }
        }, errorMessage);
    }

    protected final void executeTransaction(ISQLConsumer<Connection> consumer, String errorMessage) {
        useDatabase(con -> {
            con.setAutoCommit(false);
            try {
                consumer.accept(con);
                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e; // Rethrow so it can be handled outside.
            } finally {
                con.setAutoCommit(true);
            }
        }, errorMessage);
    }

    private void useDatabase(ISQLConsumer<Connection> consumer, String errorMessage) {
        try (Connection con = getConnection()) {
            consumer.accept(con);
        } catch (SQLException e) {
            if (shouldRetry(e.getErrorCode())) {
                useDatabase(consumer, errorMessage);
            } else {
                plugin.getLogger().log(Level.SEVERE, errorMessage, e);
            }
        }
    }

    protected abstract boolean shouldRetry(int errorCode);
}

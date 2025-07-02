package at.hugob.plugin.library.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;

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

    @Override
    public void close() {
        dataSource.close();
    }
}

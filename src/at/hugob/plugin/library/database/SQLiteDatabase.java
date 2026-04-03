package at.hugob.plugin.library.database;

import com.zaxxer.hikari.HikariConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteDataSource;
import org.sqlite.SQLiteErrorCode;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * An implementation of the AbstractDatabase to handle SQLite Databases
 */
public abstract class SQLiteDatabase<T extends JavaPlugin> extends AbstractDatabase<T> {
    /**
     * Instantiates a new SQLite Database connection
     *
     * @param plugin      the plugin that instantiates this database connection
     * @param filePath    the path to the SQLite file
     * @param tablePrefix the prefix for tables that are created in the database
     */
    public SQLiteDatabase(@NotNull T plugin, @NotNull String filePath, @NotNull String tablePrefix) {
        super(
            plugin,
            createDataSource(filePath),
            tablePrefix
        );
    }

    private static SQLiteDataSource createDataSource(final @NotNull String filePath) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:" + filePath);
        return dataSource;
    }

    /**
     * Gets a connection from the DataSource to execute SQL queries on
     * With foreign keys turned on.
     *
     * @return the Database Connection
     * @throws SQLException gets thrown if a database access error occurs
     */
    @Override
    protected Connection getConnection() throws SQLException {
        return super.getConnection();
    }

    @Override
    protected final void setupHikariConfig(HikariConfig config) {
        config.setDriverClassName("org.sqlite.JDBC");

        config.setPoolName(plugin.getName() + "-SQLite-HikariPool");

        config.setMaximumPoolSize(1);

        config.addDataSourceProperty("foreign_keys", "true");
        config.addDataSourceProperty("journal_mode", "WAL");
        config.addDataSourceProperty("synchronous", "NORMAL");
    }

    @Override
    protected final boolean shouldRetry(int errorCode) {
        return errorCode == SQLiteErrorCode.SQLITE_BUSY.code;
    }
}

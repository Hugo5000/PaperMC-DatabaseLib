package at.hugob.plugin.library.database;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteDataSource;

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
        var connection = super.getConnection();
        connection.prepareStatement("pragma foreign_keys = ON").executeUpdate();
        return connection;
    }
}

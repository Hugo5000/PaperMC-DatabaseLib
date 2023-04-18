package at.hugob.plugin.library.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of the AbstractDatabase to handle MySQL Databases
 */
public abstract class MySQLDatabase<T extends JavaPlugin> extends AbstractDatabase<T> {

    /**
     * Instantiates a new MySQL Database connection
     *
     * @param plugin      the plugin that instantiates this database connection
     * @param user        the username from the database login
     * @param password    the password from the database login
     * @param database    the database name
     * @param ip          the ip that points to the database
     * @param port        the port that points to the database
     * @param tablePrefix the prefix for tables that are created in the database
     */
    public MySQLDatabase(@NotNull T plugin, @NotNull String user, @NotNull String password, @NotNull String database, @NotNull String ip, int port, @NotNull String tablePrefix) {
        super(
            plugin,
            createDataSource(user, password, database, ip, port),
            tablePrefix
        );
    }
    
    private static MysqlDataSource createDataSource(
        final @NotNull String userName,
        final @NotNull String password,
        final @NotNull String databaseName,
        final @NotNull String ip,
        final int port
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(userName);
        dataSource.setPassword(password);
        dataSource.setDatabaseName(databaseName);
        dataSource.setServerName(ip);
        dataSource.setPort(port);
        return dataSource;
    }
}

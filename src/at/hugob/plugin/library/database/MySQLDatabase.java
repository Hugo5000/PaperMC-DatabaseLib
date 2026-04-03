package at.hugob.plugin.library.database;

import com.mysql.cj.exceptions.MysqlErrorNumbers;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.zaxxer.hikari.HikariConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * An implementation of the AbstractDatabase to handle MySQL Databases
 *
 * @param <T> the plugin class that uses this database
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

    @Override
    protected final void setupHikariConfig(HikariConfig config) {
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setPoolName(plugin.getName() + "-MySQL-HikariPool");

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        config.setMaxLifetime(1800000);
        config.setConnectionTimeout(5000);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        config.addDataSourceProperty("allowPublicKeyRetrieval", "true");
    }

    @Override
    protected final boolean shouldRetry(int errorCode) {
        return errorCode == MysqlErrorNumbers.ER_LOCK_DEADLOCK;
    }
}

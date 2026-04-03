package at.hugob.plugin.library.database;

import java.sql.SQLException;

/**
 * A simple Consumer that can throw an SQLException
 *
 * @param <T> the Class the consumer gives out
 */
@FunctionalInterface
public interface ISQLConsumer<T> {
    /**
     * A simple accept method to provide an object
     *
     * @param obj the object that is provided
     * @throws SQLException when anything goes wrong SQL wise
     */
    void accept(T obj) throws SQLException;
}

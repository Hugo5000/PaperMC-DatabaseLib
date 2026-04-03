package at.hugob.plugin.library.database;

import java.sql.SQLException;

@FunctionalInterface
public interface ISQLConsumer<T> {
    void accept(T obj) throws SQLException;
}

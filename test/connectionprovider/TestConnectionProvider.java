package connectionprovider;

import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnectionProvider implements AQSConnectionProvider {

    private final String dbURL;

    public final ThreadLocal<SQLiteConnectionPoolDataSource> sqLiteConnectionPoolDataSource = new ThreadLocal<>();
    private ThreadLocal<Connection> connection = new ThreadLocal<>();

    public TestConnectionProvider(String dbURL) {
        this.dbURL = dbURL;
    }


    public DataSource getDataSource() {
        if (sqLiteConnectionPoolDataSource.get() == null) {
            SQLiteConnectionPoolDataSource ds = new SQLiteConnectionPoolDataSource();
            ds.setUrl(dbURL);
            sqLiteConnectionPoolDataSource.set(ds);
        }
        return sqLiteConnectionPoolDataSource.get();
    }

    @Override
    public Connection getConnection() {
        try {
            if (connection.get() == null) {
                connection.set(((SQLiteConnectionPoolDataSource) getDataSource()).getPooledConnection().getConnection());
            }
            return connection.get();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot get connection", e);
        }
    }

    @Override
    public void closeConnection() {
        try {
            connection.get().close();
        } catch (SQLException e) {
            throw new RuntimeException("lol", e);
        }
    }
}

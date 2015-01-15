package connectionprovider;

import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnectionProvider implements AQSConnectionProvider {

    public final SQLiteConnectionPoolDataSource sqLiteConnectionPoolDataSource;
    private ThreadLocal<Connection> connection = new ThreadLocal<>();

    public TestConnectionProvider(String dbURL) {
        sqLiteConnectionPoolDataSource = new SQLiteConnectionPoolDataSource();
        sqLiteConnectionPoolDataSource.setUrl(dbURL);
    }


    public DataSource getDataSource() {
        return sqLiteConnectionPoolDataSource;
    }

    @Override
    public Connection getConnection() {
        try {
            if (connection.get() == null) {
                connection.set(sqLiteConnectionPoolDataSource.getPooledConnection().getConnection());
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

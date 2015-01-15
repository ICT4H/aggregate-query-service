package connectionprovider;

import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class TestConnectionProvider implements AQSConnectionProvider {

    public final SQLiteConnectionPoolDataSource sqLiteConnectionPoolDataSource;
    private Connection connection;

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
            if (connection == null) {
                connection = sqLiteConnectionPoolDataSource.getPooledConnection().getConnection();
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Cannot get connection", e);
        }
    }

    @Override
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("lol", e);
        }
    }
}

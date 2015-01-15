package connectionprovider;

import java.sql.Connection;

public interface AQSConnectionProvider {
    public Connection getConnection();

    public void closeConnection();
}

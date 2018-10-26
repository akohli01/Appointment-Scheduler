package application.dbconnectivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amit Kohli
 */
public class DatabaseConnectivity {

    private Connection dbConnection;

    /**
     * Create a database connection object Server name: 52.206.157.109 Database
     * name: U05lEH Username: U05lEH Password: 53688538264
     */
    public DatabaseConnectivity() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            dbConnection = DriverManager.getConnection("jdbc:mysql://52.206.157.109:3306/U05lEH", "U05lEH", "53688538264");
        } catch (ClassNotFoundException ce) {
            System.out.println("Cannot find the Driver Class. Check if mysql-connector-java-5.1.45-bin.jar is present ");
            ce.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Returns Connection
    public Connection getConn() {
        return dbConnection;
    }
    
    public void closeDbConnection() throws SQLException{
        dbConnection.close();
    }

}

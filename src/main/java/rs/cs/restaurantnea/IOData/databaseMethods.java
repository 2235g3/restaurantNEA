package rs.cs.restaurantnea.IOData;

import rs.cs.restaurantnea.general.errorMethods;

import java.sql.*;

public class databaseMethods {
    public static String[][] getData(String sql) {
        String[][] results; // All results from selection will be stored in a 2D array as this method is universal and data can be transferred to objects later
        String DBLocation = System.getProperty("user.dir")  + "src\\main\\resources\\rs\\cs\\restaurantnea\\data\\RestaurantNEA.accdb"; // Gets the location of the database
        try {
            Connection connection = DriverManager.getConnection("jdbc:ucanaccess://" + DBLocation); // Creates a connection to the database
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); // Creates a statement to format the results
            ResultSet resultSet = statement.executeQuery(sql); // Executes the selection and stores the results in a result set
            int rowCount = 0;
            if (resultSet.last()) {
                rowCount = resultSet.getRow(); // Gets the amount of rows in the result set
                resultSet.first(); // Sets the pointer to the beginning otherwise an error will occur
            }
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData(); // Gets the meta data of the result set
            int columnCount = resultSetMetaData.getColumnCount(); // Gets the amount of columns
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = resultSetMetaData.getColumnName(i); // Stores all the column names in a string array
            }
            results = new String[rowCount][columnCount]; // Inits the results array
            for (int i = 0; i < rowCount; i++) {
                for (int j = 0; j < columnCount; j++) {
                    results[i][j] = resultSet.getString(columnNames[j]); // Stores all values from the result set to the array
                }
            }
            resultSet.close();
            connection.close(); // Housekeeping
            return results;
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
            return null;
        }
    }
    public static void insertData(String sql) {
        String DBLocation = System.getProperty("user.dir")  + "src\\main\\resources\\rs\\cs\\restaurantnea\\data\\RestaurantNEA.accdb"; // Gets location of the database
        try {
            Connection connection = DriverManager.getConnection("jdbc:ucanaccess://" + DBLocation); // Creates a connection to the database
            Statement Statement = connection.createStatement(); // Only a simple statement is needed to insert data
            Statement.executeUpdate(sql); // Inserts data into database
            connection.close(); // Housekeeping
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
        }
    }
}

package rs.cs.restaurantnea.IOData;

import rs.cs.restaurantnea.general.errorMethods;

import java.sql.*;

public class databaseMethods {
    private static final String DBLocation = System.getProperty("user.dir")  + "\\src\\main\\resources\\rs\\cs\\restaurantnea\\data\\RestaurantNEA.accdb"; // Gets the location of the database

    public static String[][] getData(String sql, Object[] params) {
        String[][] results; // All results from selection will be stored in a 2D array as this method is universal and data can be transferred to objects later
        try {
            Connection connection = DriverManager.getConnection("jdbc:ucanaccess://" + DBLocation); // Creates a connection to the database
            PreparedStatement prepStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE); // Creates a statement to format the results
            prepStatement = addParams(prepStatement, params); // Adds the parameters to the query
            ResultSet resultSet = prepStatement.executeQuery(); // Executes the selection and stores the results in a result set
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
                    resultSet.absolute(i + 1);
                    results[i][j] = resultSet.getString(columnNames[j]); // Stores all values from the result set to the array
                }
            }
            resultSet.close();
            prepStatement.close();
            connection.close(); // Housekeeping

            return results;
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
            return null;
        }
    }
    public static void CUDData(String sql, Object[] params) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:ucanaccess://" + DBLocation); // Creates a connection to the database
            PreparedStatement prepStatement = connection.prepareStatement(sql); // Only a simple statement is needed to insert data
            prepStatement = addParams(prepStatement, params); // Adds parameters to the query
            prepStatement.executeUpdate(); // Inserts data into database
            prepStatement.close();
            connection.close(); // Housekeeping
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
        }
    }

    public static PreparedStatement addParams(PreparedStatement prepStatement, Object[] params) {
        try {
            for (int i = 0; i < params.length; i++) { // Loops through each parameter and adds the parameter to the statement with the correct data type
                String dataType = params[i].getClass().getSimpleName(); // Gets the parameters' data type as a string
                if (dataType.equals("String")) {
                    prepStatement.setString(i + 1, String.valueOf(params[i])); // Adds the parameter to the correct id
                } else if (dataType.equals("Integer")) {
                    prepStatement.setInt(i + 1, Integer.parseInt(String.valueOf(params[i])));
                } else if (dataType.equals("Float")) {
                    prepStatement.setFloat(i + 1, Float.parseFloat(String.valueOf(params[i])));
                }
            }
            return prepStatement;
        } catch (Exception e) {
            errorMethods.defaultErrors(e);
            return prepStatement;
        }
    }
}

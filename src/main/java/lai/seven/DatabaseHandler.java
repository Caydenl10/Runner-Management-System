/*
 * Cayden Lai
 * 19 April 2025
 * CSA 7th Period
 * Commander Schenk
 */

package lai.seven;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//The class handles database operations for the Runner Management System
public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/cayden?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Bigbot10!";

    //Static block to load the MySQL JDBC driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found!", e);
        }
    }

    //Establishes a connection to the database
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    //Retrieves all runners from the database
    public List<Runner> getAllRunners() {
        List<Runner> runners = new ArrayList<>();
        String query = "SELECT * FROM Runners";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            //Loop through the result set and create Runner objects
            while (rs.next()) {
                runners.add(new Runner(
                    rs.getInt("runnerID"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getBoolean("injured"),
                    rs.getDouble("distance")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving runners: " + e.getMessage());
            e.printStackTrace();
        }
        return runners;
    }

    //Adds a new runner to the database
    public int addRunner(Runner runner) {
        String query = "INSERT INTO Runners (firstName, lastName, age, gender, injured, distance) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
    
            pstmt.setString(1, runner.getFirstName());
            pstmt.setString(2, runner.getLastName());
            pstmt.setInt(3, runner.getAge());
            pstmt.setString(4, runner.getGender());
            pstmt.setBoolean(5, runner.isInjured());
            pstmt.setDouble(6, runner.getDistance());
    
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting runner failed, no rows affected.");
            }
            
            //Retrieve the generated Runner ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    //Return the generated ID
                    return generatedKeys.getInt(1);  
                } else {
                    throw new SQLException("Inserting runner failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding runner: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    //Updates an existing runner in the database
    public boolean updateRunner(Runner runner) {
        String query = "UPDATE Runners SET firstName = ?, lastName = ?, age = ?, gender = ?, injured = ? WHERE runnerID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, runner.getFirstName());
            pstmt.setString(2, runner.getLastName());
            pstmt.setInt(3, runner.getAge());
            pstmt.setString(4, runner.getGender());
            pstmt.setBoolean(5, runner.isInjured());
            pstmt.setInt(6, runner.getRunnerID());
            pstmt.setDouble(6, runner.getDistance());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating runner: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    //Deletes a runner from the database
    public boolean deleteRunner(Runner runner) {
        String query = "DELETE FROM Runners WHERE runnerID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, runner.getRunnerID());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting runner: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
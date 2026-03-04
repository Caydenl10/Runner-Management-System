/*
 * Cayden Lai
 * 19 April 2025
 * CSA 7th Period
 * Commander Schenk
 */

package lai.seven;

//The Runner class represents a runner in the Runner Management System 
//Contains attributes of the runner 
public class Runner {
    //Fields to store runner information
    private int runnerID;
    private String firstName;
    private String lastName;
    private int age;
    private String gender; 
    private boolean injured;
    private double distance;
    
    //Default constructor that initializes the fields with default values
    public Runner() {
        this.runnerID = 0;
        this.firstName = "";
        this.lastName = "";
        this.age = 0;
        this.gender = "Other";
        this.injured = false;
        this.distance = 0.0;
    }

    //Parameterized constructor that initializes all the fields with given values
    public Runner(int runnerID, String firstName, String lastName, int age, String gender, boolean injured, double distance) {
        this.runnerID = runnerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.injured = injured;
        this.distance = distance;    
    }
    
    //Getter methods
    public int getRunnerID() { return runnerID; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public boolean isInjured() { return injured; }
    public double getDistance() { return distance; }

    //Setter methods
    public void setRunnerID(int runnerID) { this.runnerID = runnerID; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setAge(int age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    public void setInjured(boolean injured) { this.injured = injured; }
    public void setDistance(double distance) { this.distance = distance; }

    //Override the toString method to provide a string representation of the Runner object
    @Override
    public String toString() {
        return String.format(
            "RunnerID: %d\nFirst Name: %s\nLast Name: %s\nAge: %d\nGender: %s\nInjured: %s\nDistance: %.2f",
            runnerID, firstName, lastName, age, gender, (injured ? "Yes" : "No"), distance
        );
    }
}
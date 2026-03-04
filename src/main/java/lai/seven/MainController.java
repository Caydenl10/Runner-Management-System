/*
 * Cayden Lai
 * 19 April 2025
 * CSA 7th Period
 * Commander Schenk
 */

package lai.seven;
// JavaFX imports
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.List;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//MainController class handles user interface and GUI interations for the Runner Management System
//Includes CRUD operations for runners
public class MainController {
    //UI controls from FXML
    @FXML private Label hiLabel;
    @FXML private TextField runnerIDField, firstNameField, lastNameField, ageField, distanceField, searchField;
    @FXML private ComboBox<String> genderComboBox, filterComboBox;
    @FXML private CheckBox injuredCheckBox;
    @FXML private Button firstButton, prevButton, nextButton, lastButton;
    @FXML private Button prevFiveButton, nextFiveButton;
    @FXML private Button createButton, deleteButton, updateButton, connectToggleButton;
    @FXML private ImageView myImageView;
    @FXML private ImageView backgroundImage;

    //Internal state variables
    private boolean isCreatingNew = false;
    private boolean isConnected = false;
    private List<Runner> allRunners = new ArrayList<>();
    private List<Runner> runners = new ArrayList<>();
    private int currentIndex = 0;

    //Method that is called when the FXML file is loaded
    @FXML
    public void initialize() {
        try {
            myImageView.setImage(new Image(getClass().getResourceAsStream("/lai/seven/runners.jpg")));
            Image bg = new Image(getClass().getResourceAsStream("/lai/seven/background.jpg"));
            backgroundImage.setImage(bg);
        } catch (Exception e) {
            System.err.println("Image not found.");
        }

        genderComboBox.getItems().addAll("Male", "Female", "Other");
        filterComboBox.getItems().addAll("First Name", "Last Name", "Gender");
        filterComboBox.setValue("First Name");
        //Live filtering as the user is typing
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        disableAllControls();
    }

    //Event handler for the connect/disconnect button
    //Connects to the database and loads all runners
    @FXML
    private void handleConnectToggle() {
        if (!isConnected) {
            DatabaseHandler dbHandler = new DatabaseHandler();
            allRunners = dbHandler.getAllRunners();
            runners = new ArrayList<>(allRunners);
            currentIndex = 0;

            if (!runners.isEmpty()) {
                displayCurrentRunner();
                enableAllControls();
                hiLabel.setText("Connected and runners loaded.");
            } else {
                hiLabel.setText("No runners found.");
            }

            connectToggleButton.setText("Disconnect");
            isConnected = true;
        } else {
            isConnected = false;
            runners.clear();
            allRunners.clear();
            clearFields();
            disableAllControls();
            connectToggleButton.setText("Connect");
            hiLabel.setText("Disconnected from database.");
        }
    }

    //Navigation button handlers
    //Handles first button
    @FXML private void handleFirstButton() {
        if (!runners.isEmpty()) {
            currentIndex = 0;
            displayCurrentRunner();
        }
    }

    //Handles last button
    @FXML private void handleLastButton() {
        if (!runners.isEmpty()) {
            currentIndex = runners.size() - 1;
            displayCurrentRunner();
        }
    }

    //Handles previous button
    @FXML private void handlePrevButton() {
        if (!runners.isEmpty() && currentIndex > 0) {
            currentIndex--;
            displayCurrentRunner();
        }
    }

    //Handles next button
    @FXML private void handleNextButton() {
        if (!runners.isEmpty() && currentIndex < runners.size() - 1) {
            currentIndex++;
            displayCurrentRunner();
        }
    }

    //Handles previous five button
    @FXML private void handlePrevFiveButton() {
        if (!runners.isEmpty()) {
            currentIndex = Math.max(0, currentIndex - 5);
            displayCurrentRunner();
        }
    }

    //Handles next five button
    @FXML private void handleNextFiveButton() {
        if (!runners.isEmpty()) {
            currentIndex = Math.min(runners.size() - 1, currentIndex + 5);
            displayCurrentRunner();
        }
    }

    //Handles create button 
    //Clears the fields for new runner entry and validates the input into the database
    @FXML private void handleCreateButton() {
        if (!isCreatingNew) {
            isCreatingNew = true;
            createButton.setText("Confirm Insert");
            clearFields();
            hiLabel.setText("Enter details for the new runner.");
        } else {
            try {
                Runner newRunner = new Runner();
                newRunner.setFirstName(firstNameField.getText());
                newRunner.setLastName(lastNameField.getText());
                newRunner.setAge(Integer.parseInt(ageField.getText()));
                newRunner.setGender(genderComboBox.getValue());
                newRunner.setInjured(injuredCheckBox.isSelected());
                newRunner.setDistance(Double.parseDouble(distanceField.getText()));

                DatabaseHandler dbHandler = new DatabaseHandler();
                int generatedId = dbHandler.addRunner(newRunner);
                newRunner.setRunnerID(generatedId);

                allRunners.add(newRunner);
                runners = new ArrayList<>(allRunners);
                currentIndex = runners.size() - 1;
                displayCurrentRunner();

                hiLabel.setText("New runner added.");
            } catch (Exception e) {
                hiLabel.setText("Error adding runner: " + e.getMessage());
            } finally {
                isCreatingNew = false;
                createButton.setText("Insert");
            }
        }
    }

    //Updates the current runner's details in the database
    @FXML private void handleUpdateButton() {
        try {
            Runner current = runners.get(currentIndex);
            current.setFirstName(firstNameField.getText());
            current.setLastName(lastNameField.getText());
            current.setAge(Integer.parseInt(ageField.getText()));
            current.setGender(genderComboBox.getValue());
            current.setInjured(injuredCheckBox.isSelected());
            current.setDistance(Double.parseDouble(distanceField.getText()));

            DatabaseHandler dbHandler = new DatabaseHandler();
            dbHandler.updateRunner(current);

            hiLabel.setText("Runner updated successfully.");
        } catch (Exception e) {
            hiLabel.setText("Error updating runner: " + e.getMessage());
        }
    }
    //Prompts the user for confirmation and deletes the current runner from the database
    @FXML private void handleDeleteButton() {
        if (!runners.isEmpty()) {
            Runner current = runners.get(currentIndex);
            String fullName = current.getFirstName() + " " + current.getLastName();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Would you like to delete runner: " + fullName + "?");
            alert.setContentText("This action cannot be undone.");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    DatabaseHandler dbHandler = new DatabaseHandler();
                    dbHandler.deleteRunner(current);

                    allRunners.remove(current);
                    runners = new ArrayList<>(allRunners);
                    currentIndex = Math.max(0, runners.size() - 1);
                    if (!runners.isEmpty()) {
                        displayCurrentRunner();
                    } else {
                        clearFields();
                        disableAllControls();
                    }
                    hiLabel.setText("Runner deleted.");
                } else {
                    hiLabel.setText("Deletion canceled.");
                }
            });
        }
    }

    //Searches for a runner based on the selected filter type and search text
    @FXML private void handleSearchButton() {
        String searchText = searchField.getText().toLowerCase();
        String filterType = filterComboBox.getValue();

        for (int i = 0; i < allRunners.size(); i++) {
            Runner r = allRunners.get(i);
            boolean match = switch (filterType) {
                case "First Name" -> r.getFirstName().toLowerCase().contains(searchText);
                case "Last Name"  -> r.getLastName().toLowerCase().contains(searchText);
                case "Gender"     -> r.getGender().toLowerCase().contains(searchText);
                default -> false;
            };

            if (match) {
                runners = new ArrayList<>(allRunners); // reset full list
                currentIndex = i;
                displayCurrentRunner();
                hiLabel.setText("Found matching runner.");
                return;
            }
        }

        hiLabel.setText("No matching runner found.");
    }

    //Exports the current list of runners to an Excel file
    @FXML private void handleExportButton() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Runners");

        //Header row
        String[] columns = {"RunnerID", "First Name", "Last Name", "Age", "Gender", "Injured", "Distance"};
        Row header = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
        }

        //Data rows
        for (int i = 0; i < allRunners.size(); i++) {
            Runner r = allRunners.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(r.getRunnerID());
            row.createCell(1).setCellValue(r.getFirstName());
            row.createCell(2).setCellValue(r.getLastName());
            row.createCell(3).setCellValue(r.getAge());
            row.createCell(4).setCellValue(r.getGender());
            row.createCell(5).setCellValue(r.isInjured() ? "Yes" : "No");
            row.createCell(6).setCellValue(r.getDistance());
        }

        //Auto size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        //Lets user choose save location
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("runners_export.xlsx");

        Stage stage = (Stage) hiLabel.getScene().getWindow();  

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                hiLabel.setText("Exported to: " + file.getAbsolutePath());
            } catch (IOException e) {
                hiLabel.setText("Export failed: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            hiLabel.setText("Export canceled.");
        }
    }

    //Displays the current runner's details in the UI fields
    private void displayCurrentRunner() {
        if (!runners.isEmpty()) {
            Runner current = runners.get(currentIndex);
            runnerIDField.setText(String.valueOf(current.getRunnerID()));
            firstNameField.setText(current.getFirstName());
            lastNameField.setText(current.getLastName());
            ageField.setText(String.valueOf(current.getAge()));
            genderComboBox.setValue(current.getGender());
            injuredCheckBox.setSelected(current.isInjured());
            distanceField.setText(String.valueOf(current.getDistance()));
            updateButtonStates();
        }
    }

    //Enable or disable buttons based on the current index
    private void updateButtonStates() {
        firstButton.setDisable(currentIndex == 0);
        prevButton.setDisable(currentIndex == 0);
        prevFiveButton.setDisable(currentIndex < 5);

        nextButton.setDisable(currentIndex >= runners.size() - 1);
        nextFiveButton.setDisable(currentIndex > runners.size() - 6);
        lastButton.setDisable(currentIndex >= runners.size() - 1);
    }

    //Applies the filter based on the selected filter type and search text
    private void applyFilter() {
        String searchText = searchField.getText().toLowerCase();
        String filterType = filterComboBox.getValue();

        runners.clear();
        for (Runner r : allRunners) {
            boolean match = switch (filterType) {
                case "First Name" -> r.getFirstName().toLowerCase().contains(searchText);
                case "Last Name"  -> r.getLastName().toLowerCase().contains(searchText);
                case "Gender"     -> r.getGender().toLowerCase().contains(searchText);
                default -> true;
            };
            if (match) runners.add(r);
        }

        if (!runners.isEmpty()) {
            currentIndex = 0;
            displayCurrentRunner();
        } else {
            clearFields();
            hiLabel.setText("No matching runners found.");
        }
    }

    //Clear all text fields
    private void clearFields() {
        runnerIDField.clear();
        firstNameField.clear();
        lastNameField.clear();
        ageField.clear();
        genderComboBox.setValue(null);
        injuredCheckBox.setSelected(false);
        distanceField.clear();
    }

    //Disables all controls until connected to the database
    private void disableAllControls() {
        firstButton.setDisable(true);
        prevFiveButton.setDisable(true);
        prevButton.setDisable(true);
        nextButton.setDisable(true);
        nextFiveButton.setDisable(true);
        lastButton.setDisable(true);
        createButton.setDisable(true);
        deleteButton.setDisable(true);
        updateButton.setDisable(true);
        runnerIDField.setDisable(true);
    }

    //Enables all controls after connecting to the database
    private void enableAllControls() {
        firstButton.setDisable(false);
        prevFiveButton.setDisable(false);
        prevButton.setDisable(false);
        nextButton.setDisable(false);
        nextFiveButton.setDisable(false);
        lastButton.setDisable(false);
        createButton.setDisable(false);
        deleteButton.setDisable(false);
        updateButton.setDisable(false);
        runnerIDField.setDisable(false);
    }
}

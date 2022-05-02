/**
 * Tracker Class
 * GHorned
 *
 * Implements a to-do list using JavaFX GUI
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.time.LocalDate;

public class Tracker extends Application {

    private TextField descriptionInput;
    private DatePicker deadlineInput;
    private TextField projectInput;
    private ColorPicker projectColorInput;
    private TableView<Task> taskTable;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage mainStage) {

        // Main stage
        mainStage.setTitle("TaskTracker");
        mainStage.initStyle(StageStyle.UNIFIED);
        mainStage.setHeight(500);
        mainStage.setOnCloseRequest(e -> saveToFile());

        // Description column
        TableColumn<Task, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setMinWidth(414);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Deadline column
        TableColumn<Task, LocalDate> deadlineColumn = new TableColumn<>("Deadline");
        deadlineColumn.setMinWidth(212);
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        // Project column
        TableColumn<Task, Text> projectColumn = new TableColumn<>(("Project"));
        projectColumn.setMinWidth(212);
        projectColumn.setCellValueFactory(new PropertyValueFactory<>("project"));

        // Create table
        taskTable = new TableView<>();
        taskTable.getColumns().addAll(descriptionColumn, deadlineColumn, projectColumn);
        loadFromFile();

        // Description input
        descriptionInput = new TextField();
        descriptionInput.setPromptText("Description");
        descriptionInput.setMinWidth(400);

        // Deadline input
        deadlineInput = new DatePicker();
        deadlineInput.setPromptText("Due Date");
        deadlineInput.setMinWidth(200);
        deadlineInput.setPrefWidth(200);
        deadlineInput.setEditable(false);

        // Project input
        projectInput = new TextField();
        projectInput.setPromptText("Project");
        projectInput.setMinWidth(200);

        // Project color input
        projectColorInput = new ColorPicker();
        projectColorInput.setPromptText("Project Color");
        projectColorInput.setMinWidth(200);

        // Add button
        Button addButton = new Button("Add");
        addButton.setMinWidth(145);
        addButton.setOnAction(e -> addClicked());

        // Edit button
        Button editButton = new Button("Edit");
        editButton.setMinWidth(145);
        editButton.setOnAction(e -> editClicked());

        // Save button
        Button saveButton = new Button("Save");
        saveButton.setMinWidth(145);
        saveButton.setOnAction(e -> saveClicked());

        // Delete button
        Button deleteButton = new Button("Delete");
        deleteButton.setMinWidth(145);
        deleteButton.setOnAction(e -> deleteClicked());

        // Input layout
        HBox inputLayout = new HBox();
        inputLayout.setPadding(new Insets(10, 10, 10, 10));
        inputLayout.setSpacing(10);
        inputLayout.getChildren().addAll(descriptionInput, deadlineInput, projectInput);

        // Button layout
        HBox buttonLayout = new HBox();
        buttonLayout.setPadding(new Insets(0, 10, 10, 10));
        buttonLayout.setSpacing(10);
        buttonLayout.getChildren().addAll(addButton, editButton, saveButton, deleteButton, projectColorInput);

        // Main layout
        VBox mainLayout = new VBox();
        mainLayout.getChildren().addAll(taskTable, inputLayout, buttonLayout);

        // Main Scene
        Scene mainScene = new Scene(mainLayout);
        mainScene.getRoot().setStyle("-fx-font-size: 12pt");
        mainStage.setScene(mainScene);
        mainStage.show();
    }

    // Add button functionality
    public void addClicked() {
        if (!descriptionInput.getText().isBlank()
                && !descriptionInput.getText().contains("@")
                && deadlineInput.getValue() != null
                && !projectInput.getText().isBlank()
                && !projectInput.getText().contains("@")
                && projectColorInput.getValue() != Color.WHITE) {
            Task task = new Task();
            task.setDescription(descriptionInput.getText());
            task.setDeadline(deadlineInput.getValue());
            task.setProject(new Text(projectInput.getText()));
            task.setProjectColor(projectColorInput.getValue());
            task.getProject().setStroke(task.getProjectColor());
            taskTable.getItems().add(task);
            descriptionInput.clear();
            deadlineInput.setValue(null);
            projectInput.clear();
            projectColorInput.setValue(Color.WHITE);
        }
    }

    // Edit button functionality
    public void editClicked() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        descriptionInput.setText(selected.getDescription());
        deadlineInput.setValue(selected.getDeadline());
        projectInput.setText(selected.getProject().getText());
        projectColorInput.setValue(selected.getProjectColor());
    }

    // Save button functionality
    public void saveClicked() {
        deleteClicked();
        addClicked();
    }

    // Delete button functionality
    public void deleteClicked() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        taskTable.getItems().remove(selected);
    }

    // Save tasks to .txt file
    public void saveToFile() {
        try {
            FileWriter fileWriter = new FileWriter("Record.txt");
            for (Task t : taskTable.getItems()) {
                fileWriter.write(t.getDescription() + "@" +
                        t.getDeadline().toString() + "@" +
                        t.getProject().getText() + "@" +
                        (t.getProjectColor().getRed()) + "@" +
                        (t.getProjectColor().getGreen()) + "@" +
                        (t.getProjectColor().getBlue()) + "@" + "\n");
            }
            fileWriter.close();
        } catch (java.io.IOException e) {
            System.out.println("File could not be saved");
        }
    }

    // Load tasks from .txt file
    public void loadFromFile() {
        try {
            String line;
            BufferedReader fileReader = new BufferedReader(new FileReader("Record.txt"));
            while ((line = fileReader.readLine()) != null) {
                if (!line.isBlank()) {
                    String[] splitLine = line.split("@");
                    Task t = new Task();
                    t.setDescription(splitLine[0]);
                    t.setDeadline(LocalDate.parse(splitLine[1]));
                    t.setProject(new Text(splitLine[2]));
                    t.setProjectColor(Color.color(Double.parseDouble(splitLine[3]),
                            Double.parseDouble(splitLine[4]),
                            Double.parseDouble(splitLine[5])));
                    t.getProject().setStroke(t.getProjectColor());
                    taskTable.getItems().add(t);
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("File could not be loaded");
        }
    }
}

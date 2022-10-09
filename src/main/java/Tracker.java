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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tracker extends Application {

    private TextField descriptionInput;
    private DatePicker deadlineInput;
    private TextField projectInput;
    private ColorPicker projectColorInput;
    private TableView<Task> taskTable;

    // On application startup
    @Override
    public void init() throws Exception {
        super.init();
        taskTable = new TableView<>();
        importData();
    }

    // Running application
    @Override
    public void start(Stage mainStage) {

        // Main stage
        mainStage.setTitle("TaskTracker");
        mainStage.initStyle(StageStyle.UNIFIED);
        mainStage.setHeight(500);

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
        taskTable.getColumns().addAll(Arrays.asList(descriptionColumn, deadlineColumn, projectColumn));

        // Description input
        descriptionInput = new TextField();
        descriptionInput.setPromptText("Description");
        descriptionInput.setMinWidth(400);

        // Deadline input
        deadlineInput = new DatePicker();
        deadlineInput.setPromptText("Deadline");
        deadlineInput.setMinWidth(200);
        deadlineInput.setPrefWidth(200);
        deadlineInput.setEditable(false);

        // Project input
        projectInput = new TextField();
        projectInput.setPromptText("Project");
        projectInput.setMaxWidth(200);

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

    // On application closing
    @Override
    public void stop() throws Exception {
        super.stop();
        exportData();
    }

    // Add button functionality
    public void addClicked() {
        if (!descriptionInput.getText().isBlank()
                && deadlineInput.getValue() != null
                && !projectInput.getText().isBlank()
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
        if (!descriptionInput.getText().isBlank()
                && deadlineInput.getValue() != null
                && !projectInput.getText().isBlank()
                && projectColorInput.getValue() != Color.WHITE
                && !taskTable.getSelectionModel().isEmpty()) {
            Task selected = taskTable.getSelectionModel().getSelectedItem();
            Task task = new Task();
            task.setDescription(descriptionInput.getText());
            task.setDeadline(deadlineInput.getValue());
            task.setProject(new Text(projectInput.getText()));
            task.setProjectColor(projectColorInput.getValue());
            task.getProject().setStroke(task.getProjectColor());
            taskTable.getItems().remove(selected);
            taskTable.getItems().add(task);
            descriptionInput.clear();
            deadlineInput.setValue(null);
            projectInput.clear();
            projectColorInput.setValue(Color.WHITE);
        }

    }

    // Delete button functionality
    public void deleteClicked() {
        Task selected = taskTable.getSelectionModel().getSelectedItem();
        taskTable.getItems().remove(selected);
    }

    // Populate table
    public void importData() {
        List<List<Object>> data = GoogleSync.loadSpreadsheet();
        if (data != null) {
            for (List<Object> row : data) {
                Task t = new Task();
                t.setDescription(row.get(0).toString());
                t.setDeadline(LocalDate.parse(row.get(1).toString()));
                t.setProject(new Text(row.get(2).toString()));
                t.setProjectColor(Color.color(Double.parseDouble(row.get(3).toString()),
                        Double.parseDouble(row.get(4).toString()),
                        Double.parseDouble(row.get(5).toString())));
                t.getProject().setStroke(t.getProjectColor());
                taskTable.getItems().add(t);
            }
        }
    }

    // Save table
    public void exportData() {
        List<List<Object>> data = new ArrayList<>();
        for (Task t : taskTable.getItems()) {
            List<Object> row = new ArrayList<>();
            row.add(t.getDescription());
            row.add(t.getDeadline().toString());
            row.add(t.getProject().getText());
            row.add(String.valueOf(t.getProjectColor().getRed()));
            row.add(String.valueOf(t.getProjectColor().getGreen()));
            row.add(String.valueOf(t.getProjectColor().getBlue()));
            data.add(row);
        }
        GoogleSync.updateSpreadsheet(data);
    }
}
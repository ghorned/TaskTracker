import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.time.LocalDate;

public class Task {

    // Instance fields
    private String description;
    private LocalDate deadline;
    private Text project;
    private Color projectColor;

    // Constructor
    public Task() {
        this.description = null;
        this.deadline = null;
        this.project = null;
        this.projectColor = null;
    }

    // Accessors and Modifiers
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Text getProject() {
        return project;
    }

    public void setProject(Text project) {
        this.project = project;
    }

    public Color getProjectColor() {
        return projectColor;
    }

    public void setProjectColor(Color projectColor) {
        this.projectColor = projectColor;
    }
}
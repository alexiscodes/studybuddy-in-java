
package com.studybuddy;

import com.studybuddy.model.Note;
import com.studybuddy.model.Task;
import com.studybuddy.service.NoteManager;
import com.studybuddy.service.TaskManager;
import com.studybuddy.service.PomodoroTimer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.animation.Animation;

public class App extends Application {

    // Existing task and timer systems
    private final TaskManager taskManager = new TaskManager();
    private final PomodoroTimer pomodoro = new PomodoroTimer();

    // Notes system
    private final NoteManager noteManager = new NoteManager();

    // Task interface
    private final ListView<String> taskList = new ListView<>();
    private final TextField taskInput = new TextField();

    private final Label progressLabel =
        new Label("Let's get things done!");

    // Timer interface
    private final Label timerLabel = new Label("25:00");
    private Timeline timeline;

    // Notes interface
    private final ListView<Note> notesList = new ListView<>();
    private final TextField noteTitleInput = new TextField();
    private final ComboBox<String> subjectBox = new ComboBox<>();
    private final TextArea noteContentInput = new TextArea();
    private final TextField searchInput = new TextField();

    private final Label noteStatusLabel =
        new Label("Your notes will appear here.");

    private int selectedNoteIndex = -1;

    @Override
    public void start(Stage stage) {

        Label heading = new Label("📚 StudyBuddy");
        Label subtitle =
            new Label("Your little space to focus and grow ✨");

        heading.setStyle(
            "-fx-font-size: 28px; -fx-font-weight: bold;"
        );

        TabPane tabs = new TabPane();

        Tab dashboardTab = new Tab(
            "🏠 Dashboard",
            createDashboard()
        );

        Tab notesTab = new Tab(
            "📝 Study Notes",
            createNotesPage()
        );

        dashboardTab.setClosable(false);
        notesTab.setClosable(false);

        tabs.getTabs().addAll(dashboardTab, notesTab);

        VBox mainLayout = new VBox(
            15,
            heading,
            subtitle,
            tabs
        );

        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle(
            "-fx-background-color: #f4f0ff;"
        );

        Scene scene = new Scene(mainLayout, 850, 650);

        stage.setTitle("StudyBuddy");
        stage.setScene(scene);
        stage.show();

        refreshTasks();
        refreshNotes();
    }

    // ==========================================
    // DASHBOARD
    // ==========================================

    private VBox createDashboard() {

        Label timerHeading = new Label("🍅 Pomodoro Timer");

        timerLabel.setStyle(
            "-fx-font-size: 36px; -fx-font-weight: bold;"
        );

        Button addButton = new Button("Add Task");
        Button completeButton =
            new Button("Complete Selected");

        Button removeButton =
            new Button("Remove Selected");

        Button startTimerButton = new Button("▶ Start");
        Button pauseTimerButton = new Button("⏸ Pause");
        Button resetTimerButton = new Button("↻ Reset");

        taskInput.setPromptText("Enter a study task...");

        addButton.setOnAction(event -> addTask());
        completeButton.setOnAction(event -> completeTask());
        removeButton.setOnAction(event -> removeTask());

        timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> {

                boolean finished = pomodoro.tick();

                timerLabel.setText(
                    pomodoro.getFormattedTime()
                );

                if (finished) {
                    timeline.stop();

                    showMessage(
                        "Pomodoro session complete! Take a little break 🎉"
                    );
                }
            })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);

        startTimerButton.setOnAction(event -> {
            if (timeline.getStatus() != Animation.Status.RUNNING) {
                timeline.play();
            }

        });

        pauseTimerButton.setOnAction(
            event -> timeline.pause()
        );

        resetTimerButton.setOnAction(event -> {

            timeline.stop();
            pomodoro.reset();

            timerLabel.setText(
                pomodoro.getFormattedTime()
            );
        });

        HBox inputRow = new HBox(
            10,
            taskInput,
            addButton
        );

        HBox timerControls = new HBox(
            10,
            startTimerButton,
            pauseTimerButton,
            resetTimerButton
        );

        HBox actionRow = new HBox(
            10,
            completeButton,
            removeButton
        );

        taskInput.setOnAction(event -> addTask());

        VBox dashboard = new VBox(
            15,
            progressLabel,
            new Separator(),
            timerHeading,
            timerLabel,
            timerControls,
            new Separator(),
            new Label("📋 My Study Tasks"),
            inputRow,
            taskList,
            actionRow
        );

        dashboard.setPadding(new Insets(15));

        taskList.setPrefHeight(250);

        return dashboard;
    }

    // ==========================================
    // TASK MANAGEMENT
    // ==========================================

    private void addTask() {

        String title = taskInput.getText().trim();

        if (title.isEmpty()) {
            showMessage(
                "Please enter a task first!"
            );
            return;
        }

        taskManager.addTask(title);

        taskInput.clear();

        refreshTasks();
    }

    private void completeTask() {

        int index = taskList.getSelectionModel()
            .getSelectedIndex();

        if (index < 0) {
            showMessage(
                "Please select a task first!"
            );
            return;
        }

        taskManager.completeTask(index);

        refreshTasks();
    }

    private void removeTask() {

        int index = taskList.getSelectionModel()
            .getSelectedIndex();

        if (index < 0) {
            showMessage(
                "Please select a task first!"
            );
            return;
        }

        taskManager.removeTask(index);

        refreshTasks();
    }

    private void refreshTasks() {

        var tasks = taskManager.getTasks();

        taskList.setItems(
            FXCollections.observableArrayList(
                tasks.stream()
                    .map(task ->
                        (task.isCompleted() ? "✅ " : "⬜ ")
                        + task.getTitle()
                    )
                    .toList()
            )
        );

        long completed = tasks.stream()
            .filter(Task::isCompleted)
            .count();

        int percentage = tasks.isEmpty()
            ? 0
            : (int) (completed * 100 / tasks.size());

        progressLabel.setText(
            "Progress: " + completed
            + " / " + tasks.size()
            + " tasks completed ("
            + percentage + "%) 🎯"
        );
    }

    // ==========================================
    // STUDY NOTES PAGE
    // ==========================================

    private VBox createNotesPage() {

        Label notesHeading =
            new Label("📝 My Study Notes");

        notesHeading.setStyle(
            "-fx-font-size: 22px; -fx-font-weight: bold;"
        );

        searchInput.setPromptText(
            "🔍 Search notes..."
        );

        searchInput.textProperty().addListener(
            (observable, oldValue, newValue) ->
                searchNotes(newValue)
        );

        // Subject dropdown
        subjectBox.setEditable(true);

        subjectBox.getItems().addAll(
            "Mathematics",
            "Science",
            "Computer Science",
            "English",
            "History",
            "Geography",
            "Other"
        );

        subjectBox.setPromptText(
            "Choose or enter a subject"
        );

        // Note editor
        noteTitleInput.setPromptText(
            "Enter note title..."
        );

        noteContentInput.setPromptText(
            "Start writing your study notes here..."
        );

        noteContentInput.setWrapText(true);

        noteContentInput.setPrefRowCount(12);

        // Buttons
        Button newButton = new Button("➕ New Note");
        Button saveButton = new Button("💾 Save Note");
        Button editButton = new Button("✏️ Update Note");
        Button deleteButton = new Button("🗑 Delete Note");
        Button clearButton = new Button("Clear Editor");

        newButton.setOnAction(
            event -> clearNoteEditor()
        );

        saveButton.setOnAction(
            event -> saveNote()
        );

        editButton.setOnAction(
            event -> updateNote()
        );

        deleteButton.setOnAction(
            event -> deleteNote()
        );

        clearButton.setOnAction(
            event -> clearNoteEditor()
        );

        // Select a note from the list
        notesList.getSelectionModel()
            .selectedItemProperty()
            .addListener(
                (observable, oldNote, newNote) -> {

                    if (newNote != null) {
                        displayNote(newNote);
                    }
                }
            );

        notesList.setCellFactory(list ->

            new ListCell<Note>() {

                @Override
                protected void updateItem(
                    Note note,
                    boolean empty
                ) {

                    super.updateItem(note, empty);

                    if (empty || note == null) {
                        setText(null);
                    } else {
                        setText(
                            "📘 " + note.getTitle()
                            + "\n" + note.getSubject()
                        );
                    }
                }
            }
        );

        notesList.setPrefWidth(250);

        // Left side: notes list
        VBox leftPanel = new VBox(
            12,
            searchInput,
            newButton,
            notesList
        );

        leftPanel.setPrefWidth(260);
        VBox.setVgrow(notesList, Priority.ALWAYS);

        // Right side: note editor
        VBox editorPanel = new VBox(
            12,
            new Label("Note Title"),
            noteTitleInput,
            new Label("Subject"),
            subjectBox,
            new Label("Note Content"),
            noteContentInput,
            new HBox(8, saveButton, editButton),
            new HBox(8, deleteButton, clearButton),
            noteStatusLabel
        );

        VBox.setVgrow(noteContentInput, Priority.ALWAYS);

        // Main notes layout
        HBox notesLayout = new HBox(
            15,
            leftPanel,
            new Separator(),
            editorPanel
        );

        HBox.setHgrow(editorPanel, Priority.ALWAYS);

        VBox page = new VBox(
            15,
            notesHeading,
            notesLayout
        );

        VBox.setVgrow(notesLayout, Priority.ALWAYS);

        page.setPadding(new Insets(15));

        return page;
    }

    // ==========================================
    // NOTES OPERATIONS
    // ==========================================

    private void saveNote() {

        String title = noteTitleInput.getText().trim();
        String subject = getSelectedSubject().trim();
        String content = noteContentInput.getText().trim();

        if (title.isBlank()
                || subject.isBlank()
                || content.isBlank()) {

            showMessage(
                "Please fill in the title, subject, and content!"
            );

            return;
        }

        try {

            noteManager.addNote(
                title,
                subject,
                content
            );

            refreshNotes();
            clearNoteEditor();

            noteStatusLabel.setText(
                "✅ Note saved successfully!"
            );

        } catch (IllegalArgumentException e) {

            showMessage(e.getMessage());
        }
    }

    private void updateNote() {

        if (selectedNoteIndex < 0) {
            showMessage("Please select a note to update!");
            return;
        }

        String title = noteTitleInput.getText().trim();
        String subject = getSelectedSubject();
        String content = noteContentInput.getText().trim();

        if (title.isBlank() || subject.isBlank() || content.isBlank()) {
            showMessage("Please fill in all the fields?");
            return;
        }

        boolean updated = noteManager.editNote(
            selectedNoteIndex,
            title,
            subject,
            content
        );

        if (updated) {
            refreshNotes();

            noteStatusLabel.setText(
                "✅ Note updated successfully!"
            );
        } else {
            showMessage("Could not update the note!");
        }
    }
    
    private void deleteNote() {

        if (selectedNoteIndex < 0) {

            showMessage(
                "Please select a note to delete!"
            );

            return;
        }

        Alert confirmation = new Alert(
            Alert.AlertType.CONFIRMATION
        );

        confirmation.setTitle("Delete Note");
        confirmation.setHeaderText(
            "Are you sure you want to delete this note?"
        );

        confirmation.setContentText(
            "This action cannot be undone."
        );

        confirmation.showAndWait().ifPresent(response -> {

            if (response == ButtonType.OK) {

                noteManager.deleteNote(
                    selectedNoteIndex
                );

                refreshNotes();
                clearNoteEditor();

                noteStatusLabel.setText(
                    "🗑 Note deleted."
                );
            }
        });
    }

    // Display selected note in editor
    private void displayNote(Note note) {

        selectedNoteIndex =
            noteManager.getAllNotes().indexOf(note);

        if (selectedNoteIndex < 0) {
            return;
        }

        noteTitleInput.setText(
            note.getTitle()
        );

        subjectBox.setValue(
            note.getSubject()
        );

        noteContentInput.setText(
            note.getContent()
        );

        noteStatusLabel.setText(
            "Last updated: "
            + note.getFormattedUpdatedAt()
        );
    }

    // Refresh notes list
    private void refreshNotes() {

        notesList.setItems(
            FXCollections.observableArrayList(
                noteManager.getAllNotes()
            )
        );
    }

    // Search notes
    private void searchNotes(String keyword) {

        notesList.setItems(
            FXCollections.observableArrayList(
                noteManager.searchNotes(keyword)
            )
        );

        selectedNoteIndex = -1;
    }

    // Get selected subject
    private String getSelectedSubject() {

        String subject = subjectBox.getEditor().getText();

        if (subject == null || subject.isBlank()) {
            subject = subjectBox.getValue();
        }

        return subject == null ? "" : subject.trim();
    }

    // Clear note editor
    private void clearNoteEditor() {

        selectedNoteIndex = -1;

        notesList.getSelectionModel().clearSelection();

        noteTitleInput.clear();
        subjectBox.setValue(null);
        subjectBox.getEditor().clear();
        noteContentInput.clear();

        noteStatusLabel.setText(
            "Ready to write a new note!"
        );
    }

    // ==========================================
    // GENERAL MESSAGE
    // ==========================================

    private void showMessage(String message) {

        Alert alert = new Alert(
            Alert.AlertType.INFORMATION
        );

        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    @Override
    public void stop() {

        if (timeline != null) {
            timeline.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

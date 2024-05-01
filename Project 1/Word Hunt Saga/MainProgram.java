//David Xie
//Patrick McDougle
//Comp Sci 2

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

// Main class that extends Application for JavaFX application
public class MainProgram extends Application {
    private MediaPlayer mediaPlayer; 
    private MediaPlayer winSoundPlayer;
    private Scene gameScene, resultsScene; 
    private Stage primaryStage; 
    private VBox volumeControls; 
    private Label timerLabel = new Label("01:30"); 
    private Label messageLabel = new Label(); 

    private Timeline timeline; 
    private Set<String> wordList; 
    private String currentWord; 
    private TextField[][] textFields = new TextField[5][5]; 
    private long startTime; 
    private ArrayList<String> resultsLog = new ArrayList<>();

    // Override the start method which is the entry point for JavaFX applications
    @Override
public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    wordList = loadWords("O:/My Programs/Project 1/Word Hunt Saga/WordList.txt");
    initializeMusicPlayer("O:/My Programs/Project 1/Word Hunt Saga/Media/[Non Copyrighted Music] Fredji - Happy Life [Tropical House].mp3");
    initializeWinSoundPlayer();
    setupVolumeControl();
    loadResults(); 
    Scene startScene = setupStartScreen();
    primaryStage.setTitle("Word Hunt Saga");
    primaryStage.setScene(startScene);
    primaryStage.show();
}

    

    // Method to load words from a file and store them in a set
    private Set<String> loadWords(String filename) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename)); // Read all lines from the file
            Set<String> words = new HashSet<>(); // Create a HashSet to store words
            for (String line : lines) {
                words.add(line.trim().toUpperCase()); // Trim and convert each line to uppercase and add to the set
            }
            return words; // Return the set of words
        } catch (IOException e) {
            System.err.println("Failed to load word list: " + e.getMessage()); // Log an error if the file cannot be read
            return new HashSet<>(); // Return an empty set on failure
        }
    }

    // Method to initialize the music player with a given music file path
    private void initializeMusicPlayer(String musicPath) {
        try {
            Media media = new Media(new File(musicPath).toURI().toString()); // Create a media object from a file path
            mediaPlayer = new MediaPlayer(media); // Initialize the media player with the media
            mediaPlayer.setVolume(0.2); // Set the volume
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Set the media player to loop indefinitely
            mediaPlayer.play(); 
        } catch (Exception e) {
            System.err.println("Error initializing music player: " + e.getMessage()); // Log an error if the media player fails to initialize
        }
    }
    private void initializeWinSoundPlayer() {
        String winSoundPath = "O:/My Programs/Project 1/Word Hunt Saga/Media/Noice Meme.mp3";
        try {
            Media winSound = new Media(new File(winSoundPath).toURI().toString());
            winSoundPlayer = new MediaPlayer(winSound);
            winSoundPlayer.setVolume(0.2); 
        } catch (Exception e) {
            System.err.println("Error initializing winning sound player: " + e.getMessage());
        }
    }
    

   
// Method to setup the start screen scene
private Scene setupStartScreen() {
    BorderPane root = new BorderPane(); 
    root.setPadding(new Insets(10)); 

    // Set the background image for the start screen
    setBackgroundImage(root, "O:/My Programs/Project 1/Word Hunt Saga/Media/WordleStartScreen.jpg", 1.0);

    Button startGameButton = new Button("Start Game"); 
    styleButton(startGameButton);
    startGameButton.setOnAction(e -> primaryStage.setScene(setupHomeScreen())); // When user clicks, go to next scene

    // Creating a layout pane that allows easy alignment and adjustment
    VBox layout = new VBox(startGameButton);
    layout.setAlignment(Pos.CENTER); 
    layout.setPadding(new Insets(313, 0, 0, 0)); // Top padding to move the button down to the correct spot

    // Set the VBox to the center of the BorderPane, effectively moving the button down
    root.setCenter(layout); 

    return new Scene(root, 1024, 1024); // Return a new scene containing the root pane
}



    // Method to setup the home screen scene
    private Scene setupHomeScreen() {
        BorderPane root = new BorderPane(); 
        root.setPadding(new Insets(10)); 

        // Set the background image for the home screen
        setBackgroundImage(root, "O:/My Programs/Project 1/Word Hunt Saga/Media/WordleHomeScreen.jpg", 1.0);

        // Setup volume controls at the top right of the screen
        setupVolumeControl();
        HBox topRight = new HBox(volumeControls); 
        topRight.setAlignment(Pos.TOP_RIGHT); // Set the alignment of the HBox to the top right
        root.setTop(topRight); 

        // Setup buttons for game controls at the center of the screen
        VBox centerMenu = new VBox(20); 
        centerMenu.setAlignment(Pos.CENTER); 
        Button playGameButton = new Button("Play Game"); 
        Button viewResultsButton = new Button("View Results"); 

        // Apply styling to the buttons
        styleButton(playGameButton);
        styleButton(viewResultsButton);

        // Set actions for the buttons
        playGameButton.setOnAction(e -> switchToGameScreen()); // When clicked, goes to the play game
        viewResultsButton.setOnAction(e -> switchToResultsScreen()); // when clicked, goes to the result screen

        // Add buttons to the VBox
        centerMenu.getChildren().addAll(playGameButton, viewResultsButton);
        root.setCenter(centerMenu); 

        return new Scene(root, 1024, 1024); 
    }

    // Method to setup volume controls
    private void setupVolumeControl() {
        Slider volumeSlider = new Slider(0, 1, 0.3); // Create a slider for volume control
        volumeSlider.setPrefWidth(180); // Set the width of the slider
        volumeSlider.valueProperty().bindBidirectional(mediaPlayer.volumeProperty()); // Makes the slider's value bidirectionally to the media player's volume property

        Label volumeLabel = new Label("Volume"); // Create a label for the slider
        volumeControls = new VBox(5, volumeSlider, volumeLabel); 
        volumeControls.setPadding(new Insets(10, 20, 10, 0)); 
    }

    // Method to switch to the game screen
    private void switchToGameScreen() {
        if (timeline != null) {
            timeline.stop(); // Stop the timeline if it is not null, to ensure no leftover updates
        }
        resetGameGrid(); // Call the method to reset the game grid
    
        currentWord = selectRandomWord(); // Select a random word from the word list
        System.out.println("Current word: " + currentWord); // Print the current word to the console    This is something that can be # out if you do not want to look at the console to cheat.
    
        messageLabel.setText(""); 
        messageLabel.setTextFill(Color.BLACK); 
    
        BorderPane gameRoot = new BorderPane(); 
        gameRoot.setPadding(new Insets(10)); 
    
        setupTopBar(gameRoot); 
    
        Pane gameLayout = createGameGrid(); 
        gameRoot.setCenter(gameLayout); 
    
        gameRoot.setBackground(new Background(new BackgroundFill(Color.web("#F5F5DC"), CornerRadii.EMPTY, Insets.EMPTY))); 
        gameScene = new Scene(gameRoot, 1024, 1024); 
        primaryStage.setScene(gameScene); 
    
        startTimer(); 
    }
    

    // Method to reset the game grid
    private void resetGameGrid() {
        if (textFields != null) { 
            for (int row = 0; row < textFields.length; row++) { // Loop through each row
                for (int col = 0; col < textFields[row].length; col++) { // Loop through each column in the row
                    if (textFields[row][col] != null) { // Check if the text field at the current position is not null
                        textFields[row][col].setText(""); // Clear the text in the text field
                        textFields[row][col].setDisable(row != 0); // Disable the text field if it is not in the first row
                    }
                }
            }
        }
    }

    // Method to select a random word from the word list
    private String selectRandomWord() {
        int size = wordList.size(); 
        int item = new Random().nextInt(size); 
        int i = 0; 
        for (String word : wordList) { 
            if (i == item) 
                return word; 
            i++; 
        }
        return null; 
    }

    // Method to setup the top bar of the game screen
    private void setupTopBar(BorderPane root) {
        HBox topBar = new HBox(10); 
        topBar.setPadding(new Insets(10)); 

        Button mainMenuButton = new Button("Main Menu"); 
        mainMenuButton.setOnAction(e -> {
            if (timeline != null) {
                timeline.stop(); 
            }
            primaryStage.setScene(setupHomeScreen()); // Change the scene to the home screen when user clicks
        }); 

        Button retryButton = new Button("Retry"); // Create a button to retry the game
        retryButton.setOnAction(e -> switchToGameScreen()); // restart the game when the retry button is clicked

        Button resultsButton = new Button("View Results"); // button to view results
        resultsButton.setFont(Font.font("Arial", FontWeight.BOLD, 14)); 
        resultsButton.setOnAction(e -> switchToResultsScreen()); // switch to the results screen when the results button is clicked
        resultsButton.setVisible(false); // set the visibility of the results button to false when game isnt over yet

        timerLabel.setFont(Font.font("Arial", 24)); 
        messageLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 24)); 
        messageLabel.setTextFill(Color.RED); 

        topBar.getChildren().addAll(mainMenuButton, resultsButton, retryButton, timerLabel, messageLabel, volumeControls); // Add all controls to the top bar
        topBar.setAlignment(Pos.CENTER); 
        HBox.setHgrow(timerLabel, Priority.ALWAYS); 
        root.setTop(topBar); 
    }

    // Method to show the results button next to the main menu
    private void showResultsButtonNextToMenu() {
        for (javafx.scene.Node node : ((HBox) gameScene.getRoot().lookup(".top-bar")).getChildren()) { 
            if (node instanceof Button && "View Results".equals(((Button) node).getText())) { 
                node.setVisible(true); // Make it visible after game ends
                break; 
            }
        }
    }
    

    // Method to start the timer
    private void startTimer() {
        if (timeline != null) {
            timeline.stop(); // Stop the timeline if it is running
        }
        startTime = System.currentTimeMillis(); // Record the start time
        timerLabel.setText(formatTime(90)); // Set the text of the timer label
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimer())); // timeline with a KeyFrame that updates every second
        timeline.setCycleCount(Timeline.INDEFINITE); // Set the timeline to repeat indefinitely
        timeline.play(); 
    }

    // Method to update the timer every second
    private void updateTimer() {
        long currentTime = System.currentTimeMillis(); 
        long elapsedSeconds = (currentTime - startTime) / 1000; // Calculate the elapsed time in seconds
        int timeLeft = 90 - (int) elapsedSeconds; // Calculate the time left by subtracting the elapsed time from 90 seconds
        if (timeLeft > 0) {
            timerLabel.setText(formatTime(timeLeft)); 
        } else {
            timeline.stop(); 
            timerLabel.setText("00:00"); 
            messageLabel.setText("Time's up!"); 
            messageLabel.setTextFill(Color.RED); 
            disableGameGrid(); // disable the game grid
            showResultsButtonNextToMenu(); // show the results button
        }
    }

    // Method to disable all text fields in the game grid
    private void disableGameGrid() {
        for (int row = 0; row < textFields.length; row++) { 
            for (int col = 0; col < textFields[row].length; col++) { 
                textFields[row][col].setDisable(true); // Disable the text field at the current position
            }
        }
    }

    // Method to format the time in minutes and seconds
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60; 
        int seconds = totalSeconds % 60; 
        return String.format("%02d:%02d", minutes, seconds); 
    }

    // Method to create the game grid
    private Pane createGameGrid() {
        GridPane grid = new GridPane(); 
        grid.setAlignment(Pos.CENTER); 
        grid.setPadding(new Insets(10)); 
        grid.setHgap(10); 
        grid.setVgap(10); 

        for (int row = 0; row < 5; row++) { 
            for (int col = 0; col < 5; col++) { 
                TextField textField = new TextField(); 
                textField.setPrefWidth(120); 
                textField.setPrefHeight(120); 
                textField.setAlignment(Pos.CENTER); 
                textField.setFont(Font.font(36)); 
                textField.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))); 
                textField.setDisable(row != 0); 

                setupTextField(textField, row, col); 

                grid.add(textField, col, row); 
                textFields[row][col] = textField; 
            }
        }

        // Create the messageLabel and grid to it
        VBox layout = new VBox(5); 
        layout.setAlignment(Pos.CENTER); 
        messageLabel.setFont(Font.font("Arial", FontPosture.ITALIC, 24)); 
        messageLabel.setTextFill(Color.RED); 

        // Add components to the VBox
        layout.getChildren().addAll(messageLabel, grid);

        return layout; 
    }

    // Method to setup a text field in the game grid
    private void setupTextField(TextField textField, final int row, final int col) {
        textField.setOnKeyPressed(event -> {
            if (!textField.isDisabled()) { 
                if (event.getCode() == KeyCode.ENTER) { // Check if the Enter key was pressed
                    checkWord(row); // Call the method to check the word
                } else if (event.getCode() == KeyCode.BACK_SPACE && textField.getText().isEmpty() && col > 0) { // Check if the Backspace key was pressed and the text field is empty and it is not the first column
                    textFields[row][col - 1].requestFocus(); // Move focus to the previous text field
                }
            }
        });

        textField.textProperty().addListener((obs, oldText, newText) -> {
            if (!textField.isDisabled()) { 
                if (newText.length() > 1) { // Check if the new text is longer than one character
                    textField.setText(newText.substring(0, 1).toUpperCase()); // Set the text of the text field to the first character of the new text
                }
                if (newText.length() == 1 && col < 4) { 
                    textFields[row][col + 1].requestFocus(); // Move focus to the next text field
                }
                if (oldText.length() > newText.length() && newText.isEmpty() && col > 0) { // Check if text was deleted, the new text is empty, and it is not the first column
                    textFields[row][col - 1].requestFocus(); // Move focus to the previous text field
                }
            }
        });
    }

    
    // Method to check the word entered by the user
    private void checkWord(int row) {
        StringBuilder wordBuilder = new StringBuilder(); 
        for (int col = 0; col < 5; col++) { 
            wordBuilder.append(textFields[row][col].getText().toUpperCase().trim()); 
        }
        String word = wordBuilder.toString(); 
    
        if (wordList.contains(word)) { // Check if the word list contains the word
            if (word.equals(currentWord)) { // Check if the word matches the current word
                colorRow(row, "green"); 
                timeline.stop(); 
                timerLabel.setText(formatTime((int) ((System.currentTimeMillis() - startTime) / 1000))); // Update the timer label with the time taken to find the word
                resultsLog.add(currentWord + "," + ((System.currentTimeMillis() - startTime) / 1000)); // Add the current word and the time taken to the results log
                saveResults(); 
                messageLabel.setText("Congratulations! You've found the word!"); 
                messageLabel.setTextFill(Color.GREEN); 
                showResultsButton(); 
                disableRow(row); 
                if (winSoundPlayer != null) {
                    winSoundPlayer.stop(); 
                    winSoundPlayer.play(); 
                }
            } else {
                validateLetters(word, row); // validate the letters of the word
                if (row < 4) { // Check if it is not the last row
                    enableRow(row + 1); 
                } else {
                    messageLabel.setText("No more attempts! The word was: " + currentWord); 
                    messageLabel.setTextFill(Color.RED); 
                    timeline.stop(); 
                    showResultsButton(); 
                }
                disableRow(row); 
            }
        } else {
            clearRow(row); // clear the row if the word is not valid
            messageLabel.setText("Not a valid word in list! Try again."); 
            messageLabel.setTextFill(Color.RED); 
            textFields[row][0].requestFocus(); 
        }
    }
    

    // Method to disable a row in the game grid
    private void disableRow(int row) {
        for (int col = 0; col < 5; col++) { 
            textFields[row][col].setDisable(true); 
        }
    }

    // Method to enable a row in the game grid
    private void enableRow(int row) {
        for (int col = 0; col < 5; col++) { 
            textFields[row][col].setDisable(false); 
        }
        textFields[row][0].requestFocus(); // Set focus to the first text field in the row
    }

    // Method to color a row in the game grid
    private void colorRow(int row, String color) {
        for (int col = 0; col < 5; col++) { 
            textFields[row][col].setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;"); 
        }
    }

    // Method to show the results button
    private void showResultsButton() {
        Button resultsButton = new Button("View Results"); 
        resultsButton.setFont(Font.font("Arial", FontWeight.BOLD, 24)); 
        resultsButton.setPrefWidth(300); 
        resultsButton.setPrefHeight(100); 
        resultsButton.setStyle("-fx-background-color: green; -fx-text-fill: white;"); 

        resultsButton.setOnAction(e -> switchToResultsScreen()); // switch to the results screen when the button is clicked

        // Check if the root of the game scene is a BorderPane
        if (gameScene.getRoot() instanceof BorderPane) {
            BorderPane root = (BorderPane) gameScene.getRoot(); 
            HBox topBar = new HBox(resultsButton); 
            topBar.setAlignment(Pos.TOP_LEFT); 
            topBar.setPadding(new Insets(10, 0, 0, 10)); 

            // Check if there is already a top component in the root pane
            if (root.getTop() != null) {
                
                if (root.getTop() instanceof HBox) {
                    HBox existingTopBar = (HBox) root.getTop(); // Cast the top component
                    existingTopBar.getChildren().add(resultsButton); // Add the results button to the existing HBox
                } else {
                    
                    HBox newTopBar = new HBox(root.getTop(), resultsButton);
                    newTopBar.setAlignment(Pos.CENTER_LEFT); 
                    root.setTop(newTopBar); 
                }
            } else {
                
                root.setTop(topBar);
            }
        }
    }

    // Method to switch to the results screen
    private void switchToResultsScreen() {
        BorderPane root = new BorderPane(); 
        VBox resultsBox = new VBox(20); 
        resultsBox.setAlignment(Pos.CENTER); 
        resultsBox.setPadding(new Insets(20)); 

        // Add a label to the VBox to display "Previous Results:"
        resultsBox.getChildren().add(new Label("Previous Results:"));
        for (String result : resultsLog) { 
            Label resultLabel = new Label(result.replace(",", " - Word: ") + " seconds"); 
            resultsBox.getChildren().add(resultLabel); 
        }

        // Create buttons for playing again and navigating to the main menu
        Button playAgainButton = new Button("Play Again");
        Button mainMenuButton = new Button("Main Menu");

        
        styleButton(playAgainButton);
        styleButton(mainMenuButton);

        // Set actions for the buttons
        playAgainButton.setOnAction(e -> switchToGameScreen()); // restart the game when the play again button is clicked
        mainMenuButton.setOnAction(e -> primaryStage.setScene(setupHomeScreen())); // switch to the home screen when the main menu button is clicked

        
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER); 
        buttonBox.getChildren().addAll(playAgainButton, mainMenuButton); 

        
        root.setTop(buttonBox);
        root.setCenter(resultsBox);

        resultsScene = new Scene(root, 1024, 1024); 
        primaryStage.setScene(resultsScene); 
    }

    
   // Method to save the game results
    private void saveResults() {
        String resultsFilePath = "O:/My Programs/Project 1/Word Hunt Saga/results.txt"; 
        try (PrintWriter out = new PrintWriter(new FileWriter(resultsFilePath, true))) { 
            out.println(currentWord + "," + (System.currentTimeMillis() - startTime) / 1000); // Write the current word and the time taken to the file
        } catch (IOException e) {
            System.err.println("Failed to save results: " + e.getMessage()); // error if there is an issue writing to the file
        }
    }

    private void loadResults() {
        String resultsFilePath = "O:/My Programs/Project 1/Word Hunt Saga/results.txt";
        File file = new File(resultsFilePath);
        if (file.exists()) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(resultsFilePath));
                resultsLog.clear(); 
                resultsLog.addAll(lines); // Add all read results to the resultsLog list
            } catch (IOException e) {
                System.err.println("Failed to load previous results: " + e.getMessage());
            }
        }
    }


    // Method to validate the letters of a word entered by the user
    private void validateLetters(String word, int row) {
        for (int i = 0; i < word.length(); i++) { 
            TextField textField = textFields[row][i]; 
            char c = word.charAt(i); 
            if (c == currentWord.charAt(i)) {
                textField.setStyle("-fx-background-color: green; -fx-text-fill: white;"); 
            } else if (currentWord.contains(String.valueOf(c))) {
                textField.setStyle("-fx-background-color: yellow; -fx-text-fill: black;"); 
            } else {
                textField.setStyle("-fx-background-color: grey; -fx-text-fill: white;"); 
            }
        }
    }

    // Method to clear a row in the game grid
    private void clearRow(int row) {
        for (int col = 0; col < 5; col++) { 
            textFields[row][col].setText(""); 
            textFields[row][col].setDisable(false); 
        }
        textFields[row][0].requestFocus(); 
    }

    // Method to set the background image for a pane
    private void setBackgroundImage(BorderPane pane, String imagePath, double opacity) {
        Image image = new Image(new File(imagePath).toURI().toString(), 1024, 1024, false, true); 
        ImageView imageView = new ImageView(image); 
        imageView.setOpacity(opacity); 
        imageView.setFitWidth(1024); 
        imageView.setFitHeight(1024); 
        pane.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT))); 
    }

    // Method to apply styling to a button
    private void styleButton(Button button) {
        button.setFont(Font.font(18)); 
        button.setPrefWidth(240);
        button.setPrefHeight(90); 
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args); // Call the launch method to start the application
    }
}

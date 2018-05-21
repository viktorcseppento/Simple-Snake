package Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScoreTable extends VBox {
    private Connection conn;
    private TableView table;
    private Stage window;
    private Scene mainMenuScene;
    private final static Logger logger = Logger.getLogger(ScoreTable.class.getName());

    static {
        try {
            FileHandler fh = new FileHandler("snake.log", true);
            fh.setLevel(Level.INFO);
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ScoreTable(Stage window, Scene mainMenuScene) {
        this.setAlignment(Pos.CENTER);
        this.window = window;
        this.mainMenuScene = mainMenuScene;
        this.spacingProperty().bind(window.heightProperty().divide(10));

        Label highScoresLabel = new Label("High Scores");
        highScoresLabel.setFont(new Font(30));

        table = new TableView();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                window.setScene(mainMenuScene);
            }
        });
        backButton.prefHeightProperty().bind(window.heightProperty().divide(20));
        backButton.prefWidthProperty().bind(window.widthProperty().divide(3));

        this.getChildren().addAll(highScoresLabel, table, backButton);

    }

    /**
     * Létrehoz egy kapcsolatot egy szerveren lévő adatbázissal.
     */
    public void connectToDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/scores?useSSL=false", "root", "root");
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Can't connect to database");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Egy új bejegyzést ad a táblához.
     *
     * @param name  Játékos neve.
     * @param score Játékos által elért pontszám.
     */
    public static void addNewEntry(String name, int score) {
        Entry toBeAdded = new Entry(name, score);

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/scores?useSSL=false", "root", "root")) {
            Statement sqlStatement = connection.createStatement();
            String addEntry = "INSERT INTO SCORES VALUES ('" + toBeAdded.getName() + "', '" + toBeAdded.getDifficulty() + "', " + toBeAdded.getNumberOfEnemies() + ", '" + toBeAdded.getResolution() + "', " + toBeAdded.getScore() + ");";
            sqlStatement.executeUpdate(addEntry);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Can't connect to database");
        }

    }

    /**
     * JavaFx által megjelenírtendő táblát készít.
     */
    public void createTableGuiFromServer() {


        TableColumn nameCol = new TableColumn("Name");
        TableColumn diffCol = new TableColumn("Difficulty");
        TableColumn enemyCol = new TableColumn("Number of Enemies");
        TableColumn resolutionCol = new TableColumn("Resolution");
        TableColumn scoreCol = new TableColumn("Score");
        table.getColumns().addAll(nameCol, diffCol, resolutionCol, scoreCol);
        nameCol.setCellValueFactory(new PropertyValueFactory<Entry, String>("name"));
        diffCol.setCellValueFactory(new PropertyValueFactory<Entry, String>("difficulty"));
        enemyCol.setCellValueFactory(new PropertyValueFactory<Entry, String>("Numberofenemies"));
        resolutionCol.setCellValueFactory(new PropertyValueFactory<Entry, String>("resolution"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<Entry, String>("score"));


        try (Statement statement = conn.createStatement()) {
            String sqlString = "SELECT * FROM SCORES;"; //Lekér minden sort
            ResultSet result = statement.executeQuery(sqlString);
            ObservableList<Entry> data = FXCollections.observableArrayList(); //Sorok listája
            while (result.next()) {
                String name = result.getString("Name");
                String difficulty = result.getString("Difficulty");
                int numberOfEnemies = result.getInt("Numberofenemies");
                String resolution = result.getString("Resolution");
                int score = result.getInt("Score");
                data.add(new Entry(name, difficulty, resolution, numberOfEnemies, score));
            }
            table.setItems(data);
            logger.log(Level.INFO, "Items have been loaded from database\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConn() {
        return conn;
    }

    public TableView getTable() {
        return table;
    }

    public Stage getWindow() {
        return window;
    }

    public Scene getMainMenuScene() {
        return mainMenuScene;
    }
}

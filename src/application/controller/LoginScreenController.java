package application.controller;

import application.dbconnectivity.DatabaseConnectivity;
import application.main.SchedulingApplication;
import application.utility.CustomAlert;
import application.utility.SceneHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;
import javafx.util.Duration;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Amit Kohli
 */
public class LoginScreenController {

    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button login;
    @FXML
    private ProgressBar loginProgress;
    @FXML
    AnchorPane ap;

    private Stage stage;

    //Handle login
    @FXML
    private void login(MouseEvent event) throws Exception {

        stage = (Stage) ap.getScene().getWindow();

        if (textFieldIsNull() != true) {

            //Create login task with progress bar
            final Task saveTask = validateUserCredentials();
            loginProgress.setOpacity(1);
            loginProgress.progressProperty().bind(saveTask.progressProperty());
            Thread saveThread = new Thread(saveTask);
            saveThread.setDaemon(true);
            saveThread.start();

            //Perform necessary tasks after login passed
            saveTask.setOnSucceeded((Event event1) -> {
                if ((int) saveTask.getValue() == 1) {
                    //Records the login timestamp
                    try {
                        recordLoginTimestamp();
                    } catch (IOException ex) {
                        Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //Checks for appointments within fifteen minutes
                    checkForUpcomingAppointments();
                    
                    //Returns the default locale to English if another locale is set
                    if (!Locale.getDefault().equals(Locale.ENGLISH)) {
                        Locale.setDefault(new Locale("en", "US"));
                    }
                    //Creates the tabs for appointment, customer, and report along with the menu bar
                    SceneHandler newScene;
                    try {
                        newScene = new SceneHandler("/application/view/Tabs.fxml", stage);
                        newScene.createAppendableScene();
                        MenuController menuController = newScene.getMenuLoader().getController();
                        menuController.setUsername(SchedulingApplication.getUsername());
                    } catch (IOException ex) {
                        Logger.getLogger(LoginScreenController.class.getName()).log(Level.SEVERE, null, ex);
                    } 
                }
                else{
                      if (Locale.getDefault().getLanguage().equals("en")) {
                               CustomAlert.createAlert(Alert.AlertType.ERROR, stage, "Login Failed",
                                    "Incorrect Username or Password");
                        }
                        
                        else {
                            CustomAlert.createAlert(Alert.AlertType.ERROR, stage, "Echec de la connexion",
                                    "Identifiant ou mot de passe incorrect");   
                        }
                      loginProgress.setOpacity(0);
                }
            });
        }
    }

    //Check if username and password textfields are null(blank)
    private boolean textFieldIsNull() {

        //Username check for null
        if (username.getText().isEmpty()) {

            if (Locale.getDefault().equals(Locale.FRANCE)) {
                CustomAlert.createAlert(Alert.AlertType.ERROR, stage, "Forme Erreur!",
                        "S'il vous plaît entrez votre nom d'utilisateur");
            } else {
                CustomAlert.createAlert(Alert.AlertType.ERROR, stage, "Form Error!",
                        "Please enter your username");
            }
            return true;
        }

        //Password check for null
        if (password.getText().isEmpty()) {

            if (Locale.getDefault().equals(Locale.FRANCE)) {
                CustomAlert.createAlert(Alert.AlertType.ERROR, stage, "Forme Erreur!",
                        "S'il vous plait entrez votre mot de passe");
            } else {
                CustomAlert.createAlert(Alert.AlertType.ERROR, stage, "Form Error!",
                        "Please enter your password");
            }
            return true;
        }
        return false;
    }

    //Checks if username and password is correct
    private Task validateUserCredentials() throws IOException {
        Task<Integer> loginTask = new Task<Integer>() {

            @Override
            protected Integer call() throws Exception {
                //Displays an indeterminate progress bar
                for (int p = 0; p < 100; p++) {
                    Thread.sleep(10);
                    updateProgress(-1, -1);
                }
                //Database operations
                DatabaseConnectivity dbConnection = new DatabaseConnectivity();
                try {
                    PreparedStatement prepStatement = dbConnection.getConn().prepareStatement("SELECT username FROM user WHERE userName=? AND password=?");
                    prepStatement.setString(1, username.getText());
                    prepStatement.setString(2, password.getText());
                    ResultSet rs = prepStatement.executeQuery();
                    if (rs.next()) {

                        SchedulingApplication.setUsername(rs.getString("userName"));

                        return 1;
                    } 
                     dbConnection.closeDbConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };
        return loginTask;
    }

    //Closes the application
    @FXML
    private void closeApplication(MouseEvent event) {
        System.exit(0);
    }

    //Allows the ability to toggle between English and French
    @FXML
    private void toggleLanguage() throws IOException {

        if (Locale.getDefault().getLanguage().equals("en")) {
            Locale.setDefault(new Locale("fr", "FR"));
        } else {
            Locale.setDefault(new Locale("en", "US"));
        }

        ResourceBundle bundle = ResourceBundle.getBundle("application.resource.login", Locale.getDefault());

        stage = new Stage();
        new SceneHandler("/application/view/LoginScreen.fxml", stage, bundle).createScene();
        
        PauseTransition delay = new PauseTransition(Duration.seconds(.1));
        delay.setOnFinished( event -> ((Stage) login.getScene().getWindow()).close() );
        delay.play();
      

    }

    //Records the timestamp for user log-ins in a .txt file
    private void recordLoginTimestamp() throws IOException{

        Path txtPath = Paths.get("loginTimestamps.txt");

        String timeStamp = new SimpleDateFormat("MM/dd/yyyy  HH:mm:ss ").format(Calendar.getInstance().getTime());
        timeStamp += "User " + SchedulingApplication.getUsername() + " has logged in";
        timeStamp += System.lineSeparator();

         
            Files.write(txtPath, timeStamp.getBytes(StandardCharsets.UTF_8),
                Files.exists(txtPath) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE); 

    }
    //Checks for appointments that start within fifteen minutes of login
    private void checkForUpcomingAppointments() {

        DatabaseConnectivity dbConnection = new DatabaseConnectivity();

        try {
            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement(
                    "SELECT title, description, location, contact, start, end, customerName "
                    + "FROM appointment INNER JOIN customer on appointment.customerId = customer.customerId WHERE "
                    + " start BETWEEN ? AND ?  AND appointment.createdBy = ?;");

            prepStatement.setTimestamp(1, java.sql.Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
            prepStatement.setTimestamp(2, java.sql.Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(15)));
            prepStatement.setString(3, SchedulingApplication.getUsername());

            ResultSet resultSet = prepStatement.executeQuery();

            while (resultSet.next()) {

                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                String contact = resultSet.getString("contact");

                ZonedDateTime startTime = resultSet.getTimestamp("start").toLocalDateTime().atZone(ZoneId.of("UTC"));
                startTime = startTime.withZoneSameInstant(ZoneId.systemDefault());
                LocalTime localStartTime = startTime.toLocalDateTime().toLocalTime();

                ZonedDateTime endTime = resultSet.getTimestamp("end").toLocalDateTime().atZone(ZoneId.of("UTC"));
                endTime = endTime.withZoneSameInstant(ZoneId.systemDefault());
                LocalTime localEndTime = endTime.toLocalDateTime().toLocalTime();

                String customerName = resultSet.getString("customerName");

                String message = "Today, you have the following meeting with " + customerName + ":\n\n"
                        + "Time: " + localStartTime + " - " + localEndTime + " \n"
                        + "Title: " + title + "\n"
                        + "Description: " + description + "\n"
                        + "Location: " + location + "\n"
                        + "Contact: " + contact;

                CustomAlert.createAlert(Alert.AlertType.INFORMATION, stage, "Upcoming Appointment",
                        message);
            }

             dbConnection.closeDbConnection();
        } catch (SQLException sqlException) {
            System.out.println("SQL syntax/logic is incorrect");
            sqlException.printStackTrace();
        } catch (Exception e) {
            System.out.println("Other exception has occurred");
            e.printStackTrace();
        }

    }

}

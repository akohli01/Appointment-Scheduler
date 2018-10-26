package application.main;

import application.utility.SceneHandler;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author Amit Kohli
 */
public class SchedulingApplication extends Application {

    private static String username;

    @Override
    public void start(Stage stage) throws Exception {
        //Set default local to English
        Locale.setDefault(new Locale("en", "US"));

        //Below is the code to support French language, just uncomment the below line and comment the above line to test
        //Locale.setDefault(new Locale("fr", "FR"));
        
        //Set language properties file based upon locale
        ResourceBundle bundle = ResourceBundle.getBundle("application.resource.login", Locale.getDefault());

        //Create the login screen
        new SceneHandler("/application/view/LoginScreen.fxml", stage, bundle).createScene();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     *
     * @param username
     */
    public static void setUsername(String username) {
        SchedulingApplication.username = username;
    }

    /**
     *
     * @return String username
     */
    public static String getUsername() {
        return SchedulingApplication.username;
    }

}

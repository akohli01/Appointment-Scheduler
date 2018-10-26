package application.utility;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

/**
 *
 * @author Amit Kohli
 */
public class CustomAlert {

    private static Alert alert;

    public static void createAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }

    public static void createHiddenAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
    }

    public static Optional<ButtonType> getAlert() {
        return alert.showAndWait();
    }
}

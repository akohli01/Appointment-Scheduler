package application.controller;

import application.model.Customer;
import application.utility.CustomAlert;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class CustomerCRUDController {

    @FXML
    protected TextField name;
    @FXML
    protected TextField address;
    @FXML
    protected TextField address2;
    @FXML
    protected TextField postalCode;
    @FXML
    protected TextField phone;
    @FXML
    protected TextField city;
    @FXML
    protected TextField country;
    @FXML
    protected GridPane customerPane;

    protected final ReadOnlyObjectWrapper<Customer> customer = new ReadOnlyObjectWrapper<>();

    //Gets the customer object
    public ReadOnlyObjectProperty<Customer> getCustomerPOJO() {
        return customer.getReadOnlyProperty();
    }

    //Cancels the operation
    @FXML
    protected void cancel() {
        ((Stage) name.getScene().getWindow()).close();
    }

    //Checks for any null inputs
    protected boolean isNull() {

        for (Node node : customerPane.getChildren()) {

            if (node instanceof TextField) {

                if (((TextField) node).getText().trim().isEmpty()) {
                    CustomAlert.createAlert(Alert.AlertType.INFORMATION, (Stage) name.getScene().getWindow(), "Null error",
                            "Please fill in all text fields ");
                    return true;
                }
            }
        }

        return false;
    }

}

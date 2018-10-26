package application.controller;

import application.model.Address;
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

/**
 * FXML Controller class
 *
 * @author Amit Kohli
 */
public class UpdateCustomerController {

    @FXML
    private TextField name;
    @FXML
    private TextField address;
    @FXML
    private TextField address2;
    @FXML
    private TextField postalCode;
    @FXML
    private TextField phone;
    @FXML
    private TextField city;
    @FXML
    private TextField country;
    @FXML
    private GridPane updateCustomerPane;

    private int customerID;

    private ReadOnlyObjectWrapper<Customer> changedCustomer = new ReadOnlyObjectWrapper<>();

    //Gets the changed customer object
    public ReadOnlyObjectProperty<Customer> changedCustomer() {
        return changedCustomer.getReadOnlyProperty();
    }

    //Cancels the operation
    @FXML
    private void cancel() {
        ((Stage) name.getScene().getWindow()).close();
    }

    //Creates the updateCustomer
    @FXML
    public void createChangedCustomer() {

        if (isNull() == true) {
            return;
        }

        Address customerAddress
                = new Address(address.getText(), address2.getText(), postalCode.getText(), phone.getText(), city.getText(), country.getText());

        Customer newCustomer = new Customer(customerID, name.getText(), customerAddress);

        changedCustomer.set(newCustomer);

        ((Stage) name.getScene().getWindow()).close();

    }

    //Populates the appropriate text fields 
    public void setFields(Customer editCustomer) {

        name.setText(editCustomer.getCustomerName());
        address.setText(editCustomer.getCustomerAddress().getAddress());
        address2.setText(editCustomer.getCustomerAddress().getAddress2());
        postalCode.setText(editCustomer.getCustomerAddress().getPostalCode());
        phone.setText(editCustomer.getCustomerAddress().getPhone());
        city.setText(editCustomer.getCustomerAddress().getCity());
        country.setText(editCustomer.getCustomerAddress().getCountry());

        customerID = editCustomer.getCustomerID();

    }

    //Checks for any null inputs 
    private boolean isNull() {

        for (Node node : updateCustomerPane.getChildren()) {

            if (node instanceof TextField) {

                if (((TextField) node).getText().trim().isEmpty()) {
                    CustomAlert.createAlert(Alert.AlertType.INFORMATION, (Stage) name.getScene().getWindow(), "Null error",
                            "Please fill in all text fields or press cancel to discard any changes ");
                    return true;
                }
            }
        }

        return false;
    }

}

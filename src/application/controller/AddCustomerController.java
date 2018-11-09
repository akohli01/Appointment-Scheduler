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
 * @author Home
 */
public class AddCustomerController extends CustomerCRUDController {

    //Creates the customer object
    @FXML
    public void createCustomer() {

        if(isNull() == true){
            return;
        }
        Address customerAddress
                = new Address(address.getText(), address2.getText(), postalCode.getText(), phone.getText(), city.getText(), country.getText());

        Customer newCustomer = new Customer(name.getText(), customerAddress);

        customer.set(newCustomer);

        ((Stage) name.getScene().getWindow()).close();

    }

}

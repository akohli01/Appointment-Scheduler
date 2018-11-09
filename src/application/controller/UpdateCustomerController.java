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
public class UpdateCustomerController extends CustomerCRUDController{

    private int customerID;

    //Creates the updateCustomer
    @FXML
    public void createChangedCustomer() {

        if (isNull() == true) {
            return;
        }

        Address customerAddress
                = new Address(address.getText(), address2.getText(), postalCode.getText(), phone.getText(), city.getText(), country.getText());

        Customer newCustomer = new Customer(customerID, name.getText(), customerAddress);

        customer.set(newCustomer);

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

}

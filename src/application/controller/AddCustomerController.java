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
public class AddCustomerController {

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
    private GridPane addCustomerPane;

    private final ReadOnlyObjectWrapper<Customer> customer = new ReadOnlyObjectWrapper<>();

    //Gets the customer object
    public ReadOnlyObjectProperty<Customer> addedCustomer() {
        return customer.getReadOnlyProperty();
    }
    
    //Cancels the operation
    @FXML
    private void cancel() {
        ((Stage) name.getScene().getWindow()).close();
    }

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
  
        
        //Checks for any null inputs 
    private boolean isNull() {

        for (Node node : addCustomerPane.getChildren()) {

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

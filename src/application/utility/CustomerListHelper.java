package application.utility;

import application.model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

//Class is used to "pass" the customer's ObservableList around in the application
/**
 *
 * @author Amit Kohli
 * 
 */
public class CustomerListHelper {

    private static ObservableList<Customer> collection = FXCollections.observableArrayList();

    public static void setObservableList(ObservableList<Customer> list) {
        collection = list;
    }

    public static ObservableList<Customer> getObservableList() {
        return collection;
    }

}

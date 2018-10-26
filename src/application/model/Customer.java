package application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {

    private String customerName;
    private Address customerAddress;
    private int customerID;

    //Overloaded Constructors
    public Customer(int customerID, String customerName, Address customerAddress) {
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerID = customerID;
    }

    public Customer(String customerName, Address customerAddress) {
        this.customerName = customerName;
        this.customerAddress = customerAddress;

    }

    //Getters and Setters
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public Address getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(Address customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getPhoneNumber() {
        return customerAddress.getPhone();
    }

}

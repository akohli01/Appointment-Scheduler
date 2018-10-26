package application.controller;

import application.dbconnectivity.DatabaseConnectivity;
import application.main.SchedulingApplication;
import application.model.Address;
import application.model.Customer;
import application.utility.CustomAlert;
import application.utility.CustomerListHelper;
import application.utility.SceneHandler;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

/*
 * FXML Controller class
 *
 * @author Amit Kohli
 */
public class CustomerTabController {

    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, String> customerName;
    @FXML
    private TableColumn<Customer, String> customerContactNumber;
    @FXML
    private TableColumn<Customer, Address> customerAddress;

    @FXML
    AnchorPane customerTab;

    private Stage parentStage;

    private ObservableList<Customer> customers = FXCollections.observableArrayList();

    //Performs initial tasks
    @FXML
    public void initialize() {

        customerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerContactNumber.setCellValueFactory(new PropertyValueFactory("phoneNumber"));
        customerAddress.setCellValueFactory(new PropertyValueFactory<Customer, Address>("customerAddress"));

        customerTableView.getItems().setAll(getCustomers());

        CustomerListHelper.setObservableList(customers);

        //Uses lambdas to handle changes to the customer list
        customers.addListener(new ListChangeListener<Customer>() {

            @Override
            public void onChanged(Change<? extends Customer> c) {
                while (c.next()) {
                    if (c.wasAdded()) {

                        customerTableView.getItems().setAll(customers);

                    } else if (c.wasRemoved()) {

                        customerTableView.getItems().setAll(customers);
                    }
                    CustomerListHelper.setObservableList(customers);
                }
            }
        });
    }

    //Gets all the customers
    public ObservableList<Customer> getCustomers() {
        DatabaseConnectivity dbConnection = new DatabaseConnectivity();

        try {
            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement(
                    "SELECT customerId, customerName, address, address2, city, postalCode, phone, country "
                    + "FROM customer INNER JOIN address ON customer.addressId = address.addressId "
                    + "INNER JOIN city ON address.cityId = city.cityId INNER JOIN country ON city.countryId = country.countryId;");

            ResultSet resultSet = prepStatement.executeQuery();

            while (resultSet.next()) {
                int customerID = resultSet.getInt("customerId");
                String customerName = resultSet.getString("customerName");
                String address = resultSet.getString("address");
                String address2 = resultSet.getString("address");
                String city = resultSet.getString("city");
                String postalCode = resultSet.getString("postalCode");
                String phone = resultSet.getString("phone");
                String country = resultSet.getString("country");

                Address customerAddress = new Address(address, address2, postalCode, phone, city, country);

                customers.add(new Customer(customerID, customerName, customerAddress));
            }

            dbConnection.closeDbConnection();

        } catch (SQLException sqe) {
            System.out.println("SQL syntax/logic is incorrect");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("An exception has occurred");
            e.printStackTrace();
        }
        return customers;
    }

    //Creates a new screen to enter in new customers
    @FXML
    private void createNewCustomerTab(MouseEvent event) throws IOException {

        parentStage = (Stage) customerTab.getScene().getWindow();

        Stage addCustomer = new Stage();

        addCustomer.setTitle("New Customer");
        addCustomer.initModality(Modality.WINDOW_MODAL);
        addCustomer.initOwner(parentStage);
        addCustomer.initStyle(StageStyle.UNDECORATED);

        SceneHandler newScene = new SceneHandler("/application/view/AddCustomer.fxml", addCustomer);
        newScene.createPopupScene();

        AddCustomerController childController = newScene.getSceneLoader().getController();

        childController.addedCustomer().addListener((obs, oldCustomer, newCustomer) -> {

            customers.add(addCustomerDatabase(newCustomer));

        });
    }

    //Adds customer and any associated information to database
    private Customer addCustomerDatabase(Customer customer) {

        DatabaseConnectivity dbConnection = new DatabaseConnectivity();
        try {
            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement("INSERT into country (country, createDate, createdBy,lastUpdateBy) values(?, CURRENT_TIMESTAMP, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            prepStatement.setString(1, customer.getCustomerAddress().getCountry());
            prepStatement.setString(2, SchedulingApplication.getUsername());
            prepStatement.setString(3, SchedulingApplication.getUsername());
            prepStatement.executeUpdate();
            ResultSet rs = prepStatement.getGeneratedKeys();

            if (rs.next()) {

                prepStatement = dbConnection.getConn().prepareStatement("INSERT into city (city, countryId, createDate, createdBy,lastUpdateBy) values(?,?, CURRENT_TIMESTAMP,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                prepStatement.setString(1, customer.getCustomerAddress().getCity());
                prepStatement.setInt(2, rs.getInt(1));
                prepStatement.setString(3, SchedulingApplication.getUsername());
                prepStatement.setString(4, SchedulingApplication.getUsername());
                prepStatement.executeUpdate();
                rs = prepStatement.getGeneratedKeys();

                if (rs.next()) {

                    prepStatement = dbConnection.getConn().prepareStatement("INSERT into address (address, address2, cityId, postalCode, phone, createDate, createdBy,lastUpdateBy) "
                            + " values(?,?,?,?,?,CURRENT_TIMESTAMP,?,?)", Statement.RETURN_GENERATED_KEYS);
                    prepStatement.setString(1, customer.getCustomerAddress().getAddress());
                    prepStatement.setString(2, customer.getCustomerAddress().getAddress2());
                    prepStatement.setInt(3, rs.getInt(1));
                    prepStatement.setString(4, customer.getCustomerAddress().getPostalCode());
                    prepStatement.setString(5, customer.getCustomerAddress().getPhone());
                    prepStatement.setString(6, SchedulingApplication.getUsername());
                    prepStatement.setString(7, SchedulingApplication.getUsername());

                    prepStatement.executeUpdate();
                    rs = prepStatement.getGeneratedKeys();
                }

                if (rs.next()) {

                    prepStatement = dbConnection.getConn().prepareStatement("INSERT into customer (customerName, addressId, createDate, createdBy,lastUpdateBy, active) "
                            + " values(?,?, CURRENT_TIMESTAMP, ?, ?, ? )", Statement.RETURN_GENERATED_KEYS);
                    prepStatement.setString(1, customer.getCustomerName());
                    prepStatement.setInt(2, rs.getInt(1));
                    prepStatement.setString(3, SchedulingApplication.getUsername());
                    prepStatement.setString(4, SchedulingApplication.getUsername());
                    prepStatement.setInt(5, 0);

                    prepStatement.executeUpdate();
                    rs = prepStatement.getGeneratedKeys();
                }

                if (rs.next()) {
                    customer.setCustomerID(rs.getInt(1));
                }
            }

            dbConnection.closeDbConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customer;
    }

    //Creates a screen to update the selected customer's information
    @FXML
    private void createUpdateCustomerTab(MouseEvent event) throws IOException, InterruptedException {

        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {

            parentStage = (Stage) customerTab.getScene().getWindow();

            Stage editCustomer = new Stage();

            editCustomer.setTitle("Edit Customer");
            editCustomer.initModality(Modality.WINDOW_MODAL);
            editCustomer.initOwner(parentStage);
            editCustomer.initStyle(StageStyle.UNDECORATED);

            SceneHandler newScene = new SceneHandler("/application/view/UpdateCustomer.fxml", editCustomer);

            newScene.createPopupScene();

            UpdateCustomerController childController = newScene.getSceneLoader().getController();

            childController.changedCustomer().addListener((obs, oldCustomer, updatedCustomer) -> {

                customers.remove(selectedCustomer);
                customers.add(updatedCustomer);

                updateCustomerDatabase(updatedCustomer);
            });

            childController.setFields(selectedCustomer);

        } else {
            CustomAlert.createAlert(Alert.AlertType.ERROR, parentStage, "Error!",
                    "Please click on  a customer from the table to update");
        }
    }

    //Update the customer's information in the database
    private void updateCustomerDatabase(Customer selectedCustomer) {

        DatabaseConnectivity dbConnection = new DatabaseConnectivity();
        try {
            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement("Update customer SET customerName = ? where customerId = ?");
            prepStatement.setString(1, selectedCustomer.getCustomerName());
            prepStatement.setInt(2, selectedCustomer.getCustomerID());

            prepStatement.executeUpdate();

            prepStatement = dbConnection.getConn().prepareStatement("SELECT addressId from customer where customerId = ?");
            prepStatement.setInt(1, selectedCustomer.getCustomerID());

            ResultSet rs = prepStatement.executeQuery();
            rs.next();

            prepStatement = dbConnection.getConn().prepareStatement("Update address SET address = ?, address2 = ?, postalCode = ?, phone = ? where addressId = ?");
            prepStatement.setString(1, selectedCustomer.getCustomerAddress().getAddress());
            prepStatement.setString(2, selectedCustomer.getCustomerAddress().getAddress2());
            prepStatement.setString(3, selectedCustomer.getCustomerAddress().getPostalCode());
            prepStatement.setString(4, selectedCustomer.getCustomerAddress().getPhone());
            prepStatement.setInt(5, rs.getInt("addressId"));

            prepStatement.executeUpdate();

            prepStatement = dbConnection.getConn().prepareStatement("SELECT cityId from address where addressId = ?");
            prepStatement.setInt(1, rs.getInt("addressId"));

            rs = prepStatement.executeQuery();
            rs.next();

            prepStatement = dbConnection.getConn().prepareStatement("Update city SET city = ? where cityId = ?");
            prepStatement.setString(1, selectedCustomer.getCustomerAddress().getCity());
            prepStatement.setInt(2, rs.getInt("cityId"));

            prepStatement.executeUpdate();

            prepStatement = dbConnection.getConn().prepareStatement("SELECT countryId from city where cityId = ?");
            prepStatement.setInt(1, rs.getInt("cityId"));

            rs = prepStatement.executeQuery();
            rs.next();

            prepStatement = dbConnection.getConn().prepareStatement("Update country SET country = ? where countryId = ?");
            prepStatement.setString(1, selectedCustomer.getCustomerAddress().getCountry());
            prepStatement.setInt(2, rs.getInt("countryId"));

            dbConnection.closeDbConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Deletes the customer from the ObservableList
    @FXML
    private void deleteCustomer() throws IOException {

        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {

            CustomAlert.createHiddenAlert(Alert.AlertType.CONFIRMATION, parentStage, "Delete Customer!",
                    "Are you sure you want to delete " + selectedCustomer.getCustomerName() + "?");

            Optional<ButtonType> result = CustomAlert.getAlert();

            if (result.get() == ButtonType.OK) {
                customers.remove(selectedCustomer);
                deleteCustomerDatabase(selectedCustomer);
            }

        } else {
            CustomAlert.createAlert(Alert.AlertType.ERROR, parentStage, "Error!",
                    "Please click on  a customer from the table to delete");
        }
    }

    //Deletes the customer from the database
    private void deleteCustomerDatabase(Customer selectedCustomer) {

        DatabaseConnectivity dbConnection = new DatabaseConnectivity();
        try {
            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement("Delete from customer where customerId = ?");
            prepStatement.setInt(1, selectedCustomer.getCustomerID());

            prepStatement.executeUpdate();

            dbConnection.closeDbConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

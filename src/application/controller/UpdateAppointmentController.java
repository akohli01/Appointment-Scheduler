package application.controller;

import application.dbconnectivity.DatabaseConnectivity;
import application.model.Appointment;
import application.model.Customer;
import application.utility.CustomAlert;
import application.utility.CustomerListHelper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Amit Kohli
 */
public class UpdateAppointmentController {

    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, String> customerName;
    @FXML
    private TextField title;
    @FXML
    private ComboBox description;
    @FXML
    private TextField appointmentLocation;
    @FXML
    private TextField contact;
    @FXML
    private DatePicker date;
    @FXML
    private Spinner hourStart;
    @FXML
    private Spinner minuteStart;
    @FXML
    private Spinner hourEnd;
    @FXML
    private Spinner minuteEnd;
    @FXML
    private ComboBox startPeriod;
    @FXML
    private ComboBox endPeriod;
    @FXML
    private AnchorPane updateAppointmentPane;
    @FXML
    private TextField filterField;

    private int appointmentID;

    private final SpinnerValueFactory<Integer> hourStartFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 12);
    private final SpinnerValueFactory<Integer> hourEndFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 12);
    private final SpinnerValueFactory<Integer> minuteStartFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(00, 59);
    private final SpinnerValueFactory<Integer> minuteEndFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(00, 59);

    private ObservableList<Customer> customers;
    private final ObservableList<String> options
            = FXCollections.observableArrayList(
                    "AM",
                    "PM"
            );

    private final ObservableList<String> descriptions
            = FXCollections.observableArrayList(
                    "Consultation",
                    "New Account",
                    "Follow Up",
                    "Close Account"
            );

    private final ReadOnlyObjectWrapper<Appointment> changedAppointment = new ReadOnlyObjectWrapper<>();

    ////Returns the appointment object
    public ReadOnlyObjectProperty<Appointment> changedAppointment() {
        return changedAppointment.getReadOnlyProperty();
    }

    //Cancels the operation
    @FXML
    private void cancel() {
        ((Stage) title.getScene().getWindow()).close();
    }

    @FXML
    public void initialize() {
        customers = CustomerListHelper.getObservableList();
        customerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        
        
         FilteredList<Customer> filteredData = new FilteredList<>(customers);
         
          
         filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(customer -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                   
                    return true;
                }
                
            
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
  
                return customer.getCustomerName().toLowerCase().startsWith(lowerCaseFilter); 
            });
        });
         
         //Wrap the FilteredList in a SortedList. 
          SortedList<Customer> sortedData = new SortedList<>(filteredData);
        
        //Bind the SortedList comparator to the TableView comparator.
         sortedData.comparatorProperty().bind(customerTableView.comparatorProperty());
        
        customerTableView.setItems(sortedData);
        
        
        

        startPeriod.setItems(options);
        startPeriod.setDisable(true);

        endPeriod.setItems(options);
        endPeriod.setDisable(true);

        description.setItems(descriptions);

        hourStart.setValueFactory(hourStartFactory);
        minuteStart.setValueFactory(minuteStartFactory);
        hourEnd.setValueFactory(hourEndFactory);
        minuteEnd.setValueFactory(minuteEndFactory);

        hourStart.setEditable(true);
        minuteStart.setEditable(true);
        hourEnd.setEditable(true);
        minuteEnd.setEditable(true);

        hourStart.valueProperty().addListener((obs, oldValue, newValue) -> {
            if ((int) newValue >= 9 && (int) newValue <= 11) {
                startPeriod.getSelectionModel().select("AM");
                startPeriod.setOpacity(1);
            }

            if (((int) newValue >= 1 && (int) newValue <= 5) || (int) newValue == 12) {
                startPeriod.getSelectionModel().select("PM");
                startPeriod.setOpacity(1);
            } else if (((int) newValue >= 6 && (int) newValue <= 8) || (int) newValue == 0) {
                startPeriod.getSelectionModel().select(0);
                startPeriod.setOpacity(0);
            }
        });

        hourEnd.valueProperty().addListener((obs, oldValue, newValue) -> {
            if ((int) newValue >= 9 && (int) newValue <= 11) {
                endPeriod.getSelectionModel().select("AM");
                endPeriod.setOpacity(1);
            }

            if (((int) newValue >= 1 && (int) newValue <= 5) || (int) newValue == 12) {
                endPeriod.getSelectionModel().select("PM");
                endPeriod.setOpacity(1);
            } else if (((int) newValue >= 6 && (int) newValue <= 8) || (int) newValue == 0) {
                endPeriod.getSelectionModel().select(0);
                endPeriod.setOpacity(0);
            }
        });
        
        hourStart.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                hourStart.increment(0); // won't change value, but will commit editor
            }
        });

        hourEnd.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                hourEnd.increment(0); // won't change value, but will commit editor
            }
        });

        date.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });
    }

    //Creates the update appointment object
    @FXML
    public void updateAppointment() {
        if (isNull() == true) {
            return;
        }

        //Add a method to check for null input
        LocalTime appointmentStartTime = LocalTime.of((int) hourStart.getValue(), (int) minuteStart.getValue());
        LocalTime appointmentEndTime = LocalTime.of((int) hourEnd.getValue(), (int) minuteEnd.getValue());
        LocalDate appointmentDate = date.getValue();

        if (checkValidTime(appointmentStartTime, appointmentEndTime) == true) {
            if (checkForOverlappingAppointments(appointmentDate, appointmentStartTime, appointmentEndTime, appointmentID) != true) {
                Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

                Appointment newAppointment
                        = new Appointment(appointmentID, selectedCustomer.getCustomerID(), title.getText(), (String) description.getValue(), appointmentLocation.getText(), contact.getText(), appointmentStartTime,
                                appointmentEndTime, appointmentDate);

                changedAppointment.set(newAppointment);

                ((Stage) title.getScene().getWindow()).close();
            }
        }
    }

    public boolean checkForOverlappingAppointments(LocalDate date, LocalTime startTime, LocalTime endTime, int appointmentID) {

        DatabaseConnectivity dbConnection = new DatabaseConnectivity();

        try {
            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement(
                    "SELECT Count(*) as OverlappingAppointments "
                    + "FROM appointment WHERE ((start BETWEEN ? AND ?) OR (end BETWEEN ? AND ?)) AND appointmentId != ?;");

            LocalDateTime appointmentStart = LocalDateTime.of(date, startTime);
            LocalDateTime appointmentEnd = LocalDateTime.of(date, endTime);

            ZonedDateTime zdtAppointmentStart = appointmentStart.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime zdtAppointmentEnd = appointmentEnd.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

            Timestamp appointmentStartUTC = Timestamp.valueOf(zdtAppointmentStart.toLocalDateTime());
            Timestamp appointmentEndUTC = Timestamp.valueOf(zdtAppointmentEnd.toLocalDateTime());

            prepStatement.setTimestamp(1, appointmentStartUTC);
            prepStatement.setTimestamp(2, appointmentEndUTC);
            prepStatement.setTimestamp(3, appointmentStartUTC);
            prepStatement.setTimestamp(4, appointmentEndUTC);
            prepStatement.setInt(5, appointmentID);

            ResultSet resultSet = prepStatement.executeQuery();

            if (resultSet.next()) {

                int overlappingAppointments = resultSet.getInt("OverlappingAppointments");

                if (overlappingAppointments >= 1) {
                    CustomAlert.createAlert(Alert.AlertType.ERROR, (Stage) title.getScene().getWindow(), "Error!",
                            "There is an existing appointment that conflicts with your appointment start and end times");

                    return true;
                }
            }
             dbConnection.closeDbConnection();
        } catch (SQLException sqe) {
            System.out.println("SQL is incorrect");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Something besides the SQL went wrong.");
            e.printStackTrace();
        }
        return false;
    }

    //Checks if appointment times are valid
    private boolean checkValidTime(LocalTime startTime, LocalTime endTime) {
        if ((int) hourStart.getValue() == 0 || ((int) hourStart.getValue() >= 5 && (int) hourStart.getValue() <= 8)) {
            CustomAlert.createAlert(Alert.AlertType.INFORMATION, (Stage) title.getScene().getWindow(), "Upcoming Appointment",
                    "Sorry, but you cannot schedule an appointment from " + startTime + startPeriod.getValue() + "-" + endTime + endPeriod.getValue() + " \n"
                    + "Business hours are from 9:00 AM - 5:00 PM ");
            return false;
        }

        if ((int) hourEnd.getValue() == 0 || ((int) hourEnd.getValue() >= 6 && (int) hourEnd.getValue() <= 8
                || ((int) hourEnd.getValue() == 9 && (int) minuteEnd.getValue() == 0))) {
            CustomAlert.createAlert(Alert.AlertType.INFORMATION, (Stage) title.getScene().getWindow(), "Upcoming Appointment",
                    "Sorry, but you cannot schedule an appointment from " + startTime + startPeriod.getValue() + "-" + endTime + endPeriod.getValue() + " \n"
                    + "Business hours are from 9:00 AM - 5:00 PM ");
            return false;
        }

        if ((startPeriod.getValue().equals("AM") && endPeriod.getValue().equals("AM")) || (startPeriod.getValue().equals("PM") && endPeriod.getValue().equals("PM"))) {

            if((int)hourStart.getValue() != 12){
                  if ((int) hourStart.getValue() > (int) hourEnd.getValue() || ((int) hourStart.getValue() == (int) hourEnd.getValue() && (int) minuteStart.getValue()
                    > (int) minuteEnd.getValue())) {
                CustomAlert.createAlert(Alert.AlertType.INFORMATION, (Stage) title.getScene().getWindow(), "Upcoming Appointment",
                        "Sorry, your appointment start time cannot be after your end time \n"
                        + "Business hours are from 9:00 AM - 5:00 PM ");
                return false;
            }
                
            }
         
        }

        if (((int) hourStart.getValue() == (int) hourEnd.getValue()) && ((int) minuteStart.getValue() == (int) minuteEnd.getValue())) {
            CustomAlert.createAlert(Alert.AlertType.INFORMATION, (Stage) title.getScene().getWindow(), "Upcoming Appointment",
                    "Sorry, your appointment start and appointment end times cannot be the same \n"
                    + "Business hours are from 9:00 AM - 5:00 PM ");
            return false;
        }

        if (!startPeriod.getValue().equals(endPeriod.getValue())) {

            if ((int) hourStart.getValue() == 12) {
                if (endPeriod.getValue().equals("AM")) {
                    CustomAlert.createAlert(Alert.AlertType.INFORMATION, (Stage) title.getScene().getWindow(), "Upcoming Appointment",
                            "Sorry, your appointment start time cannot be after your end time \n"
                            + "Business hours are from 9:00 AM - 5:00 PM ");
                    return false;
                }
    
            }
        }
        return true;
    }

    //Checks for any null inputs 
    private boolean isNull() {

       Set<Node> nodes = updateAppointmentPane.lookupAll(".text-field");
          for( Node node: nodes) {
             if (((TextField) node).getText().trim().isEmpty() && !((TextField) node).equals(filterField)) {
                    CustomAlert.createAlert(Alert.AlertType.INFORMATION, (Stage) title.getScene().getWindow(), "Null error",
                            "Please fill in all text fields ");
                    return true;
                }
         }

        if (description.getSelectionModel().isEmpty()) {
            CustomAlert.createAlert(Alert.AlertType.INFORMATION, (Stage) title.getScene().getWindow(), "Null error",
                    "Please select a value from all dropdowns ");
            return true;
        }

        Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

        if (selectedCustomer == null) {
            CustomAlert.createAlert(Alert.AlertType.INFORMATION, (Stage) title.getScene().getWindow(), "Null error",
                    "Please select a customer for this appointment");
            return true;
        }

        if (date.getValue() == null) {
            CustomAlert.createAlert(Alert.AlertType.INFORMATION, (Stage) title.getScene().getWindow(), "Null error",
                    "Please select a date for this appointment");
            return true;
        }
        return false;
    }

    public void setFields(Appointment editAppointment) {

        title.setText(editAppointment.getTitle());
        description.setValue(editAppointment.getDescription());
        appointmentLocation.setText(editAppointment.getLocation());
        contact.setText(editAppointment.getContact());
        date.setValue(editAppointment.getDate());
        hourStart.getValueFactory().setValue(editAppointment.getStartTime().getHour());
        minuteStart.getValueFactory().setValue(editAppointment.getStartTime().getMinute());
        hourEnd.getValueFactory().setValue(editAppointment.getEndTime().getHour());
        minuteEnd.getValueFactory().setValue(editAppointment.getEndTime().getMinute());

        appointmentID = editAppointment.getAppointmentID();
    }
}

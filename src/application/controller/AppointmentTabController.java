package application.controller;

import application.dbconnectivity.DatabaseConnectivity;
import application.main.SchedulingApplication;
import application.model.Appointment;
import application.utility.CustomAlert;
import application.utility.SceneHandler;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Amit Kohli
 */
public class AppointmentTabController {

    @FXML
    private TableView<Appointment> appointmentTableView;
    @FXML
    private TableColumn<Appointment, LocalDate> date;
    @FXML
    private TableColumn<Appointment, LocalTime> startTime;
    @FXML
    private TableColumn<Appointment, LocalTime> endTime;
    @FXML
    private TableColumn<Appointment, String> title;
    @FXML
    private TableColumn<Appointment, String> description;
    @FXML
    private TableColumn<Appointment, String> appointmentLocation;
    @FXML
    private TableColumn<Appointment, String> contact;

    private Stage parentStage;

    @FXML
    AnchorPane appointmentTab;

    @FXML
    private RadioButton allView;

    @FXML
    private RadioButton monthlyView;

    @FXML
    private RadioButton weeklyView;

    private ToggleGroup appointmentViewToggle;

    private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        //Create a ToggleGroup for the views
        appointmentViewToggle = new ToggleGroup();
        //Add view RadioButtons to ToggleGroup
        this.allView.setToggleGroup(appointmentViewToggle);
        //Set view all as default
        this.allView.setSelected(true);
        // this.allView.setUserData("allView");
        this.monthlyView.setToggleGroup(appointmentViewToggle);
        // this.monthlyView.setUserData("monthlyView");
        this.weeklyView.setToggleGroup(appointmentViewToggle);
        // this.weeklyView.setUserData("weeklyView");

        //Listener that executes the appropriate function based upon the selected RadioButton
        //Uses a lamdba expression to handle listener changes
        appointmentViewToggle.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) -> {
            switch (((RadioButton) appointmentViewToggle.getSelectedToggle()).getText()) {
                case "All":
                    setAllView();
                    break;
                case "Weekly View":
                    setWeeklyView();
                    break;
                case "Monthly View":
                    setMonthlyView();
                    break;
                default:
                    break;
            }
        });

        //Bind the appropriate fields to the appointmentTableView
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        startTime.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTime.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        contact.setCellValueFactory(new PropertyValueFactory<>("contact"));

        //Add all appointments to the appointmentTableView
        appointmentTableView.getItems().setAll(getAppointments());

        appointmentTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                    
                    showDetailedAppointmentInformation(appointmentTableView.getSelectionModel().getSelectedItem());
                    
                    //System.out.println(appointmentTableView.getSelectionModel().getSelectedItem());
                }
            }
        });

        //Listener that refreshes the appointmentTableView if data is added or removed
        //Uses lambdas to handle changes to the appointment list
        appointments.addListener((ListChangeListener.Change<? extends Appointment> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    appointmentTableView.getItems().setAll(appointments);
                } else if (c.wasRemoved()) {
                    appointmentTableView.getItems().setAll(appointments);
                }
            }
        });
    }

    //Gets all the appointments that correspond to the user that logged in 
    public ObservableList<Appointment> getAppointments() {
        DatabaseConnectivity dbConnection = new DatabaseConnectivity();

        try {
            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement(
                    "SELECT appointmentId, title, description, location, contact, start, end, customerId "
                    + "FROM appointment WHERE appointment.createdBy = ?;");

            prepStatement.setString(1, SchedulingApplication.getUsername());

            ResultSet resultSet = prepStatement.executeQuery();

            while (resultSet.next()) {

                int appointmentID = resultSet.getInt("appointmentId");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                String contact = resultSet.getString("contact");

                int customerId = resultSet.getInt("customerId");

                ZonedDateTime startTime = resultSet.getTimestamp("start").toLocalDateTime().atZone(ZoneId.of("UTC"));
                startTime = startTime.withZoneSameInstant(ZoneId.systemDefault());
                LocalTime localStartTime = startTime.toLocalDateTime().toLocalTime();

                ZonedDateTime endTime = resultSet.getTimestamp("end").toLocalDateTime().atZone(ZoneId.of("UTC"));
                endTime = endTime.withZoneSameInstant(ZoneId.systemDefault());
                LocalTime localEndTime = endTime.toLocalDateTime().toLocalTime();

                LocalDate localDate = startTime.toLocalDateTime().toLocalDate();

                appointments.add(new Appointment(appointmentID, customerId, title, description, location, contact, localStartTime, localEndTime, localDate));

            }

            dbConnection.closeDbConnection();

        } catch (SQLException sqe) {
            System.out.println("SQL syntax/logic is incorrect");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Something besides the SQL went wrong.");
            e.printStackTrace();
        }

        return appointments;
    }

    //Displays all appointments without any filtering
    private void setAllView() {
        appointmentTableView.setItems(appointments);
    }

    //Displays appointment within a week from current date
    private void setWeeklyView() {
        LocalDate currentDate = LocalDate.now();

        FilteredList<Appointment> weeklyView = new FilteredList<>(appointments);
        weeklyView.setPredicate(row -> {

            return (row.getDate().isAfter(currentDate) && row.getDate().isBefore(currentDate.plusDays(8))) || row.getDate().isEqual(currentDate);
        });
        appointmentTableView.setItems(weeklyView);
    }

    //Displays appointments within a month from current date
    private void setMonthlyView() {
        LocalDate currentDate = LocalDate.now();

        FilteredList<Appointment> monthlyView = new FilteredList<>(appointments);
        monthlyView.setPredicate(row -> {

            return (row.getDate().isAfter(currentDate) && row.getDate().isBefore(currentDate.plusMonths(1)))
                    || row.getDate().isEqual(currentDate) || row.getDate().isEqual(currentDate.plusMonths(1));
        });
        appointmentTableView.setItems(monthlyView);
    }

    //Creates the newAppointmentTab
    @FXML
    private void createNewAppointmentTab(MouseEvent event) throws Exception {
        parentStage = (Stage) appointmentTab.getScene().getWindow();

        Stage addAppointment = new Stage();

        addAppointment.setTitle("New Appointment");
        addAppointment.initModality(Modality.WINDOW_MODAL);
        addAppointment.initOwner(parentStage);
        addAppointment.initStyle(StageStyle.UNDECORATED);

        SceneHandler newScene = new SceneHandler("/application/view/AddAppointment.fxml", addAppointment);
        newScene.createPopupScene();

        AddAppointmentController childController = newScene.getSceneLoader().getController();

        childController.addedAppointment().addListener((obs, oldAppointment, newAppointment) -> {

            appointments.add(addAppointmentDatabase(newAppointment));
        });
    }

    //Adds the appointment information to a database
    private Appointment addAppointmentDatabase(Appointment appointment) {
        DatabaseConnectivity dbConnection = new DatabaseConnectivity();
        try {

            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement("INSERT into appointment (customerId, title, description, location, contact, start, end, "
                    + "url, createDate, createdBy, lastUpdate, lastUpdateBy)"
                    + " VALUES(?,?, ?, ?, ?, ? , ? ,?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ? )", Statement.RETURN_GENERATED_KEYS);

            LocalDateTime appointmentStart = LocalDateTime.of(appointment.getDate(), appointment.getStartTime());
            LocalDateTime appointmentEnd = LocalDateTime.of(appointment.getDate(), appointment.getEndTime());

            ZonedDateTime zdtAppointmentStart = appointmentStart.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime zdtAppointmentEnd = appointmentEnd.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

            Timestamp appointmentStartUTC = Timestamp.valueOf(zdtAppointmentStart.toLocalDateTime());
            Timestamp appointmentEndUTC = Timestamp.valueOf(zdtAppointmentEnd.toLocalDateTime());

            prepStatement.setInt(1, appointment.getCustomerID());
            prepStatement.setString(2, appointment.getTitle());
            prepStatement.setString(3, appointment.getDescription());
            prepStatement.setString(4, appointment.getLocation());
            prepStatement.setString(5, appointment.getContact());
            prepStatement.setTimestamp(6, appointmentStartUTC);
            prepStatement.setTimestamp(7, appointmentEndUTC);
            prepStatement.setString(8, "");
            prepStatement.setString(9, SchedulingApplication.getUsername());
            prepStatement.setString(10, SchedulingApplication.getUsername());

            prepStatement.executeUpdate();
            ResultSet rs = prepStatement.getGeneratedKeys();

            if (rs.next()) {
                appointment.setAppointmentID(rs.getInt(1));
            }

            dbConnection.closeDbConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointment;
    }

    //Creates the update appointment tab
    @FXML
    private void createUpdateAppointmentTab(MouseEvent event) throws IOException, InterruptedException {

        Appointment selectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {

            parentStage = (Stage) appointmentTab.getScene().getWindow();

            Stage updateAppointment = new Stage();

            updateAppointment.setTitle("Edit Customer");
            updateAppointment.initModality(Modality.WINDOW_MODAL);
            updateAppointment.initOwner(parentStage);
            updateAppointment.initStyle(StageStyle.UNDECORATED);

            SceneHandler newScene = new SceneHandler("/application/view/UpdateAppointment.fxml", updateAppointment);

            newScene.createPopupScene();

            UpdateAppointmentController childController = newScene.getSceneLoader().getController();

            childController.changedAppointment().addListener((obs, oldAppointment, updatedAppointment) -> {

                appointments.remove(selectedAppointment);
                appointments.add(updatedAppointment);

                updateAppointmentDatabase(updatedAppointment);

            });

            childController.setFields(selectedAppointment);

        } else {
            CustomAlert.createAlert(Alert.AlertType.ERROR, parentStage, "Error!",
                    "Please click on an appointment from the table to update");
        }
    }

    //Updates the appointment information in the database
    private void updateAppointmentDatabase(Appointment selectedAppointment) {
        DatabaseConnectivity dbConnection = new DatabaseConnectivity();
        try {
            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement("Update appointment "
                    + "SET customerId = ?, title = ?, description = ?, location = ?, contact = ?, start = ?, end = ?, url = ?, createDate = CURRENT_TIMESTAMP, "
                    + "createdBy = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ?  where appointmentId = ?");

            LocalDateTime appointmentStart = LocalDateTime.of(selectedAppointment.getDate(), selectedAppointment.getStartTime());
            LocalDateTime appointmentEnd = LocalDateTime.of(selectedAppointment.getDate(), selectedAppointment.getEndTime());

            ZonedDateTime zdtAppointmentStart = appointmentStart.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime zdtAppointmentEnd = appointmentEnd.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

            Timestamp appointmentStartUTC = Timestamp.valueOf(zdtAppointmentStart.toLocalDateTime());
            Timestamp appointmentEndUTC = Timestamp.valueOf(zdtAppointmentEnd.toLocalDateTime());

            prepStatement.setInt(1, selectedAppointment.getCustomerID());
            prepStatement.setString(2, selectedAppointment.getTitle());
            prepStatement.setString(3, selectedAppointment.getDescription());
            prepStatement.setString(4, selectedAppointment.getLocation());
            prepStatement.setString(5, selectedAppointment.getContact());
            prepStatement.setTimestamp(6, appointmentStartUTC);
            prepStatement.setTimestamp(7, appointmentEndUTC);
            prepStatement.setString(8, "");
            prepStatement.setString(9, SchedulingApplication.getUsername());
            prepStatement.setString(10, SchedulingApplication.getUsername());
            prepStatement.setInt(11, selectedAppointment.getAppointmentID());

            prepStatement.executeUpdate();

            dbConnection.closeDbConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Deletes the appointment from the ObservableList
    @FXML
    private void deleteAppointment() throws IOException {

        Appointment selectedAppointment = appointmentTableView.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {

            CustomAlert.createHiddenAlert(Alert.AlertType.CONFIRMATION, parentStage, "Delete Appointment!",
                    "Are you sure you want to delete " + selectedAppointment.getTitle() + "?");

            Optional<ButtonType> result = CustomAlert.getAlert();

            if (result.get() == ButtonType.OK) {
                appointments.remove(selectedAppointment);
                deleteAppointmentDatabase(selectedAppointment);
            }

        } else {
            CustomAlert.createAlert(Alert.AlertType.ERROR, parentStage, "Error!",
                    "Please click on  a customer from the table to delete");
        }
    }

    //Deletes the appointment from the database
    private void deleteAppointmentDatabase(Appointment selectedAppointment) {

        DatabaseConnectivity dbConnection = new DatabaseConnectivity();
        try {
            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement("Delete from appointment where appointmentId = ?");
            prepStatement.setInt(1, selectedAppointment.getAppointmentID());

            prepStatement.executeUpdate();

            dbConnection.closeDbConnection();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void showDetailedAppointmentInformation(Appointment appointment){
        
        DatabaseConnectivity dbConnection = new DatabaseConnectivity();

        try {
            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement(
                    "SELECT customerName, address, address2, city, postalCode, phone, country "
                    + "FROM customer INNER JOIN address ON customer.addressId = address.addressId "
                    + "INNER JOIN city ON address.cityId = city.cityId INNER JOIN country ON city.countryId = country.countryId "
                            + "WHERE customerId = ? ;");
            
            
            prepStatement.setInt(1, appointment.getCustomerID());

            ResultSet resultSet = prepStatement.executeQuery();

            if(resultSet.next()) {
                String customerName = resultSet.getString("customerName");
                String address = resultSet.getString("address");
                String address2 = resultSet.getString("address");
                String city = resultSet.getString("city");
                String postalCode = resultSet.getString("postalCode");
                String phone = resultSet.getString("phone");
                String country = resultSet.getString("country");
                
                String appointmentMessage = String.format("Appointment %n \t Title: %-10s %n \t Description: %-10s %n \t Location: %-10s %n \t Contact: %-10s "
                        + " %n \t Date: %-10s %n \t Start Time: %-10s %n \t End Time: %-10s %n %n",
                        appointment.getTitle(), appointment.getDescription(), appointment.getLocation(), appointment.getContact(), appointment.getDate(),
                        appointment.getStartTime(), appointment.getEndTime());
                
                String customerMessage = String.format("Customer %n \t Name: %-10s %n \t Address: %-10s %n \t Address2: %-10s %n \t City: %-10s "
                        + " %n \t Postal Code: %-10s %n \t Country: %-10s %n \t Phone: %-10s ",
                        customerName, address, address2, city, postalCode, country, phone);

                CustomAlert.createAlert(Alert.AlertType.INFORMATION, (Stage)appointmentTab.getScene().getWindow(), "Detailed Appointment Information",
                                    appointmentMessage + customerMessage);  
            }

            dbConnection.closeDbConnection();

        } catch (SQLException sqe) {
            System.out.println("SQL syntax/logic is incorrect");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("An exception has occurred");
            e.printStackTrace();
        }
        
    }
}

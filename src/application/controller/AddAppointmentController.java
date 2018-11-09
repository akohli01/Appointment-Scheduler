package application.controller;

import application.model.Appointment;
import application.model.Customer;

import java.time.LocalDate;
import java.time.LocalTime;

import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Amit Kohli
 */
public class AddAppointmentController extends AppointmentCRUDController {

    //Create the appointment object
    @FXML
    public void createAppointment() {
        if (isNull()) {
            return;
        }

        LocalTime appointmentStartTime = LocalTime.of((int) hourStart.getValue(), (int) minuteStart.getValue());
        LocalTime appointmentEndTime = LocalTime.of((int) hourEnd.getValue(), (int) minuteEnd.getValue());
        LocalDate appointmentDate = date.getValue();

        if (checkValidTime(appointmentStartTime, appointmentEndTime) == true) {
            if (checkForOverlappingAppointments(appointmentDate, appointmentStartTime, appointmentEndTime,-1) != true) {
                Customer selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();

                Appointment newAppointment
                        = new Appointment(selectedCustomer.getCustomerID(), title.getText(), (String) description.getValue(), appointmentLocation.getText(), contact.getText(), appointmentStartTime,
                                appointmentEndTime, appointmentDate);

                appointmentPOJO.set(newAppointment);

                ((Stage) title.getScene().getWindow()).close();
            }
        }
    }
}

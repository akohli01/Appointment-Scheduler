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
public class UpdateAppointmentController extends AppointmentCRUDController {


    //Creates the update appointment object
    @FXML
    public void updateAppointment() {
        if (isNull()) {
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

                appointmentPOJO.set(newAppointment);

                ((Stage) title.getScene().getWindow()).close();
            }
        }
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

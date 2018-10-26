package application.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author Amit Kohli
 */
public class Appointment {

    private String title, description, location, contact;
    private LocalTime startTime, endTime;
    private int customerID, appointmentID;
    private LocalDate date;

    //Overloaded Constructors
    public Appointment(int customerID, String title, String description, String location, String contact, LocalTime startTime, LocalTime endTime, LocalDate date) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerID = customerID;
        this.date = date;
    }

    public Appointment(int appointmentID, int customerID, String title, String description, String location, String contact, LocalTime startTime, LocalTime endTime, LocalDate date) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerID = customerID;
        this.date = date;
        this.appointmentID = appointmentID;
    }

    //Getters and Setters
    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

}

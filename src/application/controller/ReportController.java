package application.controller;

import application.dbconnectivity.DatabaseConnectivity;
import application.main.SchedulingApplication;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javax.swing.Timer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Amit Kohli
 */
public class ReportController {

    @FXML
    private Label appointmentTypesLabel;

    @FXML
    private Label consultantScheduleLabel;

    @FXML
    private Label customerPerCityLabel;

    //Generates the appointment type per month report
    @FXML
    private void generateAppointmentTypesReport() throws InterruptedException {

        DatabaseConnectivity dbConnection = new DatabaseConnectivity();

        try {
            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement(
                    "SELECT  MONTHNAME(`start`) AS month, description, COUNT(*) as amount"
                    + " FROM appointment GROUP BY MONTHNAME(`start`), description;");

            ResultSet resultSet = prepStatement.executeQuery();

            String dataRow;

            try (PrintWriter writer = new PrintWriter(new File("AppointmentTypesPerMonthReport.txt"))) {
                
                dataRow = String.format("%-20s %-30s  %-10s %n", "Month", "Type", "Amount" );
                writer.println(dataRow);

                while (resultSet.next()) {

                    String month = resultSet.getString("month");

                    String type = resultSet.getString("description");

                    String amount = resultSet.getString("amount");

                    dataRow = String.format("%-20s %-30s  %-10s %n", month, type, amount);

                    writer.println(dataRow);
                }

            }

            dbConnection.closeDbConnection();
        } catch (SQLException sqe) {
            System.out.println("SQL syntax/logic is incorrect");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Another exception has occurred.");
            e.printStackTrace();
        }

        appointmentTypesLabel.setOpacity(1);

        Timer t = new Timer(2000, new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                appointmentTypesLabel.setOpacity(0);
            }
        });

        t.start();
    }

    //Creates the schedule for each consultant report
    @FXML
    private void generateConsultantScheduleReport() {
        DatabaseConnectivity dbConnection = new DatabaseConnectivity();
        try {

            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement(
                    "SELECT title, description, location, contact, start, end, customerId "
                    + "FROM appointment WHERE appointment.createdBy = ?;");

            prepStatement.setString(1, SchedulingApplication.getUsername());
            ResultSet resultSet = prepStatement.executeQuery();

            String dataRow;

            try (PrintWriter writer = new PrintWriter(new File("ConsultantScheduleReport.txt"))) {
                
                dataRow = String.format("%-15s %-20s %-15s %-15s %-15s %-15s %-15s %n", "Title", "Description", "Location", "Contact", "Start", "End", 
                        "Date");

                    writer.println(dataRow);

                while (resultSet.next()) {
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

                    dataRow = String.format("%-15s %-20s %-15s %-15s %-15s %-15s %-15s %n", title, description, location, contact, localStartTime, localEndTime, localDate);

                    writer.println(dataRow);
                }
            }

            dbConnection.closeDbConnection();
        } catch (SQLException sqe) {
            System.out.println("SQL syntax/logic is incorrect");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Another exception has occurred.");
            e.printStackTrace();
        }
        consultantScheduleLabel.setOpacity(1);

        Timer t = new Timer(2000, new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                consultantScheduleLabel.setOpacity(0);
            }
        });

        t.start();
    }

    //Generates the customers per city report
    @FXML
    private void generateCustomersPerCityReport() {
        DatabaseConnectivity dbConnection = new DatabaseConnectivity();
        try {

            PreparedStatement prepStatement = dbConnection.getConn().prepareStatement(
                    "SELECT city , COUNT(city)  "
                    + "FROM customer, address, city "
                    + "WHERE customer.addressId = address.addressId "
                    + "AND address.cityId = city.cityId "
                    + "GROUP BY city");
            ResultSet resultSet = prepStatement.executeQuery();

            String dataRow;

            try (PrintWriter writer = new PrintWriter(new File("CustomersPerCityReport.txt"))) {
                
                dataRow = String.format("%-30s %-10s %n", "City", "Count");

                    writer.println(dataRow);

                while (resultSet.next()) {
                    String city = resultSet.getString("city");
                    Integer count = resultSet.getInt("COUNT(city)");

                    dataRow = String.format("%-30s %-10s %n", city, count);

                    writer.println(dataRow);
                }
            }
            dbConnection.closeDbConnection();
        } catch (SQLException sqe) {
            System.out.println("SQL syntax/logic is incorrect");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Another exception has occurred.");
            e.printStackTrace();
        }
        customerPerCityLabel.setOpacity(1);

        Timer t = new Timer(2000, new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                customerPerCityLabel.setOpacity(0);
            }
        });

        t.start();
    }
}

package com.ro.ssc.app.client.model.commons;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Catalin Tudorache <catalin.tudorache@asml.com>
 */
public class User {

    private String name;
    private String userId;
    private String userNo;
    private String cardNo;
    private String department;

    private List<Event> events;

    public User(String name, String userId, String cardNo, String department, List<Event> events) {
        this.name = name;
        this.userId = userId;
        this.userNo = userId;
        this.cardNo = cardNo;
        this.events = events;
        this.department = department;
    }

    public void getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return;
        }

        System.out.println("MySQL JDBC Driver Registered!");

        try {
            connection = DriverManager
                    .getConnection("jdbc:mysql://localhost:3306/mkyongcom", "root", "password");

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getCardNo() {
        return cardNo;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

}

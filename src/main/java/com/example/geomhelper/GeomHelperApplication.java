package com.example.geomhelper;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;

@SpringBootApplication
public class GeomHelperApplication {

    public static Statement statement;

    private static final String URL = "jdbc:mysql://localhost:3306/geomhelper";
    private static final String NAME = "root";
    private static final String PASS = "root";

    public static void main(String[] args) {
        SpringApplication.run(GeomHelperApplication.class, args);

        try {
            Driver driver = new FabricMySQLDriver();
            DriverManager.registerDriver(driver);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Connection connection = DriverManager.getConnection(URL,NAME,PASS);
            statement = connection.createStatement();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
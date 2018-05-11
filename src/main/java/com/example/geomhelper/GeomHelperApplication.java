package com.example.geomhelper;

import com.mysql.fabric.jdbc.FabricMySQLDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;
import java.sql.*;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class GeomHelperApplication {

    public static Statement statement;
    public static Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/geomhelper?useUnicode=yes&characterEncoding=UTF-8";
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
            connection = DriverManager.getConnection(URL,NAME,PASS);
            statement = connection.createStatement();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("10MB");
        factory.setMaxRequestSize("10MB");
        return factory.createMultipartConfig();
    }
}

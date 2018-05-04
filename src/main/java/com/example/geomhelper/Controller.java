package com.example.geomhelper;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.geomhelper.GeomHelperApplication.statement;

@org.springframework.stereotype.Controller
public class Controller {

    @RequestMapping("/post")
    @ResponseBody
    String post(@RequestParam("email") String email,
                @RequestParam("password") String password,
                @RequestParam("name") String name) {
        try {
            String t = "select * from users where email = " + email;
            ResultSet resultSet = statement.executeQuery(t);
            if (resultSet.next())
                return "2";

            String f = "insert into users (email,password,name,experience) values(%s,'%s','%s',0)";
            statement.execute(String.format(f, email, password, name));

            String r = "select * from users where email = %s and password = %s";
            ResultSet resultSet1 = statement.executeQuery(String.format(r, email, password));
            if (resultSet1.next())
                return resultSet1.getString("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    @RequestMapping("/get")
    @ResponseBody
    String get(@RequestParam("email") String email,
               @RequestParam("password") String password) {
        try {
            String f1 = "select * from users where email = %s";
            ResultSet resultSet1 = statement.executeQuery(String.format(f1, email));
            if (!resultSet1.next())
                return "2";

            String f = "select * from users where email = %s and password = %s";
            ResultSet resultSet = statement.executeQuery(String.format(f, email, password));

            User user = new User();
            if (resultSet.next()) {
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setExperience(resultSet.getInt("experience"));
                user.setCourses(resultSet.getString("courses"));
            } else return "3";

            Gson gson = new Gson();
            return gson.toJson(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    @RequestMapping("/put")
    @ResponseBody
    String put(@RequestParam("id") String id,
               @RequestParam("param") String param,
               @RequestParam("value") String value) {
        try {
            String f = "update users set %s = %s where id = %s";
            statement.execute(String.format(f, param, value, id));
            return "1";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

}

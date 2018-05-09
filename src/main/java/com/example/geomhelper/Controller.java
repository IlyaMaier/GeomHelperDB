package com.example.geomhelper;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.geomhelper.GeomHelperApplication.connection;
import static com.example.geomhelper.GeomHelperApplication.statement;

@org.springframework.stereotype.Controller
public class Controller {

    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    @ResponseBody
    String post(@RequestParam("email") String email,
                @RequestParam("password") String password,
                @RequestParam("name") String name) {
        try {
            PreparedStatement psCheck = connection.
                    prepareStatement("select * from users where email = ?");

            psCheck.setString(1, email);

            ResultSet result = psCheck.executeQuery();
            if (result.next())
                return "2";

            PreparedStatement psSignUp = connection.
                    prepareStatement(
                            "insert into users (email,password,name,experience) values(?,?,?,0)");

            psSignUp.setString(1, email);
            psSignUp.setString(2, password);
            psSignUp.setString(3, name);
            psSignUp.execute();

            PreparedStatement psCreated = connection.
                    prepareStatement("select *  from users where email = ? and password = ?");

            psCreated.setString(1, email);
            psCreated.setString(2, password);

            ResultSet resultCreated = psCreated.executeQuery();

            if (resultCreated.next())
                return resultCreated.getString("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    String get(@RequestParam("email") String email,
               @RequestParam("password") String password) {
        try {
            PreparedStatement psCheck = connection.
                    prepareStatement("select * from users where email = ?");

            psCheck.setString(1, email);

            ResultSet resultCheck = psCheck.executeQuery();

            if (!resultCheck.next())
                return "2";

            PreparedStatement ps = connection.
                    prepareStatement("select *  from users where email = ? and password = ?");

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet resultSet = ps.executeQuery();

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

    @RequestMapping(value = "/updateUser", method = RequestMethod.PUT)
    @ResponseBody
    String put(@RequestParam("id") String id,
               @RequestParam("param") String param,
               @RequestParam("value") String value) {
        try {
            String f = "update users set %s = '%s' where id = %s";

            PreparedStatement ps = connection.
                    prepareStatement(String.format(f, param, value, id));

            ps.execute();

            return "1";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    @RequestMapping("/getLeaders")
    @ResponseBody
    String getLeaders() {
        try {
            PreparedStatement ps = connection.
                    prepareStatement("select *  from users order by experience desc limit 10");

            ResultSet resultSet = ps.executeQuery();

            List<User> users = new ArrayList<>();

            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setExperience(resultSet.getInt("experience"));
                user.setCourses(resultSet.getString("courses"));
                users.add(user);
            }

            Gson gson = new Gson();
            return gson.toJson(users, List.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    @RequestMapping(value = "/setUserImage", method = RequestMethod.POST)
    @ResponseBody
    String image(@RequestParam("id") String id, @RequestParam("image") byte[] image) {
        System.out.println(Arrays.toString(image));
        String i = Arrays.toString(image).replace("[", "");
        String replace = i.replace("]", "");
        String f = "update users set image = '%s' where id = %s";
        try {
            PreparedStatement ps = connection.
                    prepareStatement("update users set image = ? where id = ?");

            ps.setString(1, replace);
            ps.setString(2, id);

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "0";
    }

    @RequestMapping(value = "/getImage", method = RequestMethod.GET)
    @ResponseBody
    String getimage(@RequestParam("id") String id) {
        try {
            PreparedStatement ps = connection.
                    prepareStatement("select image from users where id = ?");

            ps.setString(1, id);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next())
                return resultSet.getString("image").replace(" ", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

}

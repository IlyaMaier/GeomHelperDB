package com.example.geomhelper;

import com.google.gson.Gson;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.geomhelper.GeomHelperApplication.connection;

@org.springframework.stereotype.Controller
public class Controller {

    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    @ResponseBody
    String post(@RequestParam("email") String email,
                @RequestParam("password") String password,
                @RequestParam("name") String name) {
        PreparedStatement psCheck;
        try {
            psCheck = connection.
                    prepareStatement("select * from users where email = ?");

            psCheck.setString(1, email);

            ResultSet result = psCheck.executeQuery();
            if (result.next())
                return "2";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return register(email, password, name);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    String get(@RequestParam("email") String email,
               @RequestParam("password") String password) {
        if (checkEmail(email)) return "2";
        return login(email, password);
    }

    @RequestMapping(value = "/loginWithSocial", method = RequestMethod.POST)
    @ResponseBody
    String loginWithSocial(@RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam("name") String name) {
        if (checkEmail(email))
            return register(email, password, name);
        else return login(email, password);
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.PUT)
    @ResponseBody
    String put(@RequestParam("id") String id,
               @RequestParam("param") String param,
               @RequestParam("value") String value) {
        try {
            if (param.equals("experience")) {
                String f = "update users set %s = %s where id = %s";

                PreparedStatement ps = connection.
                        prepareStatement(String.format(f, param, value, id));
                ps.execute();

                return "1";
            } else {
                String f = "update users set %s = '%s' where id = %s";

                PreparedStatement ps = connection.
                        prepareStatement(String.format(f, param, value, id));
                ps.execute();

                return "1";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    @RequestMapping("/getLeaders")
    @ResponseBody
    String getLeaders(@RequestParam("desc") int desc) {
        try {
            String f = "select *  from users order by experience desc limit %s";
            PreparedStatement ps = connection.
                    prepareStatement(String.format(f, desc));

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

    @RequestMapping(value = "/changeEmail", method = RequestMethod.PUT)
    @ResponseBody
    String changeEmail(@RequestParam("id") String id,
                       @RequestParam("password") String password,
                       @RequestParam("email") String email) {
        PreparedStatement psCheck, ps, psCheckEmail;
        try {
            psCheck = connection.
                    prepareStatement("select password from users where id = ?");

            psCheck.setString(1, id);

            ResultSet result = psCheck.executeQuery();

            if (result.next()) {
                if (result.getString("password").equals(password)) {
                    psCheckEmail = connection.
                            prepareStatement("select id from users where email = ?");

                    psCheckEmail.setString(1, email);

                    ResultSet result2 = psCheckEmail.executeQuery();

                    if (!result2.next()) {
                        String f = "update users set email = ? where id = ?";
                        ps = connection.prepareStatement(f);

                        ps.setString(1, email);
                        ps.setInt(2, Integer.parseInt(id));

                        ps.execute();
                        return "1";
                    } else return "3";
                } else return "2";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    @RequestMapping(value = "/changePassword", method = RequestMethod.PUT)
    @ResponseBody
    String changePassword(@RequestParam("id") String id,
                          @RequestParam("password") String password,
                          @RequestParam("newPassword") String newPassword) {
        PreparedStatement psCheck, ps;
        try {
            psCheck = connection.
                    prepareStatement("select password from users where id = ?");

            psCheck.setString(1, id);

            ResultSet result = psCheck.executeQuery();

            if (result.next()) {
                if (result.getString("password").equals(password)) {
                    ps = connection.
                            prepareStatement("update users set password = ? where id = ?");

                    ps.setString(1, newPassword);
                    ps.setString(2, id);

                    ps.execute();
                    return "1";
                } else return "2";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String register(String email, String password, String name) {
        PreparedStatement psSignUp = null;
        try {
            psSignUp = connection.
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

    private boolean checkEmail(@RequestParam("email") String email) {
        PreparedStatement psCheck;
        try {
            psCheck = connection.
                    prepareStatement("select * from users where email = ?");

            psCheck.setString(1, email);

            ResultSet resultCheck = psCheck.executeQuery();

            if (!resultCheck.next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String login(String email, String password) {
        PreparedStatement ps;
        try {
            ps = connection.
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
                user.setTests(resultSet.getString("tests"));
                user.setAchievements(resultSet.getString("achievements"));
            } else return "3";

            Gson gson = new Gson();
            return gson.toJson(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

}

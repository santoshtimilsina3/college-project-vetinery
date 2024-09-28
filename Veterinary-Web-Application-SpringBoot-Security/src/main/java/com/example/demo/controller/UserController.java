package com.example.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.enums.Citys;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.ApiPaths;

@Controller
@RequestMapping(ApiPaths.UserBasicCtrl.CTRL)
public class UserController {

    public static String uploadDirectory =
            System.getProperty("user.dir") + "/src/main/resources/static/img/";

    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        super();
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/show-user/{email}/", method = RequestMethod.GET)
    public String UserShowPanel(@PathVariable String email, Map<String, Object> map)
            throws SQLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("title", "Doctor Details");
        List<User> users = userRepository.getUserByEmail(email);
        if (users.size() > 0) {
            if (auth.getName().equals(users.get(0).getEmail())) {
                map.put("user", users.get(0));
            } else {
                map.put("message", email + " email address does not belong to you.");
            }
        } else {
            map.put("message", "No records found for the email address: " + email + ".");
        }
        return "user/user-details";
    }

    @RequestMapping(value = "/update-user/{email}/", method = RequestMethod.GET)
    public String UserUpdatePanel(@PathVariable String email, Map<String, Object> map)
            throws SQLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("title", "ABC VET");

        List<User> users = userRepository.getUserByEmail(email);
        if (users.size() > 0) {
            if (auth.getName().equals(users.get(0).getEmail())) {
                map.put("user", users.get(0));
                map.put("citys", new ArrayList<Citys>(Arrays.asList(Citys.values())));
                return "user/user-update-panel";
            } else {
                map.put("message", email + " email address does not belong to you.");
            }
        } else {
            map.put("message", "No records found for the email address: " + email + ".");
        }
        return "user/user-details";
    }

    @RequestMapping(value = "/update-user/", method = RequestMethod.POST)
    public String UserUpdate(
            @Valid @ModelAttribute("user") User user, BindingResult result, Map<String, Object> map)
            throws SQLException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("title", "Doctor Record Update Page");

        // Fetch the existing user from the database using the authenticated user's email.
        List<User> users = userRepository.getUserByEmail(auth.getName());
        if (users.size() > 0 && auth.getName().equals(users.get(0).getEmail())) {

            if (result.hasErrors()) {
                map.put("message", "Record update failed.");
                return "user/user-details";
            }

            // Retrieve the existing user entity from the database.
            User existingUser = users.get(0);

            // Conditionally update only the fields that are provided (not null and not empty).
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                existingUser.setUsername(user.getUsername());
            }

            if (user.getFirstname() != null && !user.getFirstname().isEmpty()) {
                existingUser.setFirstname(user.getFirstname());
            }

            if (user.getLastname() != null && !user.getLastname().isEmpty()) {
                existingUser.setLastname(user.getLastname());
            }

            if (user.getGender() != null && !user.getGender().isEmpty()) {
                existingUser.setGender(user.getGender());
            }

            if (user.getCity() != null) {
                existingUser.setCity(user.getCity());
            }

            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                existingUser.setEmail(user.getEmail());
            }

            // Retain the existing sensitive fields.
            existingUser.setReel_password(existingUser.getReel_password());
            existingUser.setPassword(existingUser.getPassword());

            // Save the updated user to the repository.
            userRepository.save(existingUser);

            map.put("message", "Record update successful. Please log in again.");
            return "redirect:/logout";
        } else {
            map.put("message", "The email address " + users.get(0).getEmail() + " does not belong to you.");
        }

        return "user/user-details";
    }




    @RequestMapping(value = "/update-user-password/{email}/", method = RequestMethod.GET)
    public String UserPasswordUpdatePanel(@PathVariable String email, Map<String, Object> map)
            throws SQLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("title", "Doctor Details");
        List<User> users = userRepository.getUserByEmail(email);
        if (users.size() > 0) {
            if (auth.getName().equals(users.get(0).getEmail())) {
                map.put("message", "Welcome, " + email + ".");
                map.put("user", users.get(0));
                return "user/user-password-update-panel";
            } else {
                map.put("message", email + " email address does not belong to you.");
            }
        } else {
            map.put("message", "No records found for the email address: " + email + ".");
        }
        return "user/user-details";
    }

    @RequestMapping(value = "/update-user-password", method = RequestMethod.POST)
    public String UserPasswordUpdate(Map<String, Object> map, WebRequest request)
            throws SQLException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        List<User> users = userRepository.getUserByEmail(auth.getName());
        map.put("user", users.get(0));
        map.put("adminname", auth.getName());
        map.put("title", "Doctor Details");
        if (request.getParameter("oldpassword1").equals(request.getParameter("oldpassword2"))) {
            boolean control =
                    passwordEncoder.matches(
                            request.getParameter("oldpassword1"), users.get(0).getPassword());
            if (control) {
                // changing password
                users.get(0).setReel_password(request.getParameter("password"));
                users.get(0).setPassword(passwordEncoder.encode(request.getParameter("password")));
                userRepository.save(users.get(0));
                map.put("message", "Your password has been successfully updated.");
                return "redirect:/logout";
            } else {
                map.put("message", "Incorrect current password.");
            }
        } else {
            map.put("message", "The current passwords do not match.");
        }
        return "user/user-details";
    }

    @RequestMapping(value = "/upload-image/{email}/", method = RequestMethod.POST)
    public String upload(
            @PathVariable String email,
            Map<String, Object> map,
            @RequestParam("files") MultipartFile[] files) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("title", "Doctor Details");
        List<User> users = userRepository.getUserByEmail(email);
        if (users.size() > 0) {
            if (auth.getName().equals(users.get(0).getEmail())) {
                // upload image
                StringBuilder fileNames = new StringBuilder();
                for (MultipartFile file : files) {
                    Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
                    fileNames.append(file.getOriginalFilename() + " ");
                    users.get(0).setImage(fileNames.toString());
                    userRepository.save(users.get(0));
                    try {
                        Files.write(fileNameAndPath, file.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                map.put("message", "Welcome, " + email + ".");
                map.put("message_2", "Your image has been successfully uploaded.");
                map.put("user", users.get(0));
            } else {
                map.put("message", email + " email address does not belong to you.");
            }
        } else {
            map.put("message", "No records found for the email address: " + email + ".");
        }
        return "user/user-details";
    }
}

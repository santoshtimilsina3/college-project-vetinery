package com.example.demo.controller;

import com.example.demo.enums.Citys;
import com.example.demo.model.ClusterData;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.PetService;
import com.example.demo.util.AdoptionStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.*;

@Controller
public class MainController {
    public static String uploadDirectory =
            System.getProperty("user.dir") + "/src/main/resources/static/uploads";

    @Autowired private UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    @Autowired private PetService petService;

    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @Autowired private AppointmentService appointmentService;

    @RequestMapping("/")
    public String index(Map<String, Object> map) {
        if (!SecurityContextHolder.getContext()
                .getAuthentication()
                .getName()
                .equals("anonymousUser")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            map.put("adminname", auth.getName());
        }
        map.put("title", "Veterinary Homepage");
        return "index";
    }

    @RequestMapping("/index")
    public String index2(Map<String, Object> map) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            map.put("adminname", auth.getName());
        }
        map.put("title", "Veterinary Homepage");
        return "index";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerPage(Model model, Map<String, Object> map) {
        List<Role> roles = roleRepository.findAll();
        map.put("title", "Doctor Registration Page");
        map.put("roles", roles);
        map.put("citys", new ArrayList<Citys>(Arrays.asList(Citys.values())));
        model.addAttribute("user", new User());
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String saveRegisterPage(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            Model model,
            Map<String, Object> map,
            HttpServletRequest request)
            throws SQLException {

        map.put("title", "Doctor Registration Page");
        model.addAttribute("user", user);
        List<User> user_control = userRepository.getUserByEmail(user.getEmail());
        // to control whether there is user with this email
        if (user_control.size() > 0) {
            map.put("message", "This email address already exists...");

        } else {
            if (result.hasErrors()) {
                map.put("message", "An error occurred...");
                return "register";
            } else {
                int id = Integer.valueOf(request.getParameter("roles"));
                Role role = roleRepository.findById(id).get();
                user.setRoles(
                        new HashSet<Role>() {
                            {
                                add(role);
                            }
                        });
                user.setReel_password(user.getPassword());
                String pwd = user.getPassword();
                String encryptPwd = passwordEncoder.encode(pwd);
                user.setPassword(encryptPwd);
                map.put("message", "Registration process successful.");
                userRepository.save(user);
            }
        }

        return "register";
    }

    @RequestMapping("/login")
    public String login(Map<String, Object> map) {
        map.put("title", "Doctor Login Page");
        return "login";
    }

    @RequestMapping("/admin-panel")
    public String secure(Map<String, Object> map) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        map.put("adminname", auth.getName());
        map.put("title", "Management Panel");
        return "admin-panel";
    }

    @RequestMapping("/stat-panel")
    public String showStats(Map<String, Object> map) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "stat-panel";
    }

    @ResponseBody
    @RequestMapping("/index/data/{data}/")
    public String loginsss(@PathVariable String data, Map<String, Object> map) {

        return "data : " + data;
    }

    @GetMapping("/api/appointment/clusters")
    @ResponseBody
    public String getCluster() {
        List<ClusterData> clusterData = appointmentService.calcualteClustering();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(clusterData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "Error";
        }
    }

    @RequestMapping(value = "/api/pet/{petId}/changeAdoptionStatus", method = RequestMethod.GET)
    public ResponseEntity<String> changeAdoptionStatus(
            @PathVariable Long petId, @RequestParam String status) {
        try {
            // Update the adoption status using the pet service
            petService.updateAdoptionStatus(petId, AdoptionStatus.valueOf(status));
            return ResponseEntity.ok("Adoption status updated successfully.");
        } catch (IllegalArgumentException e) {
            // Handle the case where the status is invalid
            return ResponseEntity.badRequest().body("Invalid adoption status provided.");
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating adoption status.");
        }
    }
}

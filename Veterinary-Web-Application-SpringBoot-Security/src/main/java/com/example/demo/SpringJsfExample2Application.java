package com.example.demo;

import com.example.demo.controller.MainController;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
public class SpringJsfExample2Application implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        new File(MainController.uploadDirectory).mkdir();
        SpringApplication.run(SpringJsfExample2Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Fetch all roles and users
        List<Role> allRoles = roleRepository.findAll();
        List<User> allUsers = userRepository.findAll();

        // Check if roles are already present, if not, initialize them
        if (allRoles.isEmpty()) {
            Role role1 = new Role();
            role1.setRole("ADMIN");

            Role role2 = new Role();
            role2.setRole("USER");

            Role role3 = new Role();
            role3.setRole("SUPERADMIN");

            // Save the roles in the database
            roleRepository.save(role1);
            roleRepository.save(role2);
            roleRepository.saveAndFlush(role3);
        }
//saveUser();
        // Check if users are already present, if not, initialize a default user

    }

    private void saveUser() {
        List<User> allUsers = userRepository.findAll();

        if (allUsers.isEmpty()) {
            User user = new User();
            user.setFirstname("Santosh");
            user.setLastname("Timilsina");
            user.setEmail("santos.timil@gmail.com");
            user.setPassword("1234");

            // Check if the user with the email already exists
            List<User> existingUser = userRepository.getUserByEmail(user.getEmail());

            if (existingUser.isEmpty()) {
                // Fetch role "SUPERADMIN" by ID to ensure it's a managed entity
                Optional<Role> superAdminRoleOptional = roleRepository.findById(3);

                if (superAdminRoleOptional.isPresent()) {
                    Role superAdminRole = superAdminRoleOptional.get();

                    // Assign role to user
                    Set<Role> roles = new HashSet<>();
                    roles.add(superAdminRole);
                    user.setRoles(roles);

                    // Store the plain text password
                    user.setReel_password(user.getPassword());

                    // Encrypt the user's password
                    String encryptedPassword = passwordEncoder.encode(user.getPassword());
                    user.setPassword(encryptedPassword);

                    // Save the user in the database
                    userRepository.save(user);
                }
            }
        }
    }
}

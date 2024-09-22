package com.example.demo.model;

import java.util.Set;
import javax.persistence.*;

import com.example.demo.enums.Citys;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int user_id;

    private String username;
    private String firstname;
    private String lastname;
    private String gender;
    private String password;
    private String reel_password;
    private String image;

    @Column(name = "city")
    @Enumerated(EnumType.STRING)
    private Citys city;

    @Column(name = "email", unique = true)
    private String email;

    // Many-to-Many relationship for roles
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}

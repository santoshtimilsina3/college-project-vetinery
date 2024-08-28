package com.example.demo.model;

import com.example.demo.enums.Animals;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_preferences")
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "preference_id")
    private Long preferenceId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_animal_type")
    private Animals preferredAnimalType;

    @Column(name = "preferred_age")
    private Long preferredAge;

    @Column(name = "preferred_gender")
    private String preferredGender;

    
    // Add other preference fields as necessary

    // Getters and Setters
}

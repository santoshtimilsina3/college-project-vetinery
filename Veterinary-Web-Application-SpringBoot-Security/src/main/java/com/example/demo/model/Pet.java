package com.example.demo.model;

import javax.persistence.*;

import com.example.demo.enums.Animals;
import com.example.demo.util.AdoptionStatus;
import com.example.demo.util.Size;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

@Entity
@Table(name = "pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "animal_type")
    @Enumerated(EnumType.STRING)
    private Animals type;

    @Column(name = "name")
    private String name;

    @Column(name = "problem")
    private String problem;

    @Column(name = "age")
    private Long age;

    @Enumerated(EnumType.STRING)
    @Column(name = "adoption_status")
    private AdoptionStatus adoptionStatus;

    @Column(name = "size")
    @Enumerated(EnumType.STRING)
    private Size size;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerid")
    public Customer customer;

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    @Column(name = "breed")
    private String breed;

    public AdoptionStatus getAdoptionStatus() {
        return adoptionStatus;
    }

    public void setAdoptionStatus(AdoptionStatus adoptionStatus) {
        this.adoptionStatus = adoptionStatus;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }



    public Pet() {
        super();
    }

    public Pet(Animals type, String name, String problem, Customer customer) {
        super();
        this.type = type;
        this.name = name;
        this.problem = problem;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Animals getType() {
        return type;
    }

    public void setType(Animals type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}

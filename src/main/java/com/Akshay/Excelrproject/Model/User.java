package com.Akshay.Excelrproject.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    private String password;
    private String ssn;
    private Boolean emailVerified;
    private String verificationToken;

    private String resetPasswordToken;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "current_address_id")
    private Address currentAddress;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "permanent_address_id")
    private Address permanentAddress;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Certification> certifications = new ArrayList<>();



    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EducationalHistory> educationalHistory = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<EmploymentHistory> employmentHistory = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Skill> skills = new ArrayList<>();

}

    // Getters and setters}
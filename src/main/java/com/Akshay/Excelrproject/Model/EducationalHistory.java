package com.Akshay.Excelrproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Data
@Entity
public class EducationalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String schoolName;
    private String gpa;
    private String courseName;
    private LocalDate courseCompletionDate;

    @OneToOne(cascade = CascadeType.PERSIST) // Automatically persist the address
    @JoinColumn(name = "address_id")
    private Address address;


        @ManyToOne
        @JoinColumn(name = "user_id")
        @JsonIgnore
        private User user;


// Getters and setters
}

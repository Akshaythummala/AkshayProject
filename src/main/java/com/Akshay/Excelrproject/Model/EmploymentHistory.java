package com.Akshay.Excelrproject.Model;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Data
@Entity
public class EmploymentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String companyAddress;
    private String companyPhone;
    private String principalDuties;
    private LocalDate startDate;  // Assuming you have start and end date fields
    private LocalDate endDate;
    private String salary;
    private String reasonForLeaving;

    @OneToOne(cascade = CascadeType.PERSIST) // Automatically persist the address
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Getters and setters

}

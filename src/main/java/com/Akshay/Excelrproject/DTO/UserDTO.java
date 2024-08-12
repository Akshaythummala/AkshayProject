package com.Akshay.Excelrproject.DTO;


import com.Akshay.Excelrproject.Model.*;
import lombok.Data;

import java.util.List;

@Data
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String password;
    private String ssn;
    private Boolean emailVerified;
    private String verificationToken;
    private Address currentAddress;
    private Address permanentAddress;
    private List<Certification> certifications;
    private List<EducationalHistory> educationalHistory;
    private List<EmploymentHistory> employmentHistory;
    private List<Skill> skills;
    // Getters and setters
}


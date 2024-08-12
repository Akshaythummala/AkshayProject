package com.Akshay.Excelrproject.DTO;


import lombok.Data;

import java.time.LocalDate;

@Data
public class EducationalHistoryDTO {
    private Long id;
    private String schoolName;
    private String gpa;
    private String courseName;
    private LocalDate courseCompletionDate;
    private AddressDTO address;

    // Getters and setters
}

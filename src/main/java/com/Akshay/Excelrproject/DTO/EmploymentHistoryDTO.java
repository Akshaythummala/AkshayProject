package com.Akshay.Excelrproject.DTO;

import lombok.Data;

import java.time.LocalDate;


@Data
public class EmploymentHistoryDTO {
    public Long id;
    private String companyName;
    private String companyAddress;
    private String companyPhone;
    private String principalDuties;
    private LocalDate startDate;
    private LocalDate endDate;
    private String salary;
    private String reasonForLeaving;

    // Getters and setters
}

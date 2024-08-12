package com.Akshay.Excelrproject.Repository;


import com.Akshay.Excelrproject.Model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
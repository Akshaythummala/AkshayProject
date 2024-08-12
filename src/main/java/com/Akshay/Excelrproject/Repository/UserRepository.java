package com.Akshay.Excelrproject.Repository;


import com.Akshay.Excelrproject.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByVerificationToken(String token);
    User findByResetPasswordToken(String token);
    User findByPhoneNumber(String phoneNumber);
}



package com.Akshay.Excelrproject.Controller;

import com.Akshay.Excelrproject.DTO.*;
import com.Akshay.Excelrproject.Exceptions.ResourceNotFoundException;
import com.Akshay.Excelrproject.Model.User;
import com.Akshay.Excelrproject.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;




    // Register a new user
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDTO userDTO) {
        try {
            userService.registerUser(userDTO);
            return new ResponseEntity<>("User registered successfully. Please check your email for verification.", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // sign in existing user
    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody SignInDTO signInDTO) {
        try {
            String response = userService.signIn(signInDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }



    // Verify user's email using the token
    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token) {
        userService.verifyEmail(token);
        return "Email verified successfully!";
    }

    // Add user details (like address, certifications, etc.)
    @PostMapping("/addDetails/{userId}")
    public String addUserDetails(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        userService.addUserDetails(userId, userDTO);
        return "User details added successfully!";

    }

    // Get user details by ID
    @GetMapping("/user/{userId}")
    public User getUserDetails(@PathVariable Long userId) {
        return userService.getUserDetails(userId);
    }

    // Get all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    //Delete user by ID
    @DeleteMapping("/user/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "User deleted successfully!";
    }


    //Update user by ID
    @PutMapping("/UpdateUser/{userId}")
    public String updateUserDetails(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        userService.updateUserDetails(userId, userDTO);
        return "User updated successfully!";
    }


    //resend verification email
    @PostMapping("/resendVerificationEmail/{userId}")
    public String resendverificationemail(@PathVariable Long userId) {
        userService.resendverificationemail(userId);
        return "Verification email sent successfully!";
    }

    //forgot password by email
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        userService.forgotPassword(email);
        return ResponseEntity.ok("Password reset token has been sent to your email.");
    }


    //reset password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token, @RequestParam("password") String password) {
        userService.resetPassword(token, password);
        return ResponseEntity.ok("Password has been reset successfully.");
    }

    //logout user by id
    @PostMapping("/logout/{userId}")
    public ResponseEntity<String> logoutUser(@PathVariable Long userId) {
        userService.logoutUser(userId);
        return ResponseEntity.ok("User logged out successfully.");
    }

    //update address by id
    @PutMapping("/{userId}/address")
    public ResponseEntity<String> updateAddress(@PathVariable Long userId, @RequestBody AddressDTO addressDTO) {
        try {
            userService.updateAddressById(userId, addressDTO);
            return new ResponseEntity<>("Address updated successfully.", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{userId}/educational-history")
    public ResponseEntity<String> updateEducationalHistory(@PathVariable Long userId, @RequestBody EducationalHistoryDTO educationalHistoryDTO) {
        try {
            userService.updateEducationalHistoryById(userId, educationalHistoryDTO);
            return new ResponseEntity<>("Educational History updated successfully.", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //update certification by id
    @PutMapping("/{userId}/certification")
    public ResponseEntity<String> updateCertificationById(@PathVariable Long userId, @RequestBody CertificationDTO certificationDTO) {
        try {
            userService.updateCertificationById(userId, certificationDTO);
            return new ResponseEntity<>("Certification updated successfully!", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }


    //update employment details by id
    @PutMapping("/{userId}/employment-history")
    public ResponseEntity<String> updateEmploymentHistoryById(@PathVariable Long userId, @RequestBody EmploymentHistoryDTO employmentHistoryDTO) {
        try {
            userService.updateEmploymentHistoryById(userId, employmentHistoryDTO);
            return new ResponseEntity<>("Employment History updated successfully!", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    //update skills by id
    @PutMapping("/{userId}/skill")
    public ResponseEntity<String> updateSkillById(@PathVariable Long userId, @RequestBody SkillDTO skillDTO) {
        try {
            userService.updateSkillById(userId, skillDTO);
            return new ResponseEntity<>("Skill updated successfully!", HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }



}
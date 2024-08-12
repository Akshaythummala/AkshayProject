package com.Akshay.Excelrproject.Service;


import com.Akshay.Excelrproject.DTO.*;
import com.Akshay.Excelrproject.Exceptions.EmailVerificationException;
import com.Akshay.Excelrproject.Exceptions.ResourceNotFoundException;
import com.Akshay.Excelrproject.Model.*;
import com.Akshay.Excelrproject.Repository.*;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private EducationalHistoryRepository educationalHistoryRepository;

    @Autowired
    private EmploymentHistoryRepository employmentHistoryRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Transactional
    public void registerUser(UserDTO userDTO) throws Exception {
        // Check if a user with the given email already exists
        User existingUserByEmail = userRepository.findByEmail(userDTO.getEmail());
        if (existingUserByEmail != null) {
            throw new Exception("Email already exists.");
        }

        // Check if a user with the given phone number already exists
        User existingUserByPhoneNumber = userRepository.findByPhoneNumber(userDTO.getPhoneNumber());
        if (existingUserByPhoneNumber != null) {
            throw new Exception("Phone number already exists.");
        }

        // Create a new user
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user.setPassword(userDTO.getPassword());  // No encryption, as requested
        user.setSsn(userDTO.getSsn());

        // Generate a verification token
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setEmailVerified(false);

        // Save the new user
        userRepository.save(user);

        // Send verification email
        sendVerificationEmail(user.getEmail(), token);
    }

@Transactional
public String signIn(SignInDTO signInDTO) {
    String email = signInDTO.getEmail();
    String password = signInDTO.getPassword();

    if (email == null || password == null) {
        throw new IllegalArgumentException("Email and password cannot be null");
    }

    User user = userRepository.findByEmail(email);
    if (user == null) {
        throw new RuntimeException("User not found");
    }

    if (!user.getPassword().equals(password)) {
        throw new RuntimeException("Invalid password");
    }

    if (!user.getEmailVerified()) {
        throw new RuntimeException("Email not verified");
    }

    // Return a success message or token
    return "Sign in successful";
}

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token);

        if (user != null) {
            user.setEmailVerified(true);
            user.setVerificationToken(null);
            userRepository.save(user);
        }
    }

    @Transactional
    public void addUserDetails(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getCurrentAddress() != null) {
            Address currentAddress = userDTO.getCurrentAddress();
            addressRepository.save(currentAddress); // Save the address first
            user.setCurrentAddress(currentAddress);
        }

        if (userDTO.getPermanentAddress() != null) {
            Address permanentAddress = userDTO.getPermanentAddress();
            addressRepository.save(permanentAddress); // Save the address first
            user.setPermanentAddress(permanentAddress);
        }

        // Handle Certifications
        if (userDTO.getCertifications() != null) {
            userDTO.getCertifications().forEach(cert -> {
                if (cert.getId() == null) {
                    cert.setUser(user); // Set the user reference
                } else {
                    Certification existingCert = certificationRepository.findById(cert.getId())
                            .orElseThrow(() -> new RuntimeException("Certification not found"));
                    existingCert.setName(cert.getName());
                    existingCert.setInstitution(cert.getInstitution());
                    existingCert.setProvidedBy(cert.getProvidedBy());
                    existingCert.setDate(cert.getDate());
                }
            });
            certificationRepository.saveAll(userDTO.getCertifications());
            user.setCertifications(userDTO.getCertifications());
        }

        // Handle Educational History
        if (userDTO.getEducationalHistory() != null) {
            userDTO.getEducationalHistory().forEach(eh -> {
                if (eh.getAddress() != null) {
                    addressRepository.save(eh.getAddress()); // Save the address
                }
                if (eh.getId() == null) {
                    eh.setUser(user); // Set the user reference
                } else {
                    EducationalHistory existingEh = educationalHistoryRepository.findById(eh.getId())
                            .orElseThrow(() -> new RuntimeException("Educational History not found"));
                    existingEh.setSchoolName(eh.getSchoolName());
                    existingEh.setGpa(eh.getGpa());
                    existingEh.setCourseName(eh.getCourseName());
                    existingEh.setCourseCompletionDate(eh.getCourseCompletionDate());
                    existingEh.setAddress(eh.getAddress());
                }
            });
            educationalHistoryRepository.saveAll(userDTO.getEducationalHistory());
            user.setEducationalHistory(userDTO.getEducationalHistory());
        }

        // Handle Employment History
        if (userDTO.getEmploymentHistory() != null) {
            userDTO.getEmploymentHistory().forEach(emp -> {
                if (emp.getId() == null) {
                    emp.setUser(user); // Set the user reference
                } else {
                    EmploymentHistory existingEmp = employmentHistoryRepository.findById(emp.getId())
                            .orElseThrow(() -> new RuntimeException("Employment History not found"));
                    existingEmp.setCompanyName(emp.getCompanyName());
                    existingEmp.setCompanyAddress(emp.getCompanyAddress());
                    existingEmp.setCompanyPhone(emp.getCompanyPhone());
                    existingEmp.setPrincipalDuties(emp.getPrincipalDuties());
                    existingEmp.setStartDate(emp.getStartDate());  // Correct method
                    existingEmp.setEndDate(emp.getEndDate());      // Correct method
                    existingEmp.setSalary(emp.getSalary());
                    existingEmp.setReasonForLeaving(emp.getReasonForLeaving());
                }
            });
            employmentHistoryRepository.saveAll(userDTO.getEmploymentHistory());
            user.setEmploymentHistory(userDTO.getEmploymentHistory());
        }

        if (userDTO.getSkills() != null) {
            List<Skill> skills = userDTO.getSkills();
            for (Skill skill : skills) {
                if (skill.getId() == null) {
                    skill.setUser(user); // Set the user reference for new skills
                } else {
                    Skill existingSkill = skillRepository.findById(skill.getId())
                            .orElseThrow(() -> new RuntimeException("Skill not found"));
                    existingSkill.setSkillName(skill.getSkillName());
                    existingSkill.setProficiencyLevel(skill.getProficiencyLevel());
                    // Update other properties if needed
                }
            }
            skillRepository.saveAll(skills); // Save all skills
            user.setSkills(skills); // Update user's skills
        }

        userRepository.save(user);
    }

    public User getUserDetails(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Remove related entities
        user.getCertifications().forEach(certificationRepository::delete);
        user.getEducationalHistory().forEach(educationalHistoryRepository::delete);
        user.getEmploymentHistory().forEach(employmentHistoryRepository::delete);
        user.getSkills().forEach(skillRepository::delete);

        userRepository.delete(user);
    }

    private void sendVerificationEmail(String email, String token) {
        String subject = "Email Verification";
        String verificationUrl = "http://localhost:8080/api/verify?token=" + token;
        String message = "Please verify your email by clicking the link below:\n" + verificationUrl;

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), false);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(message);

            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void updateUserDetails(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        userRepository.save(user);
    }


    @Transactional
    public void resendverificationemail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        userRepository.save(user);

        sendVerificationEmail(user.getEmail(), token);
    }

    //forgot password by email
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }

        String token = UUID.randomUUID().toString();
        user.setResetPasswordToken(token);
        userRepository.save(user);

        sendPasswordResetEmail(user.getEmail(), token);
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String subject = "Password Reset Request";
        String resetUrl = "http://yourdomain.com/reset-password?token=" + token;

        String message = "Click the link below to reset your password:\n" + resetUrl;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }

    //reset password

   @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token);
        if (user == null) {
            throw new ResourceNotFoundException("Invalid token");
        }

        user.setPassword(newPassword);
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    //logout user
    public void logoutUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Perform any necessary logout actions, e.g., clearing the session

        // ...
    }

    //update address by id
    @Transactional
    public void updateAddressById(Long userId, AddressDTO addressDTO) throws ResourceNotFoundException {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Update the user's current address
        Address currentAddress = user.getCurrentAddress();
        if (currentAddress == null) {
            currentAddress = new Address(); // Create a new Address if not present
        }
        currentAddress.setStreet(addressDTO.getStreet());
        currentAddress.setCity(addressDTO.getCity());
        currentAddress.setState(addressDTO.getState());
        currentAddress.setCountry(addressDTO.getCountry());
        currentAddress.setPincode(addressDTO.getPincode());

        // Save the updated address
        user.setCurrentAddress(currentAddress);
        userRepository.save(user);
    }

    //updateEducationalHistoryById
    @Transactional
    public void updateEducationalHistoryById(Long userId, EducationalHistoryDTO educationalHistoryDTO) throws ResourceNotFoundException {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Find the educational history by ID
        EducationalHistory educationalHistory = user.getEducationalHistory().stream()
                .filter(eh -> eh.getId().equals(educationalHistoryDTO.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Educational History not found with ID: " + educationalHistoryDTO.getId()));

        // Convert DTO address to entity address
        Address address = AddressMapper.toAddress(educationalHistoryDTO.getAddress());

        // Update the educational history details
        educationalHistory.setSchoolName(educationalHistoryDTO.getSchoolName());
        educationalHistory.setAddress(address);
        educationalHistory.setGpa(educationalHistoryDTO.getGpa());
        educationalHistory.setCourseName(educationalHistoryDTO.getCourseName());
        educationalHistory.setCourseCompletionDate(educationalHistoryDTO.getCourseCompletionDate());

        // Save the updated user
        userRepository.save(user);

    }
    //update certification by id
    @Transactional
    public void updateCertificationById(Long userId, CertificationDTO certificationDTO) throws ResourceNotFoundException {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Find the certification by ID
        Certification certification = user.getCertifications().stream()
                .filter(c -> c.getId().equals(certificationDTO.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with ID: " + certificationDTO.getId()));

        // Update the certification details
        certification.setName(certificationDTO.getName());
        certification.setInstitution(certificationDTO.getInstitution());
        certification.setProvidedBy(certificationDTO.getProvidedBy());
        certification.setDate(certificationDTO.getDate());

        // Save the updated user
        userRepository.save(user);
    }





    public class AddressMapper {

        public static Address toAddress(AddressDTO addressDTO) {
            if (addressDTO == null) {
                return null;
            }
            Address address = new Address();
            address.setId(addressDTO.getId());
            address.setStreet(addressDTO.getStreet());
            address.setCity(addressDTO.getCity());
            address.setState(addressDTO.getState());
            address.setCountry(addressDTO.getCountry());
            address.setPincode(addressDTO.getPincode());
            return address;
        }
    }

    //update employment details by id
    @Transactional
    public void updateEmploymentHistoryById(Long userId, EmploymentHistoryDTO employmentHistoryDTO) throws ResourceNotFoundException {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Find the employment history by ID
        EmploymentHistory employmentHistory = user.getEmploymentHistory().stream()
                .filter(eh -> eh.getId().equals(employmentHistoryDTO.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Employment History not found with ID: " + employmentHistoryDTO.getId()));

        // Update the employment history details
        employmentHistory.setCompanyName(employmentHistoryDTO.getCompanyName());
        employmentHistory.setCompanyAddress(employmentHistoryDTO.getCompanyAddress());
        employmentHistory.setCompanyPhone(employmentHistoryDTO.getCompanyPhone());
        employmentHistory.setPrincipalDuties(employmentHistoryDTO.getPrincipalDuties());
        employmentHistory.setStartDate(employmentHistoryDTO.getStartDate());
        employmentHistory.setEndDate(employmentHistoryDTO.getEndDate());
        employmentHistory.setSalary(employmentHistoryDTO.getSalary());
        employmentHistory.setReasonForLeaving(employmentHistoryDTO.getReasonForLeaving());

        // Save the updated user
        userRepository.save(user);
    }

    //update skill by id
    @Transactional
    public void updateSkillById(Long userId, SkillDTO skillDTO) throws ResourceNotFoundException {
        // Find the user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Find the skill by ID
        Skill skill = user.getSkills().stream()
                .filter(s -> s.getId().equals(skillDTO.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + skillDTO.getId()));

        // Update the skill details
        skill.setSkillName(skillDTO.getSkillname());
        skill.setProficiencyLevel(skillDTO.getProficiencyLevel());

        // Save the updated user
        userRepository.save(user);
    }
}
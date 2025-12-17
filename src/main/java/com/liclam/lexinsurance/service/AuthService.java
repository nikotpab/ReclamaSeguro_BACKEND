package com.liclam.lexinsurance.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.liclam.lexinsurance.dto.LoginRequest;
import com.liclam.lexinsurance.dto.RegisterRequest;
import com.liclam.lexinsurance.entity.User;
import com.liclam.lexinsurance.repository.UserRepository;

@Service
public class AuthService {

    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService; 

    
    public User registerUser(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        User user = new User();
        user.setName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPhoneNumber(req.getPhone());
        user.setCedula(req.getCedula()); 
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        
        String code = generateCode();
        user.setVerificationCode(code);
        
        userRepo.save(user);

        
        emailService.sendEmail(
            user.getEmail(), 
            "Verifica tu cuenta - ReclamaSeguro", 
            "Tu código de verificación es: " + code
        );

        return user;
    }

    
    public void initiatePasswordRecovery(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("El correo no está registrado"));

        String code = generateCode();
        user.setVerificationCode(code); 
        userRepo.save(user);

        emailService.sendEmail(
            email, 
            "Recuperación de Contraseña", 
            "Usa este código para restablecer tu contraseña: " + code
        );
    }

    
    public void resetPassword(String email, String code, String newPassword) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            throw new RuntimeException("Código inválido o expirado");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setVerificationCode(null); 
        userRepo.save(user);
    }

    
    private String generateCode() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    public void verifyUser(String email, String code) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (user.getVerificationCode() == null) throw new RuntimeException("Usuario ya verificado");

        if (user.getVerificationCode().equals(code)) {
            user.setVerificationCode(null);
            userRepo.save(user);
        } else {
            throw new RuntimeException("Código incorrecto");
        }
    }

    public User login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (user.getVerificationCode() != null) throw new RuntimeException("Cuenta no verificada");
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) throw new RuntimeException("Contraseña incorrecta");
        return user;
    }
}
package com.liclam.lexinsurance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.liclam.lexinsurance.dto.LoginRequest;
import com.liclam.lexinsurance.dto.RegisterRequest;
import com.liclam.lexinsurance.entity.User;
import com.liclam.lexinsurance.repository.UserRepository;
import com.liclam.lexinsurance.util.CryptoService;

@Service
public class AuthService {

    @Autowired private UserRepository userRepo;
    @Autowired private CryptoService cryptoService;
    @Autowired private PasswordEncoder passwordEncoder;

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
        
        return userRepo.save(user);
    }
    public User login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        
        return user;
    }
}
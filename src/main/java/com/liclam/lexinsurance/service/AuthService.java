package com.liclam.lexinsurance.service;

import com.liclam.lexinsurance.dto.RegisterRequest;
import com.liclam.lexinsurance.entity.User;
import com.liclam.lexinsurance.repository.UserRepository;
import com.liclam.lexinsurance.util.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private UserRepository userRepo;
    @Autowired private CryptoService cryptoService;
    @Autowired private PasswordEncoder passwordEncoder;

    public User registerUser(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        
        user.setEncryptedName(cryptoService.encrypt(req.getFullName()));
        user.setEncryptedPhone(cryptoService.encrypt(req.getPhone()));

        return userRepo.save(user);
    }
}
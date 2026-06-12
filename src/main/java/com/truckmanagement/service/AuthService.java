package com.truckmanagement.service;

import com.truckmanagement.dto.AuthDTO.*;
import com.truckmanagement.model.User;
import com.truckmanagement.repository.UserRepository;
import com.truckmanagement.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    @Autowired private AuthenticationManager authManager;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    public TokenResponse login(LoginRequest req) {
        try {
            authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
            String token = jwtUtil.generateToken(req.getUsername());
            return new TokenResponse(token, "Bearer", req.getUsername());
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }

    public void register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        User user = new User(null, req.getUsername(),
                passwordEncoder.encode(req.getPassword()), "USER");
        userRepo.save(user);
    }
}

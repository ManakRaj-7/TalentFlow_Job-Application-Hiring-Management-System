package com.talentflow.service;

import com.talentflow.dto.request.LoginRequest;
import com.talentflow.dto.request.RegisterRequest;
import com.talentflow.dto.response.AuthResponse;
import com.talentflow.entity.User;
import com.talentflow.enums.Role;
import com.talentflow.exception.ValidationException;
import com.talentflow.repository.UserRepository;
import com.talentflow.security.JwtTokenProvider;
import com.talentflow.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(true);

        user = userRepository.save(user);
        logger.info("User registered successfully: {}", user.getEmail());

        UserPrincipal userPrincipal = UserPrincipal.create(user);
        String token = tokenProvider.generateToken(userPrincipal, user.getRole().name());

        return new AuthResponse(token, user.getEmail(), user.getRole(), user.getFullName(), user.getId());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String token = tokenProvider.generateToken(userPrincipal, userPrincipal.getRole().name());

        User user = userRepository.findByEmail(userPrincipal.getUsername())
                .orElseThrow(() -> new ValidationException("User not found"));

        logger.info("User logged in successfully: {}", user.getEmail());

        return new AuthResponse(token, user.getEmail(), user.getRole(), user.getFullName(), user.getId());
    }
}


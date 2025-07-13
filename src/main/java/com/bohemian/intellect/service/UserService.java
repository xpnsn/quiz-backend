package com.bohemian.intellect.service;

import java.util.Collections;
import java.util.List;

import com.bohemian.intellect.dto.LoginRequest;
import com.bohemian.intellect.dto.RegistrationRequest;
import com.bohemian.intellect.dto.UserDto;
import com.bohemian.intellect.exception.UserNotFoundException;
import com.bohemian.intellect.model.User;
import com.bohemian.intellect.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper mapper;

    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtService jwtService, PasswordEncoder passwordEncoder, ObjectMapper mapper) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public ResponseEntity<?> getUserByUsername(String username) {

        User user = userRepository.findByUsername(username);
        if(user == null) throw new UserNotFoundException("No user exist with username : "+username);
        return new ResponseEntity<>(mapper.convertValue(user, UserDto.class), HttpStatus.OK);
    }

    public void deleteUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.deleteById(username);
    }

    public ResponseEntity<String> generateToken(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        User user = userRepository.findByUsername(request.username());
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);
        return new ResponseEntity<>(jwtToken, HttpStatus.OK);

    }

    public ResponseEntity<String> saveUser(RegistrationRequest request) {

        User user = new User(
                null,
                request.username(),
                request.name(),
                passwordEncoder.encode(request.password()),
                request.email(),
                false,
                List.of("USER"),
                Collections.emptyList(),
                Collections.emptyList()
        );

        user = userRepository.save(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    public void addQuizToUser(String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        user.getQuizID().add(id);
        userRepository.save(user);
    }
    public void removeQuizToUser(String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        user.getQuizID().remove(id);
        userRepository.save(user);
    }

    public void addResult(String id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        user.getResultId().add(id);
        userRepository.save(user);
    }

}

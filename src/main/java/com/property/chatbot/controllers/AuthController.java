package com.property.chatbot.controllers;

import com.property.chatbot.dto.LoginRequest;
import com.property.chatbot.dto.LoginResponse;
import com.property.chatbot.utils.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest credentials) {

        UsernamePasswordAuthenticationToken authCredentials = new UsernamePasswordAuthenticationToken(
                credentials.getUsername(), credentials.getPassword());

        authenticationManager.authenticate(authCredentials);

        String jwt = jwtUtil.generateToken(credentials.getUsername());

        return ResponseEntity.ok(new LoginResponse(jwt));
    }
}

package com.bohemian.intellect.filter;

import com.bohemian.intellect.exception.CustomErrorResponse;
import com.bohemian.intellect.service.JwtService;
import com.bohemian.intellect.model.User;
import com.bohemian.intellect.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if(path.endsWith("/login") || path.endsWith("/sign-up") || path.contains("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            handleException(response, "Invalid authHeader");
            return;
        }

        String token = authHeader.substring(7);

        if(jwtService.isTokenExpired(token)) {
            handleException(response, "Token Expired");
            return;
        }

        String username = jwtService.extractUsername(token);

        User user = userRepository.findByUsername(username);

        if(user == null) {
            handleException(response, "User not found");
            return;
        }

        if(!user.isVerified() && !path.contains("/generate-otp") && !path.contains("/validate-otp")) {
            handleException(response, "User is not verified");
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void handleException(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(
                new CustomErrorResponse(
                        "Unauthorized Access",
                        401,
                        LocalDateTime.now().toString(),
                        Map.of("error", message)
                )
        ));
    }

}

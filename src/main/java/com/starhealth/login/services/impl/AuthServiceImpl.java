package com.starhealth.login.services.impl;

import com.starhealth.login.exceptions.AuthenticationAPIException;
import com.starhealth.login.exceptions.InvalidEmailException;
import com.starhealth.login.models.Role;
import com.starhealth.login.models.User;
import com.starhealth.login.payloads.LoginDto;
import com.starhealth.login.payloads.RegisterDto;
import com.starhealth.login.repositories.RoleRepository;
import com.starhealth.login.repositories.UserRepository;
import com.starhealth.login.security.JwtTokenProvider;
import com.starhealth.login.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;


    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String login(LoginDto loginDto) {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsernameOrEmail(), loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateToken(authentication);

            return token;

    }

    @Override
    public String register(RegisterDto registerDto) {

        if(userRepository.existsByUsername(registerDto.getUsername())){
            throw new AuthenticationAPIException(HttpStatus.BAD_REQUEST, "Username is already exists!.");
        }

        if(userRepository.existsByEmail(registerDto.getEmail())){
            throw new AuthenticationAPIException(HttpStatus.BAD_REQUEST, "Email is already exists!.");
        }

        if(registerDto.getEmail() != null && !registerDto.getEmail().contains("@")) {
            throw new InvalidEmailException("Invalid email id");
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        boolean isRoleExist = roleRepository.existsByName(registerDto.getRole());
        if(isRoleExist) {
            Role userRole = roleRepository.findByName(registerDto.getRole()).get();
            roles.add(userRole);
            user.setRoles(roles);
        } else {
            Role userRole = new Role();
            userRole.setName(registerDto.getRole());
            roleRepository.save(userRole);
            roles.add(userRole);
            user.setRoles(roles);
        }
        userRepository.save(user);

        return "User registered successfully!.";
    }
}


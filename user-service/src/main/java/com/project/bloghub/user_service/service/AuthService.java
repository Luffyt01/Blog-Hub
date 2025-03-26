package com.project.bloghub.user_service.service;

import com.project.bloghub.user_service.dto.LoginRequestDto;
import com.project.bloghub.user_service.dto.SignupRequestDto;
import com.project.bloghub.user_service.dto.UserDto;
import com.project.bloghub.user_service.entity.User;
import com.project.bloghub.user_service.exception.BadRequestException;
import com.project.bloghub.user_service.exception.ResourceNotFoundException;
import com.project.bloghub.user_service.repository.UserRepository;
import com.project.bloghub.user_service.utils.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

/*
 * Service to handle the authentication logic
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    // Injecting the necessary dependencies
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;


    /*
     * Function to handle the signup logic
     * First it checks if the user exists or not then proceed with signing up the user
     * Else throws exception
     * It uses BCrypt to hash and check the password securely
     * Then saves the user in the db with hashed password
     */
    public UserDto signUp(SignupRequestDto signupRequestDto) {
        log.info("Attempting to signup the user with email: {}",signupRequestDto.getEmail());

        boolean exists = userRepository.existsByEmail((signupRequestDto.getEmail()));
        if (exists){
            throw new BadRequestException("User already exists, cannot signup again.");
        }

        User user =  modelMapper.map(signupRequestDto, User.class);

        user.setPassword(PasswordUtil.hashPassword(signupRequestDto.getPassword()));

        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    /*
     * Function to handle the login logic
     * It first checks if the user exists or not if user exists then fetch the user
     * Else throw exception that user does not exist
     * Then uses BCrypt to verify the hash password stored in the db
     * Then users JWT to generate a token for the user
     */

    public String login(LoginRequestDto loginRequestDto) {
        log.info("Attempting to login user with email: {}", loginRequestDto.getEmail());

        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: "+ loginRequestDto.getEmail()));

        boolean isPasswordMatch = PasswordUtil.checkPassword(loginRequestDto.getPassword(), user.getPassword());

        if (!isPasswordMatch){
            throw new BadRequestException("Incorrect password");
        }

        return jwtService.generateAccessToken(user);
    }
}

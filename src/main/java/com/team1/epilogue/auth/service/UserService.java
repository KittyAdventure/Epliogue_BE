package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.UserResponse;
import com.team1.epilogue.auth.exception.CustomException;
import com.team1.epilogue.auth.model.User;
import com.team1.epilogue.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse registerUser(RegisterRequest request) {
        if(userRepository.existsByUserId(request.getUserId())) {
            throw new CustomException("이미 등록된 사용자 ID입니다.");
        }
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("이미 등록된 이메일입니다.");
        }

        User user = User.builder()
                .userId(request.getUserId())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .name(request.getName())
                .birthdate(request.getBirthdate())
                .email(request.getEmail())
                .phone(request.getPhone())
                .profilePhoto(request.getProfilePhoto())
                .build();

        User savedUser = userRepository.save(user);

        return UserResponse.builder()
                .id(String.valueOf(savedUser.getId()))
                .userId(savedUser.getUserId())
                .nickname(savedUser.getNickname())
                .name(savedUser.getName())
                .birthdate(savedUser.getBirthdate())
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .profilePhoto(savedUser.getProfilePhoto())
                .build();
    }
}

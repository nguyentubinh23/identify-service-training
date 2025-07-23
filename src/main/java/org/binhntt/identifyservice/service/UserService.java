package org.binhntt.identifyservice.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.binhntt.identifyservice.dto.request.UserCreationRequest;
import org.binhntt.identifyservice.dto.request.UserUpdateRequest;
import org.binhntt.identifyservice.dto.response.UserResponse;
import org.binhntt.identifyservice.entity.User;
import org.binhntt.identifyservice.exception.AppException;
import org.binhntt.identifyservice.exception.ErrorCode;
import org.binhntt.identifyservice.mapper.UserMapper;
import org.binhntt.identifyservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;

    public User createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);
        else {
            User user = userMapper.toUser(request);

            return userRepository.save(user);
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}

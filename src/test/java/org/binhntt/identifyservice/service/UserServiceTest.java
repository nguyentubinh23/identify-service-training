package org.binhntt.identifyservice.service;

import lombok.extern.slf4j.Slf4j;
import org.binhntt.identifyservice.dto.request.UserCreationRequest;
import org.binhntt.identifyservice.dto.request.UserUpdateRequest;
import org.binhntt.identifyservice.dto.response.UserResponse;
import org.binhntt.identifyservice.entity.User;
import org.binhntt.identifyservice.exception.AppException;
import org.binhntt.identifyservice.exception.ErrorCode;
import org.binhntt.identifyservice.mapper.UserMapper;
import org.binhntt.identifyservice.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Slf4j
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserCreationRequest creationRequest;

    private User response;

    private UserResponse userResponse;
    private User user;

    private User mappedUser;

    private UserUpdateRequest updateRequest;

    @Mock
    private PasswordEncoder passwordEncoder;

    // TC-01: createUser

    @Test
    void createUser_validInputUser_success() {
        // GIVEN
        creationRequest = UserCreationRequest.builder()
                .firstName("Alice")
                .lastName("Ha")
                .password("plainPassword")
                .username("binhntt")
                .dob(LocalDate.now())
                .build();


        response = User.builder()
                .id("")
                .firstName("Alice")
                .lastName("Ha")
                .password("plainPassword")
                .username("binhntt")
                .dob(LocalDate.now())
                .build();

        mappedUser = User.builder()
                .id("")
                .firstName("Alice")
                .lastName("Ha")
                .password("plainPassword")
                .username("binhntt")
                .dob(LocalDate.now())
                .build();

        when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(false);
        lenient().when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");
        when(userMapper.toUser(creationRequest)).thenReturn(mappedUser);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(response);


        // WHEN
        User result = userService.createUser(creationRequest);

        // THEN
        Assertions.assertEquals("plainPassword", result.getPassword());
        Assertions.assertEquals("binhntt", result.getUsername());
    }

    // TC-02: createUser
    @Test
    void createUser_duplicateInputUser_fail() {
        // GIVEN
        creationRequest = UserCreationRequest.builder()
                .firstName("Alice")
                .lastName("Ha")
                .password("plainPassword")
                .username("binhntt")
                .dob(LocalDate.now())
                .build();

        when(userRepository.existsByUsername(Mockito.anyString())).thenReturn(true);

        // WHEN
        AppException exception = Assertions.assertThrows(AppException.class, () -> userService.createUser(creationRequest));

        // THEN
        Assertions.assertEquals(ErrorCode.USER_EXISTED, exception.getErrorCode());
    }

    // TC-03: getUsers
    @Test
    void getUsers_success() {
        // GIVEN
        user = User.builder()
                .id("123")
                .firstName("Alice")
                .lastName("Ha")
                .password("encodedPassword")
                .username("binhntt")
                .dob(LocalDate.now())
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user));

        // WHEN
        List<User> users = userService.getUsers();

        // THEN
        Assertions.assertEquals(1, users.size());
        Assertions.assertEquals("binhntt", users.get(0).getUsername());
    }

    // TC-0x: getUser
    @Test
    void getUser_validId_success() {
        user = User.builder()
                .id("123")
                .firstName("Alice")
                .lastName("Ha")
                .password("encodedPassword")
                .username("binhntt")
                .dob(LocalDate.now())
                .build();

        userResponse = UserResponse.builder()
                .id("123")
                .firstName("Alice")
                .lastName("Ha")
                .username("binhntt")
                .dob(LocalDate.now())
                .build();

        when(userRepository.findById("123")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUser("123");

        Assertions.assertEquals("binhntt", result.getUsername());
    }

    // TC-0x: getUser
    @Test
    void getUser_invalidId_throwException() {
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class,
                () -> userService.getUser("999"));

        Assertions.assertEquals("User not found", ex.getMessage());
    }

    // TC-0x: updateUser
    @Test
    void updateUser_validInput_success() {
        // GIVEN
        user = User.builder()
                .id("123")
                .firstName("Alice")
                .lastName("Ha")
                .password("encodedPassword")
                .username("binhntt")
                .dob(LocalDate.now())
                .build();

        updateRequest
                = UserUpdateRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .build();

        UserUpdateRequest updateRequest1
                = UserUpdateRequest.builder()
                .firstName("Updated1")
                .lastName("Name")
                .build();

        userResponse = UserResponse.builder()
                .id("123")
                .firstName("Updated")
                .lastName("Name")
                .username("binhntt")
                .dob(LocalDate.now())
                .build();

        when(userRepository.findById("123")).thenReturn(Optional.of(user));
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setFirstName("Updated");
            u.setLastName("Name");
            return null;
        }).when(userMapper).updateUser(eq(user), eq(updateRequest));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        // WHEN
        UserResponse result = userService.updateUser("123", updateRequest);

        // THEN
        Assertions.assertEquals("binhntt", result.getUsername());
        verify(userRepository).findById("123");
        verify(userMapper).updateUser(any(User.class), eq(updateRequest));
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserResponse(any(User.class));
    }

    // TC-0x: updateUser
    @Test
    void updateUser_invalidId_throwException() {\
        // GIVEN
        when(userRepository.findById("999")).thenReturn(Optional.empty());

        // WHEN
        RuntimeException ex = Assertions.assertThrows(RuntimeException.class,
                () -> userService.updateUser("999", updateRequest));

        // THEN
        Assertions.assertEquals("User not found", ex.getMessage());
    }

    // TC-0x: deleteUser
    @Test
    void deleteUser_success() {
        // WHEN
        userService.deleteUser("123");

        // THEN
        verify(userRepository, times(1)).deleteById("123");
    }

}

package org.binhntt.identifyservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.binhntt.identifyservice.dto.request.UserCreationRequest;
import org.binhntt.identifyservice.entity.User;
import org.binhntt.identifyservice.exception.AppException;
import org.binhntt.identifyservice.exception.ErrorCode;
import org.binhntt.identifyservice.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
@ActiveProfiles("test")
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserCreationRequest request;

    private User response;

    @Test
    void createUser_validInput_success() throws Exception {
        log.info("Hello test");
        // GIVEN => init
        request = UserCreationRequest
                .builder()
                .username("binhntt0")
                .password("123456789")
                .firstName("Binh0")
                .lastName("Nguyen0")
                .dob(LocalDate.of(2003, 4, 14))
                .build();

        response = User
                .builder()
                .id("")
                .username("binhntt")
                .password("123456789")
                .firstName("Binh")
                .lastName("Nguyen")
                .dob(LocalDate.of(2003, 4, 14))
                .build();
        when(userService.createUser(request)).thenReturn(response);

        // WHEN
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        // THEN
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    void createUser_invalidInput_fail() throws Exception {
        // GIVEN
        request = UserCreationRequest
                .builder()
                .username("binhntt1")
                .password("123456")
                .firstName("Binh1")
                .lastName("Nguyen1")
                .dob(LocalDate.of(2003, 4, 14))
                .build();

        when(userService.createUser(request)).thenThrow(
                new RuntimeException()
        );
        // WHEN
        var result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // THEN
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ErrorCode.INVALID_PASSWORD.getCode()));
    }

    @Test
    void createUser_duplicateInput_fail() throws Exception {
        // GIVEN
        request = UserCreationRequest
                .builder()
                .username("binhntt1")
                .password("12345678")
                .firstName("Binh1")
                .lastName("Nguyen1")
                .dob(LocalDate.of(2003, 4, 14))
                .build();

        when(userService.createUser(request)).thenThrow(
                new AppException(ErrorCode.USER_EXISTED)
        );
        // WHEN
        var result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // THEN
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(ErrorCode.USER_EXISTED.getCode()));
    }

}

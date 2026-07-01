package com.utp.recommends.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utp.recommends.auth.controller.AuthController;
import com.utp.recommends.auth.dto.request.ChangePasswordRequest;
import com.utp.recommends.auth.dto.request.LoginRequest;
import com.utp.recommends.auth.dto.request.RegisterRequest;
import com.utp.recommends.auth.dto.response.AuthResponse;
import com.utp.recommends.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private AuthService authService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders
            .standaloneSetup(new AuthController(authService))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Test
    void registerReturnsCreated() throws Exception {
        RegisterRequest request = new RegisterRequest("U12345678@utp.edu.pe", "Password1!", "Juan", "Perez", 1L);
        when(authService.register(any())).thenReturn(new AuthResponse("jwt", "Bearer", 30L, "ESTUDIANTE", 1L, "Juan Perez"));

        mockMvc.perform(post("/api/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").value("jwt"));
    }

    @Test
    void loginReturnsBearerToken() throws Exception {
        LoginRequest request = new LoginRequest("U12345678@utp.edu.pe", "Password1!");
        when(authService.login(any())).thenReturn(new AuthResponse("jwt", "Bearer", 30L, "ESTUDIANTE", 1L, "Juan Perez"));

        mockMvc.perform(post("/api/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void changePasswordReturnsNoContent() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("Password1!", "NewPassword1!");

        mockMvc.perform(put("/api/auth/change-password")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent());
    }
}

package com.utp.recommends.auth.service;

import com.utp.recommends.auth.dto.request.LoginRequest;
import com.utp.recommends.auth.dto.request.RegisterRequest;
import com.utp.recommends.auth.dto.request.ChangePasswordRequest;
import com.utp.recommends.auth.dto.response.AuthResponse;
import com.utp.recommends.auth.dto.response.CurrentUserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    CurrentUserResponse me();
    void changePassword(ChangePasswordRequest request);
}

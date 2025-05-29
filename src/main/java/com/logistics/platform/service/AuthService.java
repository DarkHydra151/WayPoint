package com.logistics.platform.service;

import com.logistics.platform.domain.LoginDTO;
import com.logistics.platform.domain.RegisterDTO;
import com.logistics.platform.domain.UserDTO;

public interface AuthService {
    UserDTO loginUser(LoginDTO request);

    UserDTO registerUser(RegisterDTO request);
    String generateToken(UserDTO user);
}

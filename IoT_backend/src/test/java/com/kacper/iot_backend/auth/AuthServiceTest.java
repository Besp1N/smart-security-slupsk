package com.kacper.iot_backend.auth;

import com.kacper.iot_backend.activation_token.ActivationToken;
import com.kacper.iot_backend.activation_token.ActivationTokenService;
import com.kacper.iot_backend.exception.WrongLoginCredentialsException;
import com.kacper.iot_backend.jwt.JWTService;
import com.kacper.iot_backend.mail.MailService;
import com.kacper.iot_backend.user.User;
import com.kacper.iot_backend.user.UserService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private JWTService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private MailService mailService;

    @Mock
    private UserService userService;

    @Mock
    private ActivationTokenService activationTokenService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserSuccessfully() throws MessagingException {
        AuthRegistrationRequest request = new AuthRegistrationRequest(
                "test@example.com",
                "password",
                "John",
                "Doe"
        );
        User user = new User();
        user.setName("John");
        user.setLast_name("Doe");
        user.setEmail("test@example.com");
        ActivationToken token = new ActivationToken(1,"12345", new Date(), user);

        when(userService.createUser(request)).thenReturn(user);
        when(activationTokenService.createActivationToken(user)).thenReturn(token);

        AuthRegistrationResponse response = authService.register(request);

        verify(userService).saveUserAndToken(user, token);
        verify(mailService).sendVerificationMail(user, "12345");
        assertEquals("John", response.name());
        assertEquals("Doe", response.lastName());
        assertEquals("test@example.com", response.email());
    }

}
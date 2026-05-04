package com.duoc.seguridadcalidad;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private BackendService backendService;

    @Mock
    private JwtCookieService jwtCookieService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthController authController;

    private AuthRequest authRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername("admin");
        authRequest.setPassword("1234");

        authResponse = new AuthResponse("jwt-token");
    }

    @Test
    void shouldLoginSuccessfully() {
        when(backendService.login(authRequest)).thenReturn(authResponse);
        when(jwtCookieService.createAuthCookie("jwt-token"))
                .thenReturn(ResponseCookie.from("jwt", "jwt-token").build());

        var response = authController.createAuthenticationToken(authRequest);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() {
        when(backendService.login(authRequest))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        HttpHeaders.EMPTY,
                        "Credenciales inválidas".getBytes(),
                        null));

        var response = authController.createAuthenticationToken(authRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void shouldReturnServiceUnavailableWhenBackendIsDown() {
        when(backendService.login(authRequest))
                .thenThrow(new ResourceAccessException("Connection refused"));

        var response = authController.createAuthenticationToken(authRequest);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    void shouldLogoutSuccessfully() {
        when(jwtCookieService.clearAuthCookie())
                .thenReturn(ResponseCookie.from("jwt", "").maxAge(0).build());

        var response = authController.logout();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE));
    }

    @Test
    void shouldReturnNoContentWhenSessionIsValid() {
        when(jwtCookieService.extractToken(request)).thenReturn("valid-token");

        var response = authController.session(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void shouldReturnUnauthorizedWhenSessionIsInvalid() {
        when(jwtCookieService.extractToken(request)).thenReturn(null);

        var response = authController.session(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
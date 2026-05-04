package com.duoc.seguridadcalidad;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.ResponseCookie;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtCookieServiceTest {

    private JwtCookieService jwtCookieService;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        jwtCookieService = new JwtCookieService(true, 3600, "Strict");
    }

    @Test
    void shouldCreateAuthCookieCorrectly() {
        ResponseCookie cookie = jwtCookieService.createAuthCookie("test-token");

        assertEquals("AUTH_TOKEN", cookie.getName());
        assertEquals("test-token", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.isSecure());
        assertEquals("/", cookie.getPath());
        assertEquals("Strict", cookie.getSameSite());
        assertEquals(3600, cookie.getMaxAge().getSeconds());
    }

    @Test
    void shouldClearAuthCookieCorrectly() {
        ResponseCookie cookie = jwtCookieService.clearAuthCookie();

        assertEquals("AUTH_TOKEN", cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals(0, cookie.getMaxAge().getSeconds());
    }

    @Test
    void shouldExtractTokenFromAuthorizationHeader() {
        when(request.getHeader("Authorization"))
                .thenReturn("Bearer jwt-token");

        String token = jwtCookieService.extractToken(request);

        assertEquals("jwt-token", token);
    }

    @Test
    void shouldExtractTokenFromCookieWhenHeaderIsAbsent() {
        Cookie cookie = new Cookie("AUTH_TOKEN", "cookie-token");

        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String token = jwtCookieService.extractToken(request);

        assertEquals("cookie-token", token);
    }

    @Test
    void shouldReturnNullWhenNoHeaderOrCookiesExist() {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        String token = jwtCookieService.extractToken(request);

        assertNull(token);
    }

    @Test
    void shouldReturnNullWhenCookieValueIsBlank() {
        Cookie cookie = new Cookie("AUTH_TOKEN", "   ");

        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String token = jwtCookieService.extractToken(request);

        assertNull(token);
    }
}
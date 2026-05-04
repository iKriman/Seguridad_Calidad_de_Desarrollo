package com.duoc.seguridadcalidad;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientRestControllerTest {

    @Mock
    private BackendService backendService;

    @Mock
    private JwtCookieService jwtCookieService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private PatientRestController patientRestController;

    @Test
    void shouldReturnPatientsWhenTokenIsValid() {
        String token = "valid-token";
        List<Map<String, Object>> patients = List.of(
                Map.of("id", 1, "name", "Juan Pérez")
        );

        when(jwtCookieService.extractToken(request)).thenReturn(token);
        when(backendService.getPatients(token)).thenReturn(patients);

        var response = patientRestController.getAll(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patients, response.getBody());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissingForGet() {
        when(jwtCookieService.extractToken(request)).thenReturn(null);

        var response = patientRestController.getAll(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void shouldCreatePatientWhenTokenIsValid() {
        String token = "valid-token";
        Map<String, Object> patient = Map.of(
                "name", "María González",
                "age", 30
        );

        when(jwtCookieService.extractToken(request)).thenReturn(token);
        when(backendService.createPatient(token, patient)).thenReturn(patient);

        var response = patientRestController.create(request, patient);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(patient, response.getBody());
    }

    @Test
    void shouldReturnUnauthorizedWhenTokenIsMissingForPost() {
        Map<String, Object> patient = Map.of("name", "María González");

        when(jwtCookieService.extractToken(request)).thenReturn(null);

        var response = patientRestController.create(request, patient);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
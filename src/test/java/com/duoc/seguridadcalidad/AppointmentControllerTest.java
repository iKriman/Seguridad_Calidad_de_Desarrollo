package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppointmentControllerTest {

    private final AppointmentController controller = new AppointmentController();

    @Test
    void shouldReturnAppointmentsView() {
        String view = controller.listAppointments();

        assertEquals("appointments", view);
    }

    @Test
    void shouldReturnNewAppointmentView() {
        String view = controller.showCreateForm();

        assertEquals("new_appointment", view);
    }

    @Test
    void shouldRedirectAfterSavingAppointment() {
        String view = controller.saveAppointment();

        assertEquals("redirect:/appointments", view);
    }
}
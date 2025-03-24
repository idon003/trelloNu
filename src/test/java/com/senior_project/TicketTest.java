package com.senior_project;

import com.senior_project.accounts.Role;
import com.senior_project.models.Ticket;
import com.senior_project.models.TicketPriority;
import com.senior_project.models.TicketStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.RepeatedTest;

import java.util.UUID;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    private Ticket ticket;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        ticket = Ticket.builder()
                .id(UUID.randomUUID())
                .title("Test Ticket")
                .description("This is a test ticket")
                .status(TicketStatus.PENDING)
                .priority(TicketPriority.MEDIUM)
                .createdBy(userId)
                .assignedRole(Role.STUDENT)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Ticket ID should not be null")
    void testTicketIdNotNull() {
        assertNotNull(ticket.getId());
    }

    @Test
    @DisplayName("Title should not be empty")
    void testTitleNotEmpty() {
        assertFalse(ticket.getTitle().isEmpty());
    }

    @Test
    @DisplayName("Status should be PENDING initially")
    void testInitialStatus() {
        assertEquals(TicketStatus.PENDING, ticket.getStatus());
    }

    @Test
    @DisplayName("Priority should be MEDIUM")
    void testPriority() {
        assertEquals(TicketPriority.MEDIUM, ticket.getPriority());
    }

    @Test
    @DisplayName("AssignedTo can be null initially")
    void testAssignedToCanBeNull() {
        assertNull(ticket.getAssignedTo());
    }

    @Test
    @DisplayName("AssignedRole should be a valid role")
    void testAssignedRole() {
        assertNotNull(ticket.getAssignedRole());
        assertEquals(Role.STUDENT, ticket.getAssignedRole());
    }

    @Test
    @DisplayName("CreatedAt should be before or equal to UpdatedAt")
    void testCreatedBeforeUpdated() {
        assertTrue(ticket.getCreatedAt().isBefore(ticket.getUpdatedAt()) || ticket.getCreatedAt().isEqual(ticket.getUpdatedAt()));
    }

    @Test
    @DisplayName("Changing status should work")
    void testChangeStatus() {
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());
    }

    @Test
    @DisplayName("Updating assignedTo should work")
    void testAssignUser() {
        UUID newUser = UUID.randomUUID();
        ticket.setAssignedTo(newUser);
        assertEquals(newUser, ticket.getAssignedTo());
    }

    @Test
    @DisplayName("Updating timestamp should work")
    void testUpdateTimestamp() {
        LocalDateTime before = ticket.getUpdatedAt();
        ticket.onUpdate();
        assertTrue(ticket.getUpdatedAt().isAfter(before));
    }

    @Test
    @DisplayName("Description should not be null")
    void testDescriptionNotNull() {
        assertNotNull(ticket.getDescription());
    }

    @Test
    @DisplayName("Description should not exceed 500 characters")
    void testDescriptionLength() {
        assertTrue(ticket.getDescription().length() <= 500);
    }

    @ParameterizedTest
    @EnumSource(TicketStatus.class)
    @DisplayName("Ticket should accept all statuses")
    void testTicketStatuses(TicketStatus status) {
        ticket.setStatus(status);
        assertEquals(status, ticket.getStatus());
    }


    @RepeatedTest(5)
    @DisplayName("CreatedAt should be initialized correctly")
    void testCreatedAt() {
        Ticket newTicket = new Ticket();
        newTicket.onCreate();
        assertNotNull(newTicket.getCreatedAt());
    }
}
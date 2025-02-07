package com.senior_project.repository;

import com.senior_project.accounts.Role;
import com.senior_project.models.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByCreatedBy(UUID createdBy);

    List<Ticket> findByAssignedRoleAndStatus(Role assignedRole, TicketStatus status);

    List<Ticket> findByAssignedToAndStatus(UUID assignedTo, TicketStatus status);

    @Query("SELECT t FROM Ticket t WHERE t.createdBy = :userId AND t.status = :status")
    List<Ticket> findByCreatedByAndStatus(@Param("userId") UUID userId, @Param("status") TicketStatus status, Pageable pageable);

}

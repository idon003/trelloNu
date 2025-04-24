package com.senior_project.repository;

import com.senior_project.accounts.Role;
import com.senior_project.models.*;
import org.springframework.data.domain.Page;
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

    @Query("""
                SELECT t FROM Ticket t
                WHERE t.assignedTo = :userId AND t.status = :status
                ORDER BY CASE t.priority
                    WHEN 'HIGH' THEN 1
                    WHEN 'MEDIUM' THEN 2
                    WHEN 'LOW' THEN 3
                    ELSE 4
                END
            """)
    Page<Ticket> findByAssignedToAndStatus(UUID assignedTo, TicketStatus status, Pageable pageable);

    @Query("""
                SELECT t FROM Ticket t
                WHERE t.createdBy = :userId AND t.status = :status
                ORDER BY CASE t.priority
                    WHEN 'HIGH' THEN 1
                    WHEN 'MEDIUM' THEN 2
                    WHEN 'LOW' THEN 3
                    ELSE 4
                END
            """)
    Page<Ticket> findByCreatedByAndStatus(@Param("userId") UUID userId, @Param("status") TicketStatus status, Pageable pageable);

    @Query("""
                SELECT t FROM Ticket t
                WHERE t.assignedRole = :role AND t.status = :status
                ORDER BY CASE t.priority
                    WHEN 'HIGH' THEN 1
                    WHEN 'MEDIUM' THEN 2
                    WHEN 'LOW' THEN 3
                    ELSE 4
                END
            """)
    Page<Ticket> findByRoleAndStatus(@Param("role") Role role, @Param("status") TicketStatus status, Pageable pageable);


    @Query("SELECT t FROM Ticket t WHERE " + "(:status IS NULL OR t.status = :status) AND " + "(:priority IS NULL OR t.priority = :priority) AND " + "(:role IS NULL OR t.assignedRole = :role)")
    Page<Ticket> filterTickets(@Param("status") TicketStatus status, @Param("priority") TicketPriority priority, @Param("role") Role role, Pageable pageable);


}

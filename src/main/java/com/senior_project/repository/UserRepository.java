package com.senior_project.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.senior_project.accounts.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository // Indicates this is a Spring Data repository.
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    void deleteByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);
}


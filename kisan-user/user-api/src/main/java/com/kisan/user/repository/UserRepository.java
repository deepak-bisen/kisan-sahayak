package com.kisan.user.repository;

import com.kisan.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 */
public interface UserRepository extends JpaRepository<User,String> {
    /**
     * Find a user by their unique phone number.
     * @param phoneNumber The user's phone number.
     * @return An Optional containing the User if found.
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Check if a user exists with the given phone number.
     * @param phoneNumber The phone number to check.
     * @return true if exists, false otherwise.
     */
    boolean existsByPhoneNumber(String phoneNumber);

    /**
     * delete if a user exists with the given phone number.
     * @param phoneNumber The phone number to check.
     */
    void deleteByPhoneNumber(String phoneNumber);
}
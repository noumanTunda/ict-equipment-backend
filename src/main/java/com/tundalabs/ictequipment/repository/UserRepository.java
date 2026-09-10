package com.tundalabs.ictequipment.repository;

import com.tundalabs.ictequipment.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Cacheable(value = "users", key = "#employeeId")
    Optional<User> findByEmployeeId(String employeeId);

    Optional<User> findByEmail(String email);

    boolean existsByEmployeeId(String employeeId);

    boolean existsByEmail(String email);

//    @Query("SELECT u FROM User u WHERE u.status = com.tundalabs.ictequipment.entity.User.UserStatus.active AND (LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(u.employeeId) LIKE LOWER(CONCAT('%', :query, '%')))")
//    List<User> searchActiveUsers(@Param("query") String query);

    @Query("SELECT u FROM User u WHERE u.status = com.tundalabs.ictequipment.entity.User.UserStatus.active " +
            "AND (LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.employeeId) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<User> searchActiveUsers(@Param("query") String query);
}

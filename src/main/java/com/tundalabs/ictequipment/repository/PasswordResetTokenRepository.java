package com.tundalabs.ictequipment.repository;

import com.tundalabs.ictequipment.entity.PasswordResetToken;
import com.tundalabs.ictequipment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUser(User user);
    void deleteByUser(User user);
}

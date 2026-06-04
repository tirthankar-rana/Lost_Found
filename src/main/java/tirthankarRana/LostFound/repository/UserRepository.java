package tirthankarRana.LostFound.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tirthankarRana.LostFound.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}

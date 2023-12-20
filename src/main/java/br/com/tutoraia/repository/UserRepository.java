package br.com.tutoraia.repository;

import br.com.tutoraia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    UserDetails findByEmail(String email);

    @Query("select u from User u where u.email = ?1")
    Optional<User> byEmail(String email);
}
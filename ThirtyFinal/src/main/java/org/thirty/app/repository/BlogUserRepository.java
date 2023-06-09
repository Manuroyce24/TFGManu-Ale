package org.thirty.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thirty.app.model.BlogUser;

import java.util.Optional;

public interface BlogUserRepository extends JpaRepository<BlogUser, Long> {

    Optional<BlogUser> findByUsername(String username);

    Optional<BlogUser> findByEmail(String email);

}

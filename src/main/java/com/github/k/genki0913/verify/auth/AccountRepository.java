package com.github.k.genki0913.verify.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);
}

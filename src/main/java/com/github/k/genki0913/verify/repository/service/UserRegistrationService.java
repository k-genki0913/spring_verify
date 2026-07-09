package com.github.k.genki0913.verify.repository.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.k.genki0913.verify.domain.User;
import com.github.k.genki0913.verify.repository.jpa.UserRepository;

@Service
@Transactional
public class UserRegistrationService {

    private UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(User user) {
        return this.userRepository.save(user);
    }
}

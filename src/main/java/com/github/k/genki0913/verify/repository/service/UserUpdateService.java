package com.github.k.genki0913.verify.repository.service;

import org.springframework.stereotype.Service;

import com.github.k.genki0913.verify.domain.User;
import com.github.k.genki0913.verify.repository.exception.UserNotFoundException;
import com.github.k.genki0913.verify.repository.jpa.UserRepository;

@Service
public class UserUpdateService {

    private final UserRepository userRepository;

    public UserUpdateService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("ユーザーが見つかりません"));
    }
}

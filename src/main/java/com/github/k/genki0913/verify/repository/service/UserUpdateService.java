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

    /**
     * 指定されたIDに基づいてユーザー情報を検索します。
     * <p>
     * データベースから該当するユーザーを検索します。
     * ユーザーが存在しない場合は、{@link UserNotFoundException} をスローし、
     * 呼び出し元が適切にエラーハンドリングできる状態にします。
     * </p>
     *
     * @param id
     *               検索対象のユーザーID
     * @return 検索された {@link User} エンティティ
     * @throws UserNotFoundException
     *                                   指定されたIDのユーザーが見つからなかった場合
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("ユーザーが見つかりません"));
    }
}

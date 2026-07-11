package com.github.k.genki0913.verify.repository.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.github.k.genki0913.verify.domain.User;
import com.github.k.genki0913.verify.repository.jpa.UserRepository;

@Service
@Transactional
public class UserRegistrationService {

    private UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * ユーザーの新規登録を行います。
     * <p>
     * 指定された {@link User} エンティティをデータベースへ保存します。
     * </p>
     * <p>
     * <b>トランザクション管理:</b>
     * <ul>
     * <li>本メソッドは {@code @Transactional} によりトランザクション境界として管理されます。</li>
     * <li>処理中に例外が発生した場合は、自動的にロールバックされます。</li>
     * </ul>
     * </p>
     *
     * @param user
     *                 登録対象のユーザーエンティティ
     * @return 保存されたユーザーエンティティ（ID等が自動採番された状態）
     */
    public User register(User user) {

        Assert.isNull(user.getId(), "新規登録時にはIDはnullである必要があります");

        return this.userRepository.save(user);
    }
}

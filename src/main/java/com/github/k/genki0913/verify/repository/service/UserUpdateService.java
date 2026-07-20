package com.github.k.genki0913.verify.repository.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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

    /**
     * 指定されたメールアドレスが自分以外のユーザーによって既に登録されているか判定する。
     * <p>
     * 入力されたメールアドレスでユーザーを検索し、データが存在し、かつそのユーザーのIDが
     * 引数で渡されたID（自分自身）と一致しない場合に {@code true} を返す。
     * </p>
     *
     * @param id
     *                  除外する自身のユーザーID
     * @param email
     *                  重複チェックを行うメールアドレス
     * @return 既に他のユーザーによって登録されている場合は {@code true}、そうでない場合は {@code false}
     */
    public boolean isEmailRegisteredByOther(Long id, String email) {

        Optional<User> user = userRepository.findByEmail(email);

        return user.isPresent() && !user.get().getId().equals(id);
    }

    /**
     * ユーザー情報を更新する。
     * <p>
     * 渡されたユーザーエンティティのIDが {@code null} でないことを検証し、
     * データベースのユーザー情報を更新する。
     * </p>
     *
     * @param user
     *                 更新対象のユーザー情報（IDが設定されている必要がある）
     * @return 更新されたユーザーエンティティ
     * @throws IllegalArgumentException
     *                                      ユーザーのIDが {@code null} の場合
     */
    public User update(User user) {
        Assert.notNull(user.getId(), "更新時にはIDは設定されている必要があります。");

        return this.userRepository.save(user);
    }
}

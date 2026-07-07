package com.github.k.genki0913.verify.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.k.genki0913.verify.domain.User;

/**
 * {@link User} エンティティに対するデータアクセス層のインターフェース。
 * <p>
 * Spring Data JPA の {@link JpaRepository} を継承しており、
 * 以下のような主要な CRUD 操作メソッドが自動的に提供されます。
 * </p>
 * *
 * <h3>主要機能</h3>
 * <ul>
 * <li><b>保存・更新:</b> {@code save(User entity)} - エンティティの保存または更新を行います。</li>
 * <li><b>検索 (全件):</b> {@code findAll()} - 全ユーザーのリストを返します。</li>
 * <li><b>検索 (ID指定):</b> {@code findById(Long id)} - IDに一致するユーザーを検索します (戻り値: {@code Optional<User>})。</li>
 * <li><b>削除 (エンティティ指定):</b> {@code delete(User entity)} - 指定したエンティティを削除します。</li>
 * <li><b>削除 (ID指定):</b> {@code deleteById(Long id)} - ID指定で削除します。</li>
 * <li><b>件数取得:</b> {@code count()} - 登録されている全ユーザー数を取得します。</li>
 * </ul>
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 名前をキーにした部分一致検索を行います。
     * <p>
     * <b>メソッド名のルール:</b>
     * <ul>
     * <li>{@code findBy}: 検索を実行する接頭辞</li>
     * <li>{@code Name}: {@link User} クラスのプロパティ名（カラム名に対応）</li>
     * <li>{@code Containing}: SQLの {@code LIKE %?%} に相当するキーワード</li>
     * </ul>
     * </p>
     * <p>
     * <b>挙動の変化例:</b>
     * <ul>
     * <li>{@code findByNameStartingWith}: 前方一致 ({@code LIKE ?%})</li>
     * <li>{@code findByNameEndingWith}: 後方一致 ({@code LIKE %?})</li>
     * <li>{@code findByName}: 完全一致</li>
     * </ul>
     * </p>
     *
     * @param name
     *                 検索対象の名前の一部
     * @return 検索条件に一致するユーザーのリスト
     */
    List<User> findByNameContaining(String name);

    boolean existsByEmail(String email);
}

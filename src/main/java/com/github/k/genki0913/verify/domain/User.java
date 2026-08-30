package com.github.k.genki0913.verify.domain;

import com.github.k.genki0913.verify.repository.form.UserRegistForm;
import com.github.k.genki0913.verify.repository.form.UserUpdateForm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ユーザー情報を管理するドメインモデル
 * <p>
 * 本クラスはJPAのエンティティとして定義されており、
 * データベースの {@code users} テーブルとマッピングしている
 * </p>
 */
@Entity
@Table(name = "users")
@SuppressWarnings("PMD.DataClass")
public class User {

    /**
     * ユーザーを一意に識別するID。
     * <p>
     * {@link GenerationType#IDENTITY} を指定しており、
     * データベース側の自動採番機能（オートインクリメント）によって値が生成
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ユーザー名。
     * NULL不可、最大長は50文字
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * メールアドレス。
     * NULL不可、一意制約（ユニーク）が付与されており、最大長は100文字
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * デフォルトコンストラクタ。
     * JPAがインスタンスを生成するために必要。
     * 別のコンストラクタを定義しても良いがデフォルトコンストラクタは必ず定義しなければいけない
     */
    public User() {
    }

    public User(UserRegistForm form) {
        this.name = form.name();
        this.email = form.email();
    }

    public User(UserUpdateForm form) {
        this.id = form.getId();
        this.name = form.getName();
        this.email = form.getEmail();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

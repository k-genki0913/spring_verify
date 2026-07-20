package com.github.k.genki0913.verify.repository.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 自身（ID）を除外したメールアドレスの重複チェックを行うカスタムバリデーションアノテーション。
 * <p>
 * クラスレベル（{@link ElementType#TYPE}）に付与して使用する。
 * {@link FieldsMatch} のような可変のフィールド名を受け取る属性（{@code field} など）を持たず、
 * 対象のフォームクラス（例: {@code UserUpdateForm}）の構造を前提として固定でチェックを行うため、
 * アノテーション利用時に属性を指定する必要がないのが特徴である。
 * </p>
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailExceptSelfValidator.class)
@Documented
public @interface UniqueEmailExceptSelf {

    String message() default "{com.github.k.genki0913.verify.common.email.duplicate.except.self}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ ElementType.FIELD })
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        UniqueEmailExceptSelf[] value();
    }
}

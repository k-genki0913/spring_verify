package com.github.k.genki0913.verify.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// このアノテーションの検証ロジックを担当するクラスを指定する
@Constraint(validatedBy = FieldsMatchValidator.class)
// このアノテーションを「クラス単位（TYPE）」に付与できるようにする
@Target({ ElementType.TYPE })
// 本番実行時（RUNTIME）にもこのアノテーションの情報を維持する
@Retention(RetentionPolicy.RUNTIME)
// Javadocに本アノテーションを明記させる
@Documented
// @interface: オリジナルのアノテーションを自作するための専用キーワード
public @interface FieldsMatch {

    // エラー時に表示するメッセージ（プロパティファイルで上書き可能）
    String message() default "{com.github.k.genki0913.verify.common.validation.FieldsMatch.message}";

    // バリデーションのグループ（@GroupSequence用）を指定できるようにする
    Class<?>[] groups() default {};

    // チェックに必要な追加情報をのせるための決まり文句
    Class<? extends Payload>[] payload() default {};

    // 比較したい「2つのフィールド名」を受け取るための属性を定義（カスタムアノテーション専用）
    String field(); // 比較元（例: "email"）

    String fieldToMatch(); // 比較対象（例: "retryEmail"）

    /**
     * 同じ画面で「メールアドレスの一致」と「パスワードの一致」など、
     * 2つ以上のFieldsMatchを連続して定義するための入れ物
     */
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        FieldsMatch[] value();
    }
}

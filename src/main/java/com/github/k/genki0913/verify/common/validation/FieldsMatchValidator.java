package com.github.k.genki0913.verify.common.validation;

import org.springframework.beans.BeanWrapperImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// ConstraintValidator: バリデータを作成するための標準インターフェース
// <A, T> A: どのアノテーション用のバリデータか。 T: どのデータ型をチェックするか(Objectはクラスなども対応。フィールドの場合、Stringも定義可能)
public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {

    private String field;
    private String fieldToMatch;

    /**
     * 自作アノテーション（@FieldsMatch）に指定された属性値の初期化読み込み
     * *
     * <p>
     * 【💡 開発者メモ：アノテーション専用コンストラクタとしての役割】
     * </p>
     * <p>
     * このメソッドは、Spring Boot（Validatorシステム）によって当バリデータが生成された直後、
     * 実際のバリデーション（{@link #isValid}) が動く前の「準備フェーズ」として自動的に呼び出される。
     * </p>
     * <p>
     * 引数の {@link FieldsMatch}（付箋データ）から、Formクラスの上に指定した
     * 比較元のフィールド名（例: "email"）と、比較対象のフィールド名（例: "retryEmail"）を読み取り、
     * インスタンス変数（this.field, this.fieldToMatch）にキープ（記憶）する役割を持つ。
     * </p>
     * <p>
     * ここで記憶したフィールド名の文字列をもとに、次の isValid メソッド内で
     * {@link org.springframework.beans.BeanWrapperImpl} のリフレクションを使い、
     * どんなFormクラスからでも動的に値を取得可能になる、共通バリデータの心臓部。
     * </p>
     *
     * @param constraintAnnotation
     *                                 検証対象のFormクラスに貼られている、自作アノテーションの設定値（付箋データ）
     */
    @Override
    public void initialize(FieldsMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldToMatch = constraintAnnotation.fieldToMatch();
    }

    /**
     * 相関チェック（2つのフィールド値が一致しているか）の動的な検証ロジック
     *
     * <p>
     * 【💡 開発者メモ：リフレクションを用いた動的バリデーションの核心】
     * </p>
     * <p>
     * 引数の {@code value} には、画面の入力値が詰まったFormオブジェクト（例:
     * {@code CorrelationUserRegistForm}）が、
     * どんな画面からでも汎用的に受け取れるよう、最上位の {@link Object} 型に包まれて届く。
     * </p>
     * <p>
     * Object型のままでは {@code .getEmail()} などのメソッドを呼び出せないため、Springが提供する
     * {@link org.springframework.beans.BeanWrapperImpl}で {@code value}
     * をラップする。
     * これにより、{@link #initialize} で記憶したフィールド名（"email" や "retryEmail"）の文字列をキーにして、
     * クラス型を問わずに裏側（リフレクション）から動的に値を取得（{@code getPropertyValue}）している。
     * </p>
     * <p>
     * 最終的に、取得した2つの値を {@link java.util.Objects#equals} を用いて比較する。
     * どちらかが未入力（{@code null}）であっても NullPointerException を起こさず安全に不一致（{@code false}）
     * と判定できる、実務における定石かつ堅牢な実装パターン。
     * </p>
     *
     * @param value
     *                    バリデーション対象のFormオブジェクトそのもの（Object型にアップキャストされた状態）
     * @param context
     *                    バリデーションシステムと通信し、エラーメッセージやエラー項目を細かくカスタマイズするためのリモコン
     * @return 検証結果（2つのフィールド値が完全に一致していれば {@code true}、異なっていれば {@code false}）
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            // Springの便利クラス「BeanWrapperImpl」を使って、Formオブジェクトから値を取り出す
            // FormオブジェクトをBean形式でデータ取得できるようにラップするクラス
            Object fieldValue = new BeanWrapperImpl(value).getPropertyValue(this.field);
            Object fieldToMatchValud = new BeanWrapperImpl(value).getPropertyValue(this.fieldToMatch);

            // 2つの値がどちらもnull（未入力）の場合は、単項目チェック（@NotBlank）に任せるため、ここではtrueにする
            if (fieldValue == null && fieldToMatchValud == null) {
                return true;
            }

            // 2つのフィールドの値を比較した結果を返す（一致していれば true、違えば false）
            return fieldValue != null && fieldValue.equals(fieldToMatchValud);
        } catch (Exception e) {
            return false;
        }
    }
}

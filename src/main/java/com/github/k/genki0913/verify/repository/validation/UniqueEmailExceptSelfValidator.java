package com.github.k.genki0913.verify.repository.validation;

import com.github.k.genki0913.verify.repository.form.UserUpdateForm;
import com.github.k.genki0913.verify.repository.service.UserUpdateService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueEmailExceptSelfValidator implements ConstraintValidator<UniqueEmailExceptSelf, UserUpdateForm> {

    private final UserUpdateService userUpdateService;

    public UniqueEmailExceptSelfValidator(UserUpdateService userUpdateService) {
        this.userUpdateService = userUpdateService;
    }

    /**
     * {@link UniqueEmailExceptSelf} アノテーションによるメールアドレスの重複チェック（相関バリデーション）の検証ロジック。
     *
     * <p>
     * 【💡 開発者メモ：特定フォーム専用バリデータとしての設計上の特徴】
     * </p>
     * <p>
     * クラス宣言の型引数（{@code ConstraintValidator<UniqueEmailExceptSelf, UserUpdateForm>}）が示す通り、
     * 本バリデータは {@link UserUpdateForm} 専用に設計されている。そのため、以下の特徴を持つ：
     * </p>
     * <ul>
     * <li>
     * <b>{@code initialize} メソッドが不要</b>: アノテーションから可変の属性値（フィールド名など）を受け取る必要がないため、
     * オーバーライドによる初期化処理を省略している。
     * </li>
     * <li>
     * <b>{@link org.springframework.beans.BeanWrapperImpl} が不要</b>: 検査対象が {@code UserUpdateForm} に完全に固定されており、
     * メソッドの引数として型安全に直接受け取れるため、リフレクションを用いた動的なプロパティ値の取得を行う必要がない。
     * 代わりに、{@code form.getId()} や {@code form.getEmail()} のように型安全なgetterを直接呼び出して値検証を行っている。
     * </li>
     * </ul>
     * <p>
     * 検証処理では、自身のIDを除外した状態で他のユーザーに同じメールアドレスが登録されていないかを
     * {@link UserUpdateService} を介して確認する。重複が検知された場合は、デフォルトのエラーを無効化した上で、
     * エラーの紐付け先を明示的に {@code email} フィールドに指定し、専用のメッセージキーを返す。
     * </p>
     *
     * @param form
     *                    バリデーション対象の {@link UserUpdateForm} オブジェクト（型安全に直接受け取る）
     * @param context
     *                    バリデーションシステムと通信し、エラーメッセージの紐付け先（プロパティノード）をカスタマイズするためのリモコン
     * @return 検証結果（自分以外に重複するメールアドレスが存在しない場合は {@code true}、存在する場合は {@code false}）
     */
    @Override
    public boolean isValid(UserUpdateForm form, ConstraintValidatorContext context) {

        if (form.getId() == null || form.getEmail() == null) {
            return true;
        }

        boolean isDuplicatedExceptSelf = userUpdateService.isEmailRegisteredByOther(form.getId(), form.getEmail());
        if (isDuplicatedExceptSelf) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{common.email.duplicate.except.self}")
                    .addPropertyNode("email")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

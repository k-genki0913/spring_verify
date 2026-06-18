package com.github.k.genki0913.verify.validation.form;

import com.github.k.genki0913.verify.common.validation.ValidationGroup;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * ユーザー登録・認証時の入力値を格納し、単項目バリデーションを制御するFormクラス（DTO）。
 *
 * <p>
 * <strong>■ バリデーション設計思想（グループシーケンスによる段階評価）</strong><br>
 * 本クラスでは、同一フィールドに複数の制約アノテーションを付与した際、エラーメッセージが画面に
 * 重複して大洪水を起こすのを防ぐため、{@link GroupSequence} を採用して検証の優先順位（フェーズ）を厳格に制御しています。
 * </p>
 *
 * <p>
 * <strong>■ 検証実行のライフサイクル</strong>
 * <ol>
 * <li><b>第1段階：必須チェック（{@link ValidationGroup.Required}）</b><br>
 * 対象：{@code username}, {@code password} の未入力・空文字検知。<br>
 * 理由：未入力の状態で長さや形式チェックを行うと、すべての制約に同時に違反して画面に不要なアドバイスが並び、
 * ユーザーを混乱させる（UXの低下）ため、まずはここで完全に防衛します。途中で1つでもエラーがあれば、以降のフェーズは実行されません。</li>
 * <li><b>第2段階：形式・長さチェック（{@link ValidationGroup.Format}）</b><br>
 * 対象：{@code password} の文字数（{@link Size}）および使用可能文字（{@link Pattern}）。<br>
 * 理由：第1段階をクリアした「何かしらの文字が入っている状態」に対してのみ実行。
 * 「文字数が足りない」かつ「記号が含まれていない」ような状況では、画面に双方のアドバイスが同時に並んだ方が
 * ユーザーの修正を阻害しないため、このフェーズ内ではあえて同時に評価させます。</li>
 * <li><b>第3段階：クラス本来の検証（{@code UserForm.class}）</b><br>
 * 上記すべての単項目チェックを通過したのち、このオブジェクト全体を「安全な入力値」として確定させます。</li>
 * </ol>
 * </p>
 *
 * <p>
 * <strong>■ エラーメッセージの共通規格</strong><br>
 * 各アノテーションの {@code message}
 * 属性の末尾には、共通例外ハンドラー（{@code GlobalHtmlExceptionHandler}）が
 * 項目名を動的に切り出すための目印として {@code ?item.name=項目名} というクエリ文字列形式の独自規格を付与しています。
 * </p>
 */

public class UserForm {

    @GroupSequence({
            ValidationGroup.Required.class,
            ValidationGroup.Format.class
    })
    public @interface ValidationSequence {
    }

    /**
     * 必須入力チェックのみを第一段階として実施します。
     */
    @NotBlank(message = "{common.notItemName.blank}", groups = ValidationGroup.Required.class)
    private String username;

    /**
     * <p>
     * <strong>■ 適用されるバリデーションルール</strong>
     * <ul>
     * <li>{@link NotBlank}（第1段階）：必須入力。空文字・スペースのみを許容しません。</li>
     * <li>{@link Size}（第2段階）：最小8文字、最大16文字の長さ制限。</li>
     * <li>{@link Pattern}（第2段階）：正規表現による複雑さの担保。<br>
     * <b>正規表現解説：</b>{@code "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9-]+$"}<br>
     * 「半角英字」および「半角数字」をそれぞれ最低1文字以上含めることを強制する（先行評価ルックアヘッド）。
     * かつ、使用可能文字を半角英数字に加えて<b>記号のハイフン（{@code -}）のみ</b>に限定します。</li>
     * </ul>
     * </p>
     */
    @NotBlank(message = "{common.notItemName.blank}", groups = ValidationGroup.Required.class)
    @Size(min = 8, max = 16, message = "{common.notItemName.size}", groups = ValidationGroup.Format.class)
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9-]+$", message = "{common.notItemName.pattern}", groups = ValidationGroup.Format.class)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

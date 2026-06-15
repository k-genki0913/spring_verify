package com.github.k.genki0913.verify.validation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.k.genki0913.verify.validation.form.CorrelationUserRegistForm;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/validation/correlation")
public class CorrelationFormController {

    /**
     * 相関バリデーション検証画面の初期表示
     * URL: http://localhost:8080/validation/correlation/form
     *
     * <p>
     * 【💡 開発者メモ：Thymeleafとの協調】
     * </p>
     * <p>
     * 遷移先の画面（correlation-form.html）で {@code th:object} に指定されたFormの受け皿として、
     * 引数で受け取った空のFormオブジェクトを自動的にModelへバインドする。
     * Spring MVCの仕様により、引数にオブジェクトを定義するだけで自動的にModelへ追加されるため、
     * 明示的に Model.addAttribute を書く必要がないクリーンな実装。
     * </p>
     *
     * @param correlationUserRegistForm
     *                                      画面描画のバインディング対象となる空のフォームオブジェクト
     * @return 遷移先のHTML画面パス
     */
    @GetMapping("/form")
    public String showCorrelationForm(CorrelationUserRegistForm correlationUserRegistForm) {
        return "validation/correlation-form";
    }

    /**
     * POSTメソッドによる相関バリデーション（メールアドレス一致）の検証
     * URL: http://localhost:8080/validation/correlation/form
     *
     * <p>
     * 【💡 開発者メモ：クラス型グループ指定によるシーケンス発動】
     * </p>
     * <p>
     * 引数の {@code @Validated} に {@code (CorrelationUserRegistForm.class)}
     * を明示的に指定することで、
     * Formクラス側に定義した {@link jakarta.validation.GroupSequence} が初めて正常に起動する。
     * これにより、「必須（Required）」->「形式（Format）」->「相関（Correlation）」の3段階チェックが完璧に制御される。
     * </p>
     * <p>
     * また、検証成功時は {@code "redirect:"} を用いて自画面のGET URLへリダイレクトさせている。
     * これは実務における二重送信防止の定石である<b>「PRG（Post-Redirect-Get）パターン」</b>に準拠したクリーンな設計。
     * </p>
     *
     * @param form
     *                          3段階のシーケンスバリデーションが実行された後のフォームオブジェクト
     * @param bindingResult
     *                          エラー検証結果を保持するオブジェクト（必ず@Validatedの直後に配置するルール）
     * @return エラー時は入力画面の再描画（フォワード）、成功時は初期表示URLへのリダイレクト
     */
    @PostMapping("/form")
    public String registUser(
            @Validated(CorrelationUserRegistForm.class) @ModelAttribute("correlationUserRegistForm") CorrelationUserRegistForm form,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "validation/correlation-form";
        }

        return "redirect:/validation/correlation/form";
    }

}

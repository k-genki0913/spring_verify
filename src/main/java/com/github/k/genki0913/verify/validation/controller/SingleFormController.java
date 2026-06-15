package com.github.k.genki0913.verify.validation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.github.k.genki0913.verify.validation.form.UserForm;

/**
 * POSTメソッドおよびFormオブジェクトを使用した単項目バリデーション検証用コントローラー
 */
@Controller
@RequestMapping("/validation")
public class SingleFormController {

    /**
     * POSTバリデーション検証画面の初期表示
     * URL: http://localhost:8080/validation/single/form
     * *
     * <p>
     * 【💡 開発者メモ：Thymeleafとの協調】
     * </p>
     * <p>
     * 遷移先の画面（single-form.html）で th:object="${userForm}" を指定しているため、
     * 初期表示の段階で空の Form オブジェクトを Model にバインドしておく必要がある。
     * 画面とコントローラーをGET用と完全に分離したため、GET側の都合に巻き込まれることなく
     * 自身のForm管理だけに集中できているクリーンな状態。
     * </p>
     *
     * @param model
     *                  画面に値を渡すためのModelオブジェクト
     * @return 遷移先のリファレンス画面パス（POST専用画面）
     */
    @GetMapping("/single/form")
    public String showSingleForm(UserForm userForm) {
        return "validation/single-form";
    }

    /**
     * POSTメソッドによるFormオブジェクトのバリデーション検証
     * URL: http://localhost:8080/validation/single/form
     *
     * <p>
     * 【💡 開発者メモ：段階的バリデーション（@GroupSequence）の検証】
     * </p>
     * <p>
     * 引数の UserForm には {@link jakarta.validation.GroupSequence} が設定されており、
     * 「必須チェック（RequiredGroup）」->「相関・形式チェック（既定グループ）」の順に段階検証が行われる。
     * これにより、未入力時に文字数エラーやパターンエラーが重複して表示されるのを防ぎ、
     * 狙った順番でメッセージを切り替える洗練されたUIを実現している。
     * </p>
     * <p>
     * また、エラー時はそのまま自身の入力画面（single-form.html）を返却するだけで、
     * 入力値やBindingResult（th:errors）が自動で引き継がれる、Spring MVCの標準的な定石パターン。
     * </p>
     *
     * @param userForm
     *                          バリデーション対象のフォームオブジェクト（@Validatedによる自動検証）
     * @param bindingResult
     *                          エラー検証結果を保持するオブジェクト（必ず@Validatedの直後に配置するルール）
     * @param model
     *                          画面に値を渡すためのModelオブジェクト
     * @return 遷移先のリファレンス画面パス（エラー時は入力画面の再描画、成功時は結果表示）
     */
    @PostMapping("/single/form")
    public String executeFormSingleValidation(@Validated(UserForm.class) @ModelAttribute("userForm") UserForm userForm,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "validation/single-form";
        }

        model.addAttribute("successMessagePost", "サーバー側でFormオブジェクトを正常に受け取りました!(POST)");
        model.addAttribute("savedUsername", userForm.getUsername());
        model.addAttribute("savedPassword", userForm.getPassword());

        return "validation/single-form";
    }
}

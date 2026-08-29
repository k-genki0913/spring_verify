package com.github.k.genki0913.verify.validation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.k.genki0913.verify.validation.constant.View;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Controller
@RequestMapping("/validation")
@Validated
public class ValidationController {

    /**
     * 単項目チェック画面の初期表示
     * URL: http://localhost:8080/validation/single
     *
     * @return 遷移先のリファレンス画面パス
     */
    @GetMapping("/single")
    public String showSingleValidationForm(Model model) {
        return View.SINGLE;
    }

    /**
     * GETメソッドによるクエリパラメータのバリデーション検証
     * URLの例: /validation/single/query?requiredParam=xxx&lengthParam=yyy
     *
     * <p>
     * 【💡 開発者メモ：@RequestParam(required = false) にしている理由】
     * </p>
     * <p>
     * ここを true (または省略) にすると、パラメータが空の時にValidationエンジンが動く前に
     * Spring MVC側が「MissingServletRequestParameterException (HTTP 400)」を投げてしまい、
     * 画面遷移や自作のエラーメッセージ (@NotBlank(message = "...")) の制御ができなくなるため。
     * </p>
     * <p>
     * あえて false でSpringのチェックをすり抜けさせ、後続の Jakarta Validation に
     * 必須チェックの主導権を握らせるのが実務における複数項目エラーハンドリングの定石。
     * </p>
     *
     * @param requiredParam
     *                          必須チェック項目（空文字は@NotBlankで弾く）
     * @param lengthParam
     *                          長さチェック項目（5文字以内）
     * @param model
     *                          画面に値を渡すためのModelオブジェクト
     * @return 遷移先のリファレンス画面パス
     */
    @GetMapping("/single/query")
    public String executeQuerySingleValidation(
            @RequestParam(required = false) @NotBlank(message = "{common.notBlank}?item.name=必須チェック項目") String requiredParam,
            @RequestParam(required = false) @Size(min = 0, max = 5, message = "{common.size}?item.name=長さチェック項目") String lengthParam,
            Model model) {

        model.addAttribute("successMessage", "サーバー側で正常に値を受け取りました! （DB保存なし）");
        model.addAttribute("savedRequired", requiredParam);
        model.addAttribute("savedLength", lengthParam);
        return View.SINGLE;
    }
}

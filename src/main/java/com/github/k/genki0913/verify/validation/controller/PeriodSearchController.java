package com.github.k.genki0913.verify.validation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.github.k.genki0913.verify.validation.constant.ViewConst;
import com.github.k.genki0913.verify.validation.form.PeriodSearchForm;

/**
 * 期間検索におけるバリデーションを検証するコントローラー。
 * *
 * <p>
 * 主にGETリクエストにおける「型変換（@DateTimeFormat）」と、
 * 自作アノテーションによるクラスレベルの「相関チェック（@ChronologicalPeriod）」の連動挙動を検証する。
 * </p>
 */
@Controller
@RequestMapping("/validation/period")
public class PeriodSearchController {

    /**
     * 期間検索画面の初期表示、および検索実行（バリデーション）を同一URLで受け止める。
     * URL: http://localhost:18080/validation/period/search
     *
     * <p>
     * 【開発者メモ：GETメソッドでの画面・検索の同一化設計】
     * </p>
     * <p>
     * 一般的なデータ登録（POST）では二重送信を防止するためにPRG（Post-Redirect-Get）パターンを用いるが、
     * データの参照（検索など）の文脈においては、冪等性（何回叩いても結果が同じ）が保証されているため、
     * 初期表示と検索実行をこの1つのGETメソッドに集約させる設計がスマートな定石となる。
     * </p>
     * <p>
     * 初回アクセス時（パラメータなし）は、Springが空の {@link PeriodSearchForm} を生成する。
     * バリデーターは「空ならスルー（正常）」として処理するためエラーはチャージされず、クリーンに初期画面が描画される。
     * 検索ボタン押下時（パラメータあり）は、型変換エラー（typeMismatch）や期間逆転エラーが検知され、
     * 戻り値で自画面のビュー（フォワード）を返すことで、エラーメッセージ付きの画面が再表示される仕組み。
     * </p>
     *
     * @param form
     *                          型変換および期間相関バリデーションが実行された後のフォームオブジェクト
     * @param bindingResult
     *                          エラー検証結果を保持するオブジェクト（必ず@Validatedの直後に配置するルール）
     * @param model
     *                          画面へ成功メッセージを渡すためのUIモデル
     * @return 遷移先のHTML画面パス（常に同一の検索画面）
     */
    @GetMapping("/search")
    public String search(@Validated @ModelAttribute("periodSearchForm") PeriodSearchForm form,
            BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return ViewConst.PERIOD_SEARCH_FORM;
        }

        model.addAttribute("successMessage", "日付形式チェック、期間チェックが正常終了しました。");

        return ViewConst.PERIOD_SEARCH_FORM;
    }
}

package com.github.k.genki0913.verify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice(annotations = Controller.class)
public class WebExceptionHandler {

    // =========================================================================
    // 404 Not Found (Spring Boot 3.2+ 最新仕様)
    // =========================================================================
    /**
     * 【全画面共通】リソース未検出エラー（404 Not Found）を一括ハンドリング。
     *
     * <p>
     * <strong>■ 発生契機</strong><br>
     * サーバー内に要求されたURL（マッピング）が存在しない場合、またはCSS・JS・画像などの
     * 静的リソースが物理的に存在しない場合。<br>
     * （※Spring Boot 3.2以降、従来のNoHandlerFoundExceptionから本例外へ一元化されました）
     * </p>
     *
     * <p>
     * <strong>■ 制御概要</strong><br>
     * Spring Boot標準のホワイトレーベルエラーページを上書きし、共通のHTML画面へ遷移させる。<br>
     * ハッカーへの情報漏洩（スタックトレースや内部パス）を防ぐため、技術的な詳細は画面に一切露出させない。
     * </p>
     *
     * <p>
     * <strong>■ 画面（View）への返却データ</strong><br>
     * なし（※関心の分離に基づき、エラータイトル等の文言はModelを介さず、
     * Thymeleaf側が{@code messages.properties}から直接取得する設計をとっています）
     * </p>
     *
     * @param ex
     *               発生したリソース未検出例外。リクエストメソッドや未検出パスの情報を内包する。
     * @return 遷移先テンプレートのパス {@code "error/404"} を含むModelAndView
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNotFound(NoResourceFoundException ex) {
        return new ModelAndView("error/404");
    }

    // =========================================================================
    // 405 Method Not Allowed
    // =========================================================================
    /**
     * 【全画面共通】未許可のHTTPメソッド（405 Method Not Allowed）を一括ハンドリング。
     * *
     * <p>
     * <strong>■ 発生契機</strong><br>
     * エンドポイント（URL）自体は存在するが、設定されていないリクエストメソッドが送られた場合。<br>
     * （例：POSTのみを期待するURLに対して、ブラウザの直リンクやリンク遷移等でGETを送信した等）
     * </p>
     * *
     * <p>
     * <strong>■ 制御概要</strong><br>
     * Spring Boot標準のホワイトレーベル（またはJSON型エラー）を上書きし、共通のHTML画面へ遷移させる。<br>
     * HTTP RFCの仕様に準拠するため、レスポンスに{@code Allow}ヘッダーを明示的に付与する。
     * </p>
     *
     * <p>
     * <strong>■ 画面（View）への返却データ</strong>
     * <ul>
     * <li>{@code requestedMethod}: 拒否されたメソッド（例: "GET"）</li>
     * <li>{@code supportedMethods}: 許可されているメソッドのセット（例: [POST, PUT]）</li>
     * </ul>
     * </p>
     *
     * @param ex
     *                     発生したメソッド不一致例外。許可されているメソッド一覧（HttpHeaders）を内包する。
     * @param response
     *                     サーブレットレスポンス。HTTP仕様準拠のAllowヘッダー注入に使用。
     * @return 遷移先テンプレートのパス {@code "error/405"} を含むModelAndView
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ModelAndView handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
            HttpServletResponse response) {

        if (ex.getHeaders().getAllow() != null && !ex.getHeaders().getAllow().isEmpty()) {
            response.setHeader("Allow", ex.getHeaders().getFirst("Allow"));
        }

        ModelAndView mav = new ModelAndView("error/405");

        mav.addObject("requestedMethod", ex.getMethod());
        mav.addObject("supportedMethods", ex.getSupportedMethods());

        return mav;
    }
}

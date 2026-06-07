package com.github.k.genki0913.verify.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    // =========================================================================
    // 403 Forbidden（セキュリティ対策として404にすり替え）
    // =========================================================================
    /**
     * 【全画面共通】認可エラー（403 Forbidden）を一括ハンドリングし、404画面へ隠蔽遷移させます。
     *
     * <p>
     * <strong>■ 発生契機</strong><br>
     * ログイン中のユーザーが、自身の権限（Role）ではアクセスを許可されていない
     * ページや管理機能（例: 一般ユーザーによるシステム管理者画面へのURL直叩きなど）を要求した場合。
     * </p>
     *
     * <p>
     * <strong>■ 制御概要（【重要】隠蔽によるセキュリティ対策）</strong><br>
     * 本システムでは、セキュリティ（脆弱性）対策の一環として、認可エラー時に本来の
     * 403ステータスや専用画面を返却せず、<strong>あえて「404 Not Found」として偽装処理</strong>します。<br>
     * これにより、悪意ある攻撃者（ハッカー）に対して「そもそもそこに秘匿な管理画面が存在する」
     * という事実そのものを隠蔽（列挙攻撃を防御）します。
     * </p>
     *
     * <p>
     * <strong>■ 画面（View）への返却データ</strong><br>
     * なし（※ユーザーに404と信じ込ませるため、404エラー発生時と全く同じ外観・データ構造を保ちます）
     * </p>
     *
     * @param ex
     *               発生した認可エラー（アクセス拒否例外）
     * @return 遷移先としてあえて404画面のパス {@code "error/404"} を設定したModelAndView
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleForbidden(AccessDeniedException ex) {

        log.warn("【認可エラー】アクセス権限のないリクエストを検知しました。ブラウザには404を返却して隠蔽します。 エラー詳細: {}", ex.getMessage());

        return new ModelAndView("error/404");
    }

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

        log.warn("【リソース未検出エラー】存在しないリソースへのリクエストを検知しました。エラー詳細: {}", ex.getMessage());

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

        log.warn("【未許可メソッドエラー】許可していないメソッドのリクエストを検知しました。エラー詳細: {}", ex.getMessage());

        if (ex.getHeaders().getAllow() != null && !ex.getHeaders().getAllow().isEmpty()) {
            response.setHeader("Allow", ex.getHeaders().getFirst("Allow"));
        }

        ModelAndView mav = new ModelAndView("error/405");

        mav.addObject("requestedMethod", ex.getMethod());
        mav.addObject("supportedMethods", ex.getSupportedMethods());

        return mav;
    }

    // =========================================================================
    // 500 Internal Server Error (予期せぬシステム例外の最終防衛ライン)
    // =========================================================================
    /**
     * 【全画面共通】アプリ内で発生したすべての予期せぬ例外（500 Internal Server Error）を一括ハンドリング。
     * *
     * <p>
     * <strong>■ 発生契機</strong><br>
     * 他の具体的な例外ハンドラー（404, 403, 405等）でキャッチされなかった、すべての未定義の例外が発生した場合。<br>
     * （例：{@link NullPointerException}, {@link IllegalArgumentException},
     * データベース接続エラー、予期せぬシステム障害等）
     * </p>
     * *
     * <p>
     * <strong>■ 制御概要</strong><br>
     * 全例外の基底クラスである{@link Exception}を指定することで、アプリケーション全体の「最後の砦（セーフティネット）」として機能させる。<br>
     * 開発者向けにはバグ修正の命綱となる<b>スタックトレース付きのエラーログ（ERRORレベル）</b>をコンソール・ログファイルに確実に一発出力する。<br>
     * 一方で、一般ユーザーに対しては、Spring
     * Boot標準の無機質なエラー画面を上書きし、一元化されたWeb共通の500システムエラー画面へ安全に誘導する。
     * </p>
     * *
     * <p>
     * <strong>■ セキュリティ上の配慮（重要）</strong><br>
     * 画面（HTML）側には、発生した例外の具体的なメッセージ（{@code ex.getMessage()}）やスタックトレースは<b>絶対に渡さない</b>。<br>
     * これらを画面に露出させると、システムの内部構造（SQL文、パッケージ名、使用ライブラリ等）がハッカーへの脆弱性情報として漏洩するリスクがあるため、<br>
     * 画面側には一貫して「システムエラーが発生しました」という抽象的な文言のみを表示させる設計とする。
     * </p>
     * * @param ex
     * 発生した予期せぬ例外オブジェクト。ログへのスタックトレース出力に使用。
     * 
     * @return 遷移先テンプレートのパス {@code "error/500"} を含むModelAndView
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllException(Exception ex) {
        log.error("【システムエラー】予期せぬ例外が発生しました。");

        return new ModelAndView("error/500");
    }
}

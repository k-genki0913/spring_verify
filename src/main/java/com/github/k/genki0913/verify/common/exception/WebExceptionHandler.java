package com.github.k.genki0913.verify.common.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.github.k.genki0913.verify.config.AppValidationProperties;
import com.github.k.genki0913.verify.repository.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    private final AppValidationProperties validationProperties;
    private final MessageSource messageSource;

    public WebExceptionHandler(AppValidationProperties validationProperties, MessageSource messageSource) {
        this.validationProperties = validationProperties;
        this.messageSource = messageSource;
    }

    // =========================================================================
    // 400 Bad Request（GETパラメータ等の単項目バリデーションエラー）
    // =========================================================================
    /**
     * 【全画面共通】GETメソッド等における引数（クエリパラメータ）のバリデーションエラーを一括ハンドリングし、
     * ユーザーがリクエストを送ってきた元の入力画面へ動的に送還します。
     *
     * <p>
     * <strong>■ 発生契機</strong><br>
     * Controllerのメソッド引数（{@code @RequestParam}）に対して直接 {@code @NotBlank} や
     * {@code @Size} などの
     * 制約アノテーションが配置されている状況において、ブラウザから送信された値がその制約に違反した場合。<br>
     * （※クラスの頭に {@code @Validated} を付与することで起動する、Spring AOPのプロキシ網によって検知・スローされます）
     * </p>
     *
     * <p>
     * <strong>■ 制御概要（動的View解決による汎用送還）</strong><br>
     * 発生したエラーからプロパティファイルに定義されたエラーメッセージを自動抽出し、画面表示用データとして格納します。<br>
     * 遷移先は特定の画面に固定せず、{@link HttpServletRequest} からリクエスト元のURIを動的に解析・取得し、
     * 末尾のアクション（例: {@code /query}）を切り落とすことで、<strong>エラーが発生した「元の入力画面」のテンプレート名（View名）を
     * 自動で導き出して返却</strong>します。これにより、画面ごとにハンドラーを乱立させる必要のない高い汎用性を実現しています。
     * </p>
     *
     * <p>
     * <strong>■ 画面（View）への返却データ</strong><br>
     * <ul>
     * <li>{@code errors}:
     * 発生したすべての制約違反メッセージ（日本語化・動的置換適用済）のリスト（{@code List<String>}）</li>
     * </ul>
     * （※URLパラメータはブラウザ側に保持されたまま画面が再描画されるため、入力値の復元はThymeleafの {@code ${param.xxx}}
     * 側で自動的に行われます）
     * </p>
     *
     * @param ex
     *                    AOPプロキシによってスローされた制約違反例外
     * @param model
     *                    元の画面へエラーメッセージのリストを詰め替えて引き渡すためのUIモデル
     * @param request
     *                    リクエスト元のURLパス（URI）を解析するために使用するサーブレットリクエスト
     * @return 動的に導き出された、エラー発生元のThymeleafテンプレート名（例: {@code "validation/single"}）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public String handlerConstraintViolationException(ConstraintViolationException ex, Model model,
            HttpServletRequest request) {

        List<String> errorMessages = ex.getConstraintViolations().stream().map(violation -> {
            final String placeholder = validationProperties.getPlaceholder();
            final String queryParamItemName = validationProperties.getQueryParamItemName();
            String resolvedMessage = violation.getMessage();
            String template = violation.getMessageTemplate();

            if (!template.contains(queryParamItemName)) {
                String defaultField = messageSource.getMessage("common.default.field", null, "対象項目",
                        request.getLocale());
                return resolvedMessage.replace(placeholder, defaultField);
            }

            int queryParamItemNameIndexInMsg = resolvedMessage.indexOf(queryParamItemName);
            String cleanMessage = resolvedMessage.substring(0, queryParamItemNameIndexInMsg);

            String fieldName = template
                    .substring(template.indexOf(queryParamItemName) + queryParamItemName.length());
            return cleanMessage.replace(placeholder, fieldName);
        }).toList();

        model.addAttribute(validationProperties.getErrorAttributeName(), errorMessages);

        String requestURI = request.getRequestURI();
        String viewName = deriveViewName(requestURI);

        return viewName;
    }

    /**
     * リクエストURIから、戻り先となるThymeleafのテンプレート名（View名）を動的に導き出します。
     *
     * <p>
     * <strong>■ 設計思想（ルーティング規約）</strong><br>
     * 本システムでは、URLのパス構造と {@code src/main/resources/templates} 構造を一致させる規約としています。<br>
     * 本メソッドは、アクセスされたURLの末尾にある「実行アクション（メソッド名に相当するパス）」を削り落とすことで、
     * エラー発生時に送還すべき<strong>「元の入力画面のHTML配置パス」</strong>を自動計算します。
     * </p>
     *
     * <p>
     * <strong>■ 処理仕様（インプットとアウトプットの具体例）</strong><br>
     * 先頭のスラッシュ（{@code /}）を削除したのち、一番最後のスラッシュ以降の文字列（アクション名）を切り落とします。<br>
     * 解析に失敗した場合、またはスラッシュが含まれないトップレベルのパスの場合は、安全のため一律トップ画面（{@code "index"}）を返却します。
     * </p>
     * *
     * <table border="1" style="border-collapse: collapse; padding: 5px;">
     * <tr style="background-color: #f2f2f2;">
     * <th>入力（requestURI）</th>
     * <th>変換プロセス</th>
     * <th>出力（戻り値：View名）</th>
     * </tr>
     * <tr>
     * <td>{@code "/validation/single/query"}</td>
     * <td>先頭の/を削り、末尾の {@code /query} を切り落とす</td>
     * <td><strong>{@code "validation/single"}</strong> （単項目チェック画面）</td>
     * </tr>
     * <tr>
     * <td>{@code "/validation/correlation/submit"}</td>
     * <td>先頭の/を削り、末尾の {@code /submit} を切り落とす</td>
     * <td><strong>{@code "validation/correlation"}</strong> （相関チェック画面）</td>
     * </tr>
     * <tr>
     * <td>{@code "/"} または {@code "/index"}</td>
     * <td>スラッシュによる階層が存在しないため、フォールバックが作動</td>
     * <td><strong>{@code "index"}</strong> （トップ画面）</td>
     * </tr>
     * </table>
     *
     * @param requestURI
     *                       サーバーが受け取ったリクエストURI（例:
     *                       {@code "/validation/single/query"}）
     * @return 導き出されたThymeleafのテンプレート名（末尾の {@code .html} は除外した形式）
     */
    private String deriveViewName(String requestURI) {
        // 先頭のロリポップ（/）を削除
        String path = requestURI.startsWith("/") ? requestURI.substring(1) : requestURI;

        // 末尾のメソッド名（/query や /submit など）を削る
        if (path.contains("/")) {
            int lastSlashIndex = path.lastIndexOf("/");
            return path.substring(0, lastSlashIndex);
        }

        return "index";
    }

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

        if (log.isWarnEnabled()) {
            log.warn("【認可エラー】アクセス権限のないリクエストを検知しました。ブラウザには404を返却して隠蔽します。 エラー詳細: {}", ex.getMessage());
        }
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

        if (log.isWarnEnabled()) {
            log.warn("【リソース未検出エラー】存在しないリソースへのリクエストを検知しました。エラー詳細: {}", ex.getMessage());
        }
        return new ModelAndView("error/404");
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleUserNotFound(UserNotFoundException ex) {
        if (log.isWarnEnabled()) {
            log.warn("【ユーザー未検出】存在しないユーザーへのアクセスが発生しました。エラー詳細: {}", ex.getMessage());
        }
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

        if (log.isWarnEnabled()) {
            log.warn("【未許可メソッドエラー】許可していないメソッドのリクエストを検知しました。エラー詳細: {}", ex.getMessage());
        }

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
        log.error("【システムエラー】予期せぬ例外が発生しました。", ex);

        return new ModelAndView("error/500");
    }
}

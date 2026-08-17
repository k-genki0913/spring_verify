package com.github.k.genki0913.verify.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * カスタムログイン処理を管理するコントローラー。
 * 
 * <p>
 * Spring Securityのデフォルトのフォームログイン機能を使用せず、
 * 自前でログイン画面の表示と認証処理、およびログイン成功後のリダイレクト制御を行います。
 * </p>
 */
@Controller
public class LoginController {

    /**
     * 認証を実際に実行するSpring Securityのコアエンジン。
     */
    private final AuthenticationManager authenticationManager;

    /**
     * 認証情報（SecurityContext）をHTTPセッションに保存・復元するためのリポジトリ。
     * 次のリクエスト以降もログイン状態を維持するために使用します。
     */
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    /**
     * 未認証時にアクセスしようとした元のリクエスト情報をセッションから取得・削除するためのキャッシュ機構。
     */
    private final RequestCache requestCache = new HttpSessionRequestCache();

    /**
     * コントローラーのコンストラクタ。
     * 
     * @param authenticationManager
     *                                  SecurityConfigで公開されたAuthenticationManager
     */
    public LoginController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * ログイン画面を表示します。
     * 
     * @param model
     *                  ビューに渡すデータを保持するオブジェクト
     * @return ログイン画面のテンプレートパス
     */
    @GetMapping("/login")
    public String loginPage(Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }
        return "login/login";
    }

    /**
     * ログインフォームの送信を受け取り、認証処理を実行します。
     * 
     * <p>
     * 以下の手順で処理を行います：
     * </p>
     * <ol>
     * <li>入力値のバリデーションチェック</li>
     * <li>AuthenticationManagerによるパスワード照合・認証</li>
     * <li>成功時：認証情報をスレッド（SecurityContextHolder）およびセッション（SecurityContextRepository）に保存</li>
     * <li>成功時：アクセス元のURLが保存されていればそこへ、なければホーム画面（/）へリダイレクト</li>
     * <li>失敗時：エラーメッセージをモデルに設定し、ログイン画面を再表示</li>
     * </ol>
     *
     * @param loginForm
     *                          ユーザーが入力したログイン情報（バリデーション対象）
     * @param bindingResult
     *                          バリデーション結果
     * @param request
     *                          HTTPリクエスト（セッションへの情報保存やキャッシュ取得に使用）
     * @param response
     *                          HTTPレスポンス
     * @param model
     *                          ビューに渡すデータを保持するオブジェクト
     * @return 認証成功時はリダイレクト先URL、バリデーション・認証エラー時はログイン画面のテンプレートパス
     */
    @PostMapping("/login")
    public String login(@Validated(LoginForm.ValidationSequence.class) LoginForm loginForm, BindingResult bindingResult,
            HttpServletRequest request,
            HttpServletResponse response, Model model) {

        if (bindingResult.hasErrors()) {
            return "login/login";
        }

        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginForm.getEmail(),
                    loginForm.getPassword());

            Authentication authentication = authenticationManager.authenticate(token);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);

            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                String originalRequestUrl = savedRequest.getRedirectUrl();
                requestCache.removeRequest(request, response);

                return "redirect:" + originalRequestUrl;
            }

            return "redirect:/";
        } catch (AuthenticationException e) {
            model.addAttribute("loginError", "メールアドレスまたはパスワードが間違っています。");
            return "login/login";
        }
    }

}

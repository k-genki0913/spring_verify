package com.github.k.genki0913.verify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

/**
 * Spring Securityの全体的なセキュリティ・認証・認可の設定を行うクラス。
 *
 * <p>
 * リクエストに対するフィルタリングルール、パスワードの暗号化方式、
 * および認証マネージャーの公開などを定義します。
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * セキュリティフィルターチェーン（フィルタリングのルールや動作）を定義します。
     *
     * <p>
     * HTTPリクエストに対するアクセス権限、未認証時のリダイレクト先、
     * フォームログインの無効化、ログアウト処理などを構成します。
     * </p>
     *
     * @param http
     *                 HttpSecurityオブジェクト
     * @return 構築された SecurityFilterChain
     * @throws Exception
     *                       設定時の例外
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // AuthorizationFilterのカスタム設定
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated())
                // UsernamePasswordAuthenticationFilterのカスタム設定
                .formLogin(form -> form.disable())
                // ExceptionTranslationFilterのカスタム設定
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
                // LogoutFilterのカスタム設定
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }

    /**
     * 認証処理を自作コントローラーから実行するために、AuthenticationManagerをBeanとして公開します。
     *
     * @param authenticationConfiguration
     *                                        認証設定オブジェクト
     * @return AuthenticationManager インスタンス
     * @throws Exception
     *                       取得時の例外
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * パスワードのハッシュ化および照合を行うPasswordEncoderを定義します。
     *
     * <p>
     * 標準的かつ強力なハッシュ化アルゴリズムであるBCryptを使用します。
     * </p>
     *
     * @return BCryptPasswordEncoder インスタンス
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

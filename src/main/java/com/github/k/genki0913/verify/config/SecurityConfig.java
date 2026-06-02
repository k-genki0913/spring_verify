package com.github.k.genki0913.verify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

// AIの提示したコードを丸コピー（今後変えていく）
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ★全てのURLへのアクセスを、ログインなしで「全員許可（permitAll）」にする設定
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
                // ログイン画面（Formログイン）も一旦無効化
                .formLogin(form -> form.disable());

        return http.build();
    }
}

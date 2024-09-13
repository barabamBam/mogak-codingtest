package com.ormi.mogakcote.security.config;

import com.ormi.mogakcote.security.TokenConstants;
import com.ormi.mogakcote.security.jwt.JwtFilter;
import com.ormi.mogakcote.security.jwt.JwtFilterImpl;
import com.ormi.mogakcote.user.domain.Authority;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.user.password.secretKey}")
    private String secretKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtFilterImpl jwtFilter,
                                           LogoutHandler logoutHandler) throws Exception {
        http.csrf(cnf -> cnf.ignoringRequestMatchers("/api/**"));

        http.logout(cnf -> {
            cnf.logoutUrl("/api/v1/auth/logout");
            cnf.permitAll();
            cnf.addLogoutHandler(logoutHandler);
        });

        http.authorizeHttpRequests(auth -> {

            auth.dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll();
            auth.requestMatchers("/api/*/**").permitAll();
            // 프로필 관련 엔드포인트 추가
            //auth.requestMatchers("/profile/**").authenticated();

            // 회원가입 관련 엔드포인트 허용
            auth.requestMatchers("/api/users/register").permitAll();

            // 메인
            auth.requestMatchers(HttpMethod.GET, "/api/*/posts/list").permitAll();

            // 관리자
//            auth.requestMatchers("/api/*/admin", "/api/*/admin/**").hasRole("ADMIN");
            //auth.requestMatchers("/api/*/admin").hasRole("ADMIN");
            //auth.requestMatchers("/html/admin/**").hasAuthority("ADMIN");

            // 공지사항
            auth.requestMatchers(HttpMethod.GET, "/api/*/notice/*").permitAll();
            //auth.requestMatchers("/api/*/notice/*").hasRole("ADMIN");

            // 엘런 문제 분석
            //auth.requestMatchers("/api/*/reports/**").hasRole("USER");

            // 알림
            //auth.requestMatchers(HttpMethod.GET, "/api/*/news/*").hasRole("USER");
            //auth.requestMatchers("/api/*/news", "/api/*/news/*").hasRole("ADMIN");

            // 댓글
            auth.requestMatchers(HttpMethod.GET, "/api/*/posts/*/comments/**").permitAll();
            //auth.requestMatchers("/api/*/posts/*/comments", "/api/*/posts/*/comments/**").hasRole("USER");

            // 시스템 댓글
            auth.requestMatchers(HttpMethod.GET, "/api/*/posts/*/system-comments").permitAll();

            // 게시글
            auth.requestMatchers(HttpMethod.GET, "/api/*/posts/*").permitAll();
            //auth.requestMatchers("/api/*/posts", "/api/*/posts/*").hasRole("USER");

            // 회원가입
            auth.requestMatchers("/api/*/signup/**", "/api/*/users/**").permitAll();

            // 인증/권한
            auth.requestMatchers("/api/*/auth/**").permitAll();

            // 헬스 체크
            auth.requestMatchers("/health").anonymous();

            // 마이페이지
            //auth.requestMatchers("/api/*/users", "/api/*/users/**").hasRole("USER");

            auth.requestMatchers("/css/**", "/html/**", "/js/**", "/img/**").permitAll(); //정적파일
            //auth.requestMatchers("/css/**","html/header/**","/html/auth/**", "/js/**", "/img/**").permitAll(); //정적파일

            // 나머지 요청은 인증 필요
            auth.anyRequest().authenticated();
        });

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder(
            secretKey,
            16,
            310000,
            Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }
}

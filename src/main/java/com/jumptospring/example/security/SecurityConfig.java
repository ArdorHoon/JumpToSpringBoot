package com.jumptospring.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableMethodSecurity(prePostEnabled = true) //@PreAuthorize를 사용하기 위해 필요한 어노테이션
@Configuration // 스프링의 환경설정 파일임을 의미하는 애너테이션
@EnableWebSecurity //내부적으로 SpringSecurityFilterChain이 동작하여 필터가 적용된다.
public class SecurityConfig {

    /**
     * Spring Security의 세부 설정은 SecurityFilterChain 빈을 생성하여 설정 가능하다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        //모든 인증되지 않은 요청을 허락한다
        httpSecurity.authorizeRequests().antMatchers("/**").permitAll()
                .and()//이어서 http 객체 설정
                .csrf().ignoringAntMatchers("/h2-console/**")
                .and()
                .headers()
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                .and()
                .formLogin()
                .loginPage("/user/login")
                .defaultSuccessUrl("/")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true); //로그아웃 시 생성된 사용자 세션 삭제

        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //AuthenticationManager는 스프링 시큐리티의 인증을 담당, 생성 시 스프링의 내부 동작으로 인해 위에서 작성한 UserSecurityService와 PasswordEncoder가 자동으로 설정
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}

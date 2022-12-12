package com.jumptospring.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

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
                .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN));

        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}

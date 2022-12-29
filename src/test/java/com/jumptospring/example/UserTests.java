package com.jumptospring.example;

import com.jumptospring.example.uesr.SiteUser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
public class UserTests {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    @ParameterizedTest
    @MethodSource("passwords")
    void PasswordEqualTest(String password) {

        SiteUser user = new SiteUser();

        user.setPassword(passwordEncoder.encode("1234"));
        user.setUsername("any");
        user.setEmail("any@any.com");

        Boolean checkPassword = passwordEncoder.matches(password, user.getPassword());

        assertThat(checkPassword).isTrue();
    }

    private static Stream<Arguments> passwords() {
        return Stream.of(
                arguments("1234"),
                arguments("1111"),
                arguments("2222")
        );
    }
}

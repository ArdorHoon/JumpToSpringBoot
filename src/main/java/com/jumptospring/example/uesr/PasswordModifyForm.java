package com.jumptospring.example.uesr;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class PasswordModifyForm {

    @NotEmpty(message = "변경할 비밀번호는 필수항목입니다.")
    private String password1;

    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
    private String password2;

    @NotEmpty(message = "이전 비밀번호는 필수항목입니다.")
    private String beforePassword;

}

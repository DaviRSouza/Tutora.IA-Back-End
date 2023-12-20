package br.com.tutoraia.request;

import lombok.Data;

@Data
public class PasswordRecoverRequest {

    private String password;
    private String confirmPassword;
}

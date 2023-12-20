package br.com.tutoraia.controller;

import br.com.tutoraia.request.LoginRequest;
import br.com.tutoraia.request.UserRequest;
import br.com.tutoraia.response.TokenResponse;
import br.com.tutoraia.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/auth")
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@RequestBody UserRequest userRequest) {
        return userService.signUp(userRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid      LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @GetMapping
    public String teste(){
        return "teste";
    }
}

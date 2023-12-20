package br.com.tutoraia.service;

import br.com.tutoraia.request.LoginRequest;
import br.com.tutoraia.request.PasswordRecoverRequest;
import br.com.tutoraia.request.UserRequest;
import br.com.tutoraia.response.DefaultMessageResponse;
import br.com.tutoraia.response.TokenResponse;
import br.com.tutoraia.exceptions.InvalidParamsException;
import br.com.tutoraia.model.PasswordResetToken;
import br.com.tutoraia.model.User;
import br.com.tutoraia.repository.PasswordResetTokenRepository;
import br.com.tutoraia.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final AuthenticationManager manager;
    private final TokenService tokenService;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public ResponseEntity<Void> signUp(UserRequest userRequest) {
        log.info("Save user: {}", userRequest);
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            log.error("Email already exists");
            throw new InvalidParamsException("Email already exists");
        }
        User user = new User();
        BeanUtils.copyProperties(userRequest, user);
        user.setPassword(encoder.encode(userRequest.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<TokenResponse> login(LoginRequest loginRequest) {
        log.info("Login user: {}", loginRequest);
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        Authentication auth = manager.authenticate(userToken);
        String token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.status(HttpStatus.OK).body(new TokenResponse(token));
    }

    public ResponseEntity<String> recoverPassword(String email) {
        log.info("Recover password for email: {}", email);
        Optional<User> user = userRepository.byEmail(email);
        if (user.isPresent()) {
            log.info("User found: {}", user.get());
            PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                    .token(UUID.randomUUID().toString())
                    .user(user.get())
                    .expiryDate(LocalDateTime.now())
                    .build();
            passwordResetTokenRepository.save(passwordResetToken);
            emailService.sendEmail(user.get(), "tutora-ia.com.br/recuperar-senha/" + passwordResetToken.getToken());
            return ResponseEntity.ok().build();
        }
        log.error("User not found for email: {}", email);
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<DefaultMessageResponse> resetPassword(String token, PasswordRecoverRequest password) {
        log.info("Reset password for token: {}", token);
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken.isPresent() && !passwordResetToken.get().getUsed()) {
            log.info("Token found: {}", passwordResetToken.get());
            LocalDateTime tokenCreationTime = passwordResetToken.get().getExpiryDate();
            LocalDateTime expirationTime = tokenCreationTime.plusHours(24);
            if (expirationTime.isBefore(LocalDateTime.now())) {
                log.error("Token expired");
                return ResponseEntity.badRequest().body(new DefaultMessageResponse("Token expired"));
            }
            if (!password.getPassword().equals(password.getConfirmPassword())) {
                log.error("Password and confirm password not match");
                return ResponseEntity.badRequest().body(new DefaultMessageResponse("Password and confirm password not match"));
            }
            User user = passwordResetToken.get().getUser();
            user.setPassword(encoder.encode(password.getPassword()));
            userRepository.save(user);
            passwordResetToken.get().setUsed(true);
            return ResponseEntity.ok().body(new DefaultMessageResponse("Password updated successfully"));
        }
        log.error("Token not found");
        return ResponseEntity.badRequest().body(new DefaultMessageResponse("Recover password token not found"));
    }
}

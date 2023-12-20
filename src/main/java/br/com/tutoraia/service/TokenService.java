package br.com.tutoraia.service;

import br.com.tutoraia.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class TokenService {

    private String password = "SENHASUPERSECRETA";

    public String getSubject(String tokenJWT){
        try {
            var algorithm = Algorithm.HMAC256(password);
            return JWT.require(algorithm)
                    .withIssuer("Turoiaia")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        }catch (JWTVerificationException e){
            throw new RuntimeException("Token Invalido ou Expirado");
        }
    }
    public String generateToken(User user){
        try {
            var algoritimo = Algorithm.HMAC256(password);
            return JWT.create()
                    .withIssuer("Turoiaia")
                    .withClaim("id", user.getId())
                    .withClaim("name", user.getName())
                    .withClaim("email", user.getEmail())
                    .withClaim("password", user.getPassword())
                    .withExpiresAt(expirationDate())
                    .sign(algoritimo);
        }catch (JWTCreationException e){
            throw new RuntimeException("Erro ao gerar o token jwt", e);
        }
    }

    private Instant expirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
    
}

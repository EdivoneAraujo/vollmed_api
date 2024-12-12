package med.voll.api.Infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import med.voll.api.domain.usuario.Usuario;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration}")
    private int expirationHours;

    /**
     * Gera um token JWT para o usuário fornecido.
     *
     * @param usuario O usuário para o qual o token será gerado.
     * @return O token JWT gerado.
     */
    public String gerarToken(Usuario usuario) {
        try {
            logger.info("Gerando token para o usuário: {}", usuario.getLogin());
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API Voll.med")
                    .withSubject(usuario.getLogin())
                    .withExpiresAt(dataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException exception) {
            logger.error("Erro ao gerar token JWT", exception);
            throw new TokenException("Erro ao gerar token JWT", exception);
        }
    }

    /**
     * Obtém o subject (identificador do usuário) a partir de um token JWT.
     *
     * @param tokenJWT O token JWT fornecido.
     * @return O subject contido no token.
     */
    public String getSubject(String tokenJWT) {
        try {
            logger.info("Validando token JWT...");
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("API Voll.med")
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            logger.error("Token JWT inválido ou expirado!", exception);
            throw new TokenException("Token JWT inválido ou expirado!", exception);
        }
    }

    /**
     * Calcula a data de expiração do token com base no tempo atual e no valor configurado.
     *
     * @return A data de expiração como um objeto Instant.
     */
    private Instant dataExpiracao() {
        return LocalDateTime.now()
                .plusHours(expirationHours)
                .toInstant(ZoneOffset.systemDefault().getRules().getOffset(Instant.now()));
    }
}

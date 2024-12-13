package med.voll.api.infra.security;

public class TokenException extends RuntimeException {

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}

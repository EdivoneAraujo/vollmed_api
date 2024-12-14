package med.voll.api.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import med.voll.api.domain.usuario.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            try {
                // Extrai o subject (usuário) do token JWT
                var subject = tokenService.getSubject(tokenJWT);

                // Busca o usuário no banco de dados
                var usuario = repository.findByLogin(subject);

                if (usuario != null) {
                    // Cria a autenticação no contexto de segurança
                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("Usuário autenticado: " + subject);
                } else {
                    logger.warn("Usuário não encontrado para o token: " + subject);
                }
            } catch (Exception e) {
                logger.error("Erro ao processar o token JWT", e);
            }
        }

        // Continua o filtro da requisição
        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        // Recupera o token JWT do cabeçalho Authorization
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}


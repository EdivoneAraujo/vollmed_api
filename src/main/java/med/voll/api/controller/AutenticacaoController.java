package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.infra.security.TokenService;
import med.voll.api.domain.usuario.DadosAutenticacao;
import med.voll.api.domain.usuario.Usuario;
import med.voll.api.infra.security.DadosTokenJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacaoController {

    private static final Logger logger = LoggerFactory.getLogger(AutenticacaoController.class);

    private final AuthenticationManager manager;
    private final TokenService tokenService;

    @Autowired
    public AutenticacaoController(@Lazy AuthenticationManager manager, TokenService tokenService) {
        this.manager = manager;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<?> efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        try {
            // Criação do token de autenticação com as credenciais fornecidas
            var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
            var authentication = manager.authenticate(authenticationToken);

            // Geração do token JWT para o usuário autenticado
            var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

            // Retorno com o token gerado
            return ResponseEntity.ok(new DadosTokenJWT(tokenJWT));

        } catch (BadCredentialsException e) {
            // Caso as credenciais estejam erradas
            logger.error("Erro de autenticação: Credenciais inválidas", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas.");
        } catch (Exception e) {
            // Captura de outras exceções
            logger.error("Erro inesperado ao tentar efetuar login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado ao tentar efetuar login.");
        }
    }
}

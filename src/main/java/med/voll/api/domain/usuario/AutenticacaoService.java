package med.voll.api.domain.usuario;

import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.util.List;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AutenticacaoService.class);

    public void salvarUsuario(Usuario usuario) {
        String senhaCodificada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCodificada);
        repository.save(usuario);
        logger.info("Usuário '{}' salvo com sucesso", usuario.getLogin());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repository.findByLogin(username);
        if (usuario == null) {
            logger.error("Usuário '{}' não encontrado", username);
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        logger.info("Usuário '{}' autenticado com sucesso", username);
        return usuario;
    }


    @Transactional
    public void atualizarSenhas() {
        List<Usuario> usuarios = repository.findAll();

        for (Usuario usuario : usuarios) {
            String senhaOriginal = usuario.getSenha(); // Salva a senha original
            // Verifica se a senha ainda está em formato não codificado (se necessário)
            if (!passwordEncoder.matches(senhaOriginal, senhaOriginal)) {
                String senhaCodificada = passwordEncoder.encode(senhaOriginal);
                usuario.setSenha(senhaCodificada);
                repository.save(usuario);
            }
        }
    }



    public boolean verificarSenha(String senhaFornecida, Usuario usuario) {
        return passwordEncoder.matches(senhaFornecida, usuario.getSenha());
    }

    public boolean validarSenha(String senha, String senhaEnc) {
        return passwordEncoder.matches(senha, senhaEnc);
    }
}

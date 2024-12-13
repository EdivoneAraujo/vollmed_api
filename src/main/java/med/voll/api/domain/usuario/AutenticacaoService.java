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

    private static final Logger logger = LoggerFactory.getLogger(AutenticacaoService.class);

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void salvarUsuario(Usuario usuario) {
        String senhaCodificada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCodificada);
        repository.save(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repository.findByLogin(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        return new User(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
    }

    @Transactional
    public void atualizarSenhas() {
        List<Usuario> usuarios = repository.findAll();

        for (Usuario usuario : usuarios) {
            if (!passwordEncoder.matches(usuario.getSenha(), usuario.getSenha())) {
                String senhaCodificada = passwordEncoder.encode(usuario.getSenha());
                usuario.setSenha(senhaCodificada);
                repository.save(usuario);
            }
        }
    }

    public boolean verificarSenha(String senhaFornecida, Usuario usuario) {
        logger.debug("Senha fornecida: " + senhaFornecida);
        logger.debug("Senha armazenada: " + usuario.getPassword());
        return passwordEncoder.matches(senhaFornecida, usuario.getSenha());
    }

    public boolean validarSenha(String senha, String senhaEnc) {
        return passwordEncoder.matches(senha, senhaEnc);
    }
}

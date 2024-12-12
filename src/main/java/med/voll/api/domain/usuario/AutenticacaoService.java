package med.voll.api.domain.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AutenticacaoService(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repository.findByLogin(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }

        // Verifique se a senha fornecida corresponde ao hash armazenado no banco de dados
        if (!passwordEncoder.matches(usuario.getPassword(), usuario.getPassword())) {
            // Re-encode a senha se ela não estiver no formato correto
            String novaSenha = passwordEncoder.encode(usuario.getPassword());
            usuario.setSenha(novaSenha);
            repository.save(usuario); // Salve a senha re-encodificada
        }

        return new User(usuario.getUsername(), usuario.getPassword(), usuario.getAuthorities());
    }

    public boolean verificarSenha(String senhaFornecida, Usuario usuario) {
        // Verifica se a senha fornecida corresponde ao hash BCrypt do usuário
        return passwordEncoder.matches(senhaFornecida, usuario.getPassword());
    }

    public void salvarUsuario(Usuario usuario) {
        // Codifica a senha do usuário antes de salvar no banco de dados
        String senhaCodificada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCodificada);
        repository.save(usuario);
    }

    // Metodo de verificação de senha, para comparar se a senha fornecida corresponde ao hash
    public boolean validarSenha(String senha, String senhaEnc) {
        return passwordEncoder.matches(senha, senhaEnc);
    }
}

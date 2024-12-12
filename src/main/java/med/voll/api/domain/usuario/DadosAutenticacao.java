package med.voll.api.domain.usuario;

import jakarta.validation.constraints.NotBlank;

public record DadosAutenticacao(String login, String senha) {
}

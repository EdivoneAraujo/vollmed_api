package med.voll.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.domain.consulta.DadosAgendamentoConsulta;
import med.voll.api.domain.consulta.DadosCancelamentoConsulta;
import med.voll.api.domain.consulta.AgendaDeConsultas;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("consultas")
@SecurityRequirement(name = "bearer-key")
public class ConsultaController {

    private final AgendaDeConsultas agenda;

    @Autowired
    public ConsultaController(AgendaDeConsultas agenda) {
        this.agenda = agenda;
    }

    @PostMapping
    @Transactional
    public ResponseEntity agendar(@RequestBody @Valid DadosAgendamentoConsulta dados) {
        try {
            // Realizando o agendamento da consulta
            var dto = agenda.agendar(dados);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            // Retorno com erro se ocorrer uma falha
            return ResponseEntity.badRequest().body("Erro ao agendar consulta: " + e.getMessage());
        }
    }

    @DeleteMapping
    @Transactional
    public ResponseEntity cancelar(@RequestBody @Valid DadosCancelamentoConsulta dados) {
        try {
            // Realizando o cancelamento da consulta
            agenda.cancelar(dados);
            return ResponseEntity.noContent().build();  // Resposta 204 No Content
        } catch (Exception e) {
            // Retorno com erro se ocorrer uma falha no cancelamento
            return ResponseEntity.badRequest().body("Erro ao cancelar consulta: " + e.getMessage());
        }
    }
}

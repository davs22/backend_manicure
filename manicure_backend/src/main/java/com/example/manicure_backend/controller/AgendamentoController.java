package com.example.manicure_backend.controller;

import com.example.manicure_backend.DTO.AgendamentoRequestDTO;
import com.example.manicure_backend.model.Agendamento;
import com.example.manicure_backend.service.AgendamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoService service;

    // Rota para o CLIENTE ver o que ele marcou
    @GetMapping("/meus")
    public ResponseEntity<List<Agendamento>> listarMeusAgendamentos(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.listarMeusAgendamentos(token.replace("Bearer ", "")));
    }

    // Rota para a MANICURE ver quem marcou com ela
    @GetMapping("/minha-agenda")
    public ResponseEntity<List<Agendamento>> listarAgendaManicure() {
        return ResponseEntity.ok(service.listarAgendaManicurePeloLogin());
    }

    // Criar
    @PostMapping
    public ResponseEntity<Agendamento> criar(@RequestBody AgendamentoRequestDTO dto,
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(service.criar(dto, token.replace("Bearer ", "")));
    }

    // Deletar (Cancelar)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agendamento> buscarPorId(@PathVariable Long id) {
        // 1. Buscamos de forma explícita
        Optional<Agendamento> agendamentoOpt = service.buscarPorId(id);

        // 2. Verificamos se existe com IF
        if (agendamentoOpt.isPresent()) {
            return ResponseEntity.ok(agendamentoOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
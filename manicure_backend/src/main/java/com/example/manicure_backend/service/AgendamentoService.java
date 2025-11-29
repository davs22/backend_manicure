package com.example.manicure_backend.service;

import com.example.manicure_backend.DTO.AgendamentoRequestDTO;
import com.example.manicure_backend.model.*;
import com.example.manicure_backend.repository.*;
import com.example.manicure_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final ComplementosRepository complementosRepository;
    private final JwtUtil jwtUtil;

    // CLIENTE: Meus agendamentos
    public List<Agendamento> listarMeusAgendamentos(String token) {
        String email = jwtUtil.extractEmail(token); // Token já vem sem Bearer do controller
        Usuario cliente = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return repository.findByUsuario_IdUsuario(cliente.getIdUsuario());
    }

    // MANICURE: Minha agenda
    public List<Agendamento> listarAgendaManicurePeloLogin() {
        // 1. Pega o email de quem está logado
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        
        // 2. Busca o ID desse usuário
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado."));
        
        // 4. Busca direto no banco tudo que tiver o ID dela na coluna 'id_manicure'
        return repository.findByManicure_IdUsuario(usuario.getIdUsuario());
    }

    public Agendamento criar(AgendamentoRequestDTO dto, String token) {
        String email = jwtUtil.extractEmail(token);
        Usuario cliente = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Usuario manicure = usuarioRepository.findById(dto.getManicureId())
                .orElseThrow(() -> new RuntimeException("Manicure não encontrada"));

        // Validação se o alvo é manicure
        complementosRepository.findByUsuario_IdUsuario(manicure.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário selecionado não é manicure"));

        if(repository.existsByManicure_IdUsuarioAndDataAndHora(manicure.getIdUsuario(), dto.getData(), dto.getHora())) {
             throw new RuntimeException("Horário já ocupado");
        }

        Agendamento agendamento = new Agendamento();
        agendamento.setUsuario(cliente);
        agendamento.setManicure(manicure);
        agendamento.setDescricao(dto.getDescricao());
        agendamento.setData(dto.getData());
        agendamento.setHora(dto.getHora());
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        agendamento.setValor(0.0);

        return repository.save(agendamento);
    }

    public void deletar(Long id) {
        repository.deleteById(id);
    }

    public Optional<Agendamento> buscarPorId(Long id) {
        return repository.findById(id);
    }
}

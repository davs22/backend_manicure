package com.example.manicure_backend.DTO;

import com.example.manicure_backend.model.StatusAgendamento;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AgendamentoDTO {
    private Long idAgendamento;
    private String descricao;
    private LocalDate data;
    private LocalTime hora;
    private Double valor;
    private StatusAgendamento status;

    private UsuarioDTO usuario;
    private UsuarioDTO manicure;
}

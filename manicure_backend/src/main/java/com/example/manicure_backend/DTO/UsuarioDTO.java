package com.example.manicure_backend.DTO;

import com.example.manicure_backend.model.Sexo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long idUsuario;
    private String nome;
    private Integer idade;
    private String email;
    private String senha;
    private String urlFotoPerfil;
    private Sexo sexo;

    // Dados de Manicure
    private String especialidade;
    private String regiao;
    private boolean isManicure;

    // Dados Sociais (Novos)
    private long seguidores;
    private long seguindo;
    private boolean seguidoPorMim;
}
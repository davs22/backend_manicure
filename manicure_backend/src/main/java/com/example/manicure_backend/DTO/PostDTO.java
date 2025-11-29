package com.example.manicure_backend.DTO;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long idPost;
    private String titulo;
    private String descricao;
    private String urlImagem;
    private LocalDate data;
    
    // 🔴 DADOS OBRIGATÓRIOS PARA O FRONT
    private Long idAuthor;
    private String authorNome; 
    private String authorFoto;

    private long likesCount;
    private boolean likedByMe;
}
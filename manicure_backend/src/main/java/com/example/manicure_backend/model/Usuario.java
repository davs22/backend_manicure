package com.example.manicure_backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference; // Importante trocar para ManagedReference se der erro de loop
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    // 🔴 MUDANÇA AQUI: De LAZY para EAGER
    // Isso garante que os dados de manicure (especialidade/regiao) venham sempre.
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference // Ajuda a serializar corretamente o complemento
    private Complementos complemento;

    @Column(nullable = false)
    private String nome;

    private Integer idade;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true, name = "url_foto_perfil")
    private String urlFotoPerfil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Sexo sexo = Sexo.F;
}
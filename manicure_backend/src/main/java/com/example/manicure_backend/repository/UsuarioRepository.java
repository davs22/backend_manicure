package com.example.manicure_backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.manicure_backend.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByNomeContainingIgnoreCase(String nome);

    // 🔴 SQL NATIVO: Busca quem tem ID na tabela de complementos (Manicures)
    @Query(value = "SELECT u.* FROM usuario u INNER JOIN complementos c ON u.id_usuario = c.id_usuario", nativeQuery = true)
    List<Usuario> findAllManicures();
}
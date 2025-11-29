package com.example.manicure_backend.repository;

import com.example.manicure_backend.model.Curtida;
import com.example.manicure_backend.model.Post;
import com.example.manicure_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CurtidaRepository extends JpaRepository<Curtida, Long> {
    Optional<Curtida> findByUsuarioAndPost(Usuario usuario, Post post);
    long countByPost(Post post);
    boolean existsByUsuarioAndPost(Usuario usuario, Post post);
}
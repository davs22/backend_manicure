package com.example.manicure_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.manicure_backend.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    // O JPA monta a query automaticamente: WHERE author.email = ?
    List<Post> findAllByAuthorEmail(String email);
}

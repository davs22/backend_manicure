package com.example.manicure_backend.controller;

import com.example.manicure_backend.DTO.PostDTO;
import com.example.manicure_backend.model.Post;
import com.example.manicure_backend.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 🔴 ALTERADO: Agora aceita token opcional para verificar likes
    @GetMapping
    public ResponseEntity<List<PostDTO>> listarTodos(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(postService.listarTodosDTO(authHeader));
    }

    // ... (Mantenha os outros métodos como criar, atualizar, deletar e buscarPorId iguais) ...
    // Apenas certifique-se de que o criarPost chame o service.salvar corretamente
    @PostMapping
    public ResponseEntity<?> criarPost(@RequestBody Post post, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Post salvo = postService.salvar(post, token);
            return ResponseEntity.ok(salvo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
    
    // ... métodos /my, delete, etc.
    @GetMapping("/my")
    public ResponseEntity<List<PostDTO>> listarMeusPosts(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            return ResponseEntity.ok(postService.listarPostsPorUsuarioLogado(token));
        } catch (Exception e) { return ResponseEntity.status(401).build(); }
    }
}
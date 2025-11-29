package com.example.manicure_backend.controller;

import com.example.manicure_backend.model.*;
import com.example.manicure_backend.repository.*;
import com.example.manicure_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class InteracaoController {

    private final PostRepository postRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurtidaRepository curtidaRepository;
    private final ComentarioRepository comentarioRepository;
    private final JwtUtil jwtUtil;

    private Usuario getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    // 1. Alternar Like (Curtir/Descurtir)
    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        Usuario usuario = getUserFromToken(authHeader);
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post não encontrado"));

        var curtidaExistente = curtidaRepository.findByUsuarioAndPost(usuario, post);

        if (curtidaExistente.isPresent()) {
            curtidaRepository.delete(curtidaExistente.get());
            return ResponseEntity.ok(Map.of("liked", false));
        } else {
            Curtida nova = Curtida.builder().usuario(usuario).post(post).build();
            curtidaRepository.save(nova);
            return ResponseEntity.ok(Map.of("liked", true));
        }
    }

    // 2. Adicionar Comentário
    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long id, @RequestBody Map<String, String> body, @RequestHeader("Authorization") String authHeader) {
        Usuario usuario = getUserFromToken(authHeader);
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post não encontrado"));
        
        String texto = body.get("texto");
        if (texto == null || texto.isBlank()) return ResponseEntity.badRequest().body("Texto obrigatório");

        Comentario comentario = Comentario.builder()
                .usuario(usuario)
                .post(post)
                .texto(texto)
                .data(java.time.LocalDateTime.now())
                .build();

        comentarioRepository.save(comentario);
        
        // Retorna dados para atualizar a lista no front
        return ResponseEntity.ok(Map.of(
            "id", comentario.getId(),
            "texto", comentario.getTexto(),
            "autor", usuario.getNome(),
            "autorId", usuario.getIdUsuario()
        ));
    }

    // 3. Listar Comentários de um Post
    // 🔴 CORREÇÃO AQUI: Mudamos o retorno para ResponseEntity<?> para evitar o erro de Tipagem
    @GetMapping("/{id}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long id) {
        Post post = postRepository.findById(id).orElseThrow();
        List<Comentario> comentarios = comentarioRepository.findByPostOrderByDataDesc(post);
        
        var response = comentarios.stream().map(c -> Map.of(
            "id", c.getId(),
            "texto", c.getTexto(),
            "autor", c.getUsuario().getNome(),
            "autorId", c.getUsuario().getIdUsuario() 
        )).toList();

        return ResponseEntity.ok(response);
    }
}
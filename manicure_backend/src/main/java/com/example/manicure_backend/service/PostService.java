package com.example.manicure_backend.service;

import com.example.manicure_backend.DTO.PostDTO; // Import da pasta DTO
import com.example.manicure_backend.model.*;
import com.example.manicure_backend.repository.*;
import com.example.manicure_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UsuarioRepository usuarioRepository;
    private final CurtidaRepository curtidaRepository;
    private final JwtUtil jwtUtil;

    private PostDTO toDTO(Post post, Usuario currentUser) {
        long likes = curtidaRepository.countByPost(post);
        boolean liked = (currentUser != null) && curtidaRepository.existsByUsuarioAndPost(currentUser, post);
        Usuario autor = post.getAuthor();

        return new PostDTO(
            post.getIdPost(),
            post.getTitulo(),
            post.getDescricao(),
            post.getUrlImagem(),
            post.getData(),
            autor.getIdUsuario(),      
            autor.getNome(),           
            autor.getUrlFotoPerfil(),  // 🔴 Garante a foto
            likes,
            liked
        );
    }

    @Transactional(readOnly = true)
    public List<PostDTO> listarTodosDTO(String token) {
        Usuario currentUser = null;
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String email = jwtUtil.extractEmail(token.substring(7));
                currentUser = usuarioRepository.findByEmail(email).orElse(null);
            } catch (Exception e) {}
        }
        Usuario finalUser = currentUser;
        return postRepository.findAll().stream().map(p -> toDTO(p, finalUser)).collect(Collectors.toList());
    }

    public Post salvar(Post post, String token) {
        String email = jwtUtil.extractEmail(token);
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow();
        if (usuario.getComplemento() == null) throw new RuntimeException("Apenas manicures.");
        post.setAuthor(usuario);
        return postRepository.save(post);
    }
    
    public List<PostDTO> listarPostsPorUsuarioLogado(String token) {
        String email = jwtUtil.extractEmail(token);
        Usuario user = usuarioRepository.findByEmail(email).orElseThrow();
        return postRepository.findAllByAuthorEmail(email).stream().map(p -> toDTO(p, user)).collect(Collectors.toList());
    }
    
    public Optional<PostDTO> buscarPorIdDTO(Long id) {
        return postRepository.findById(id).map(p -> toDTO(p, null));
    }
    
    public void deletar(Long id, String token) {
        String email = jwtUtil.extractEmail(token);
        Post post = postRepository.findById(id).orElseThrow();
        if(!post.getAuthor().getEmail().equals(email)) throw new RuntimeException("Não autorizado");
        postRepository.delete(post);
    }
}
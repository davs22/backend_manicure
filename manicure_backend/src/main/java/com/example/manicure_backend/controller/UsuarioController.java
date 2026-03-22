package com.example.manicure_backend.controller;

import com.example.manicure_backend.DTO.UsuarioDTO;
import com.example.manicure_backend.model.Usuario;
import com.example.manicure_backend.service.UsuarioService;
import com.example.manicure_backend.repository.UsuarioRepository;
import com.example.manicure_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    // 🚀 NOVO: Método auxiliar para extrair o ID de quem está fazendo a requisição
    private Long getMeuId(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String email = jwtUtil.extractEmail(token);
                return usuarioRepository.findByEmail(email).map(Usuario::getIdUsuario).orElse(null);
            } catch (Exception e) {}
        }
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long meuId = getMeuId(authHeader); // Lê quem está chamando a tela
        return ResponseEntity.ok(usuarioService.buscarPorIdDTO(id, meuId)); 
    }

    @GetMapping("/nome")
    public ResponseEntity<List<UsuarioDTO>> buscarPorNome(@RequestParam String nome, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long meuId = getMeuId(authHeader);
        return ResponseEntity.ok(usuarioService.buscarPorNome(nome, meuId));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long meuId = getMeuId(authHeader);
        Usuario atualizado = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(usuarioService.toDTO(atualizado, meuId));
    }

    @GetMapping("/manicures")
    public ResponseEntity<List<UsuarioDTO>> listarManicures() {
        return ResponseEntity.ok(usuarioService.listarApenasManicures());
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long meuId = getMeuId(authHeader);
        return ResponseEntity.ok(usuarioService.buscarPorNome("", meuId));
    }
}
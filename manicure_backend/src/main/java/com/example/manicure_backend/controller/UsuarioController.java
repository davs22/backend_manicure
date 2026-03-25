package com.example.manicure_backend.controller;

import com.example.manicure_backend.DTO.UsuarioDTO;
import com.example.manicure_backend.model.Usuario;
import com.example.manicure_backend.repository.UsuarioRepository;
import com.example.manicure_backend.security.JwtUtil;
import com.example.manicure_backend.service.UsuarioService;
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

    private Long getMeuId(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                String email = jwtUtil.extractEmail(token);
                return usuarioRepository.findByEmail(email).map(Usuario::getIdUsuario).orElse(null);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long meuId = getMeuId(authHeader);
        return ResponseEntity.ok(usuarioService.buscarPorIdDTO(id, meuId));
    }

    @GetMapping("/nome")
    public ResponseEntity<List<UsuarioDTO>> buscarPorNome(
            @RequestParam String nome,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long meuId = getMeuId(authHeader);
        return ResponseEntity.ok(usuarioService.buscarPorNome(nome, meuId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(
            @PathVariable Long id,
            @RequestBody UsuarioDTO dto,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        try {
            Long meuId = getMeuId(authHeader);
            Usuario atualizado = usuarioService.atualizar(id, dto);
            return ResponseEntity.ok(usuarioService.toDTO(atualizado, meuId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> excluirMinhaConta(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long meuId = getMeuId(authHeader);
        if (meuId == null) {
            return ResponseEntity.status(401).build();
        }
        usuarioService.excluirConta(meuId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/manicures")
    public ResponseEntity<List<UsuarioDTO>> listarManicures(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "bairro", required = false) String bairro,
            @RequestParam(value = "cidade", required = false) String cidade,
            @RequestParam(value = "estado", required = false) String estado,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long meuId = getMeuId(authHeader);
        return ResponseEntity.ok(usuarioService.listarApenasManicures(q, bairro, cidade, estado, meuId));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long meuId = getMeuId(authHeader);
        return ResponseEntity.ok(usuarioService.buscarPorNome("", meuId));
    }
}

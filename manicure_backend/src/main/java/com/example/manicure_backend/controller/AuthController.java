package com.example.manicure_backend.controller;

import com.example.manicure_backend.DTO.LoginRequest;
import com.example.manicure_backend.DTO.UsuarioDTO;
import com.example.manicure_backend.model.Usuario;
import com.example.manicure_backend.security.JwtUtil;
import com.example.manicure_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    // Registro de usuário
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsuarioDTO dto) {
        try {
            Usuario novoUsuario = usuarioService.registrarUsuario(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "id", novoUsuario.getIdUsuario(),
                    "nome", novoUsuario.getNome(),
                    "email", novoUsuario.getEmail()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Login de usuário
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return usuarioService.login(request.getEmail(), request.getSenha())
                .map(usuario -> {
                    String token = jwtUtil.generateToken(usuario.getEmail());
                    return ResponseEntity.ok(Map.of(
                            "token", token,
                            "usuario", Map.of(
                                "id", usuario.getIdUsuario(),
                                "nome", usuario.getNome(),
                                "email", usuario.getEmail()
                            )
                    ));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Email ou senha inválidos")));
    }
}


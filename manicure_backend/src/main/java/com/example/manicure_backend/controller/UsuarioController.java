package com.example.manicure_backend.controller;

import com.example.manicure_backend.DTO.UsuarioDTO; // DTO Maiúsculo
import com.example.manicure_backend.model.Usuario;
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

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorIdDTO(id, null)); 
    }

    @GetMapping("/nome")
    public ResponseEntity<List<UsuarioDTO>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(usuarioService.buscarPorNome(nome, null));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        Usuario atualizado = usuarioService.atualizar(id, dto);
        return ResponseEntity.ok(usuarioService.toDTO(atualizado, null));
    }

    // 🔴 ROTA EXCLUSIVA PARA O SELECT DE AGENDAMENTO
    @GetMapping("/manicures")
    public ResponseEntity<List<UsuarioDTO>> listarManicures() {
        return ResponseEntity.ok(usuarioService.listarApenasManicures());
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.buscarPorNome("", null));
    }
}
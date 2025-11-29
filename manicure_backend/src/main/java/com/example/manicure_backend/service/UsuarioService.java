package com.example.manicure_backend.service;

import com.example.manicure_backend.DTO.UsuarioDTO;
import com.example.manicure_backend.model.*;
import com.example.manicure_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final ComplementosRepository complementosRepository;
    private final SeguindoRepository seguindoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(List.of()) // ✅ SEM ROLE
                .build();
    }

    public Optional<Usuario> login(String email, String senha) {
        return usuarioRepository.findByEmail(email).filter(u -> passwordEncoder.matches(senha, u.getSenha()));
    }

    @Transactional(readOnly = true)
    public UsuarioDTO toDTO(Usuario user, Long meuId) {
        Complementos comp = user.getComplemento();
        boolean isManicure = comp != null;

        long seguidores = seguindoRepository.countBySeguido_IdUsuario(user.getIdUsuario());
        long seguindo = seguindoRepository.countBySeguidor_IdUsuario(user.getIdUsuario());
        boolean sigoEle = (meuId != null)
                && seguindoRepository.existsBySeguidor_IdUsuarioAndSeguido_IdUsuario(meuId, user.getIdUsuario());

        return UsuarioDTO.builder()
                .idUsuario(user.getIdUsuario())
                .nome(user.getNome())
                .email(user.getEmail())
                .idade(user.getIdade())
                .sexo(user.getSexo())
                .urlFotoPerfil(user.getUrlFotoPerfil())
                .especialidade(isManicure ? comp.getEspecialidade() : null)
                .regiao(isManicure ? comp.getRegiao() : null)
                .isManicure(isManicure)
                .seguidores(seguidores)
                .seguindo(seguindo)
                .seguidoPorMim(sigoEle)
                .build();
    }

    public List<UsuarioDTO> listarApenasManicures() {
        return usuarioRepository.findAllManicures().stream()
                .map(u -> toDTO(u, null))
                .collect(Collectors.toList());
    }

    @Transactional
    public Usuario registrarUsuario(UsuarioDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent())
            throw new RuntimeException("Email já cadastrado");

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .idade(dto.getIdade())
                .sexo(dto.getSexo())
                .urlFotoPerfil(dto.getUrlFotoPerfil())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .build();

        // 1. Salva o usuário
        usuario = usuarioRepository.save(usuario);

        // 2. Se for manicure, SALVA O COMPLEMENTO
        if (dto.getEspecialidade() != null && !dto.getEspecialidade().trim().isEmpty()) {
            Complementos c = new Complementos();
            c.setUsuario(usuario); // Liga ao usuário recém criado
            c.setEspecialidade(dto.getEspecialidade());
            c.setRegiao(dto.getRegiao());

            complementosRepository.save(c); // Grava no banco
            usuario.setComplemento(c); // Atualiza objeto em memória
        }
        return usuario;
    }

    @Transactional
    public Usuario atualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        if (dto.getNome() != null)
            usuario.setNome(dto.getNome());
        if (dto.getIdade() != null)
            usuario.setIdade(dto.getIdade());
        if (dto.getUrlFotoPerfil() != null)
            usuario.setUrlFotoPerfil(dto.getUrlFotoPerfil());
        if (dto.getSenha() != null && !dto.getSenha().isBlank())
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));

        if (dto.getEspecialidade() != null && !dto.getEspecialidade().isBlank()) {
            Complementos comp = usuario.getComplemento();
            if (comp == null) {
                comp = new Complementos();
                comp.setUsuario(usuario);
            }
            comp.setEspecialidade(dto.getEspecialidade());
            comp.setRegiao(dto.getRegiao());
            complementosRepository.save(comp);
            usuario.setComplemento(comp);
        } else if (dto.getEspecialidade() != null && dto.getEspecialidade().isEmpty()) {
            if (usuario.getComplemento() != null) {
                complementosRepository.delete(usuario.getComplemento());
                usuario.setComplemento(null);
            }
        }
        return usuarioRepository.save(usuario);
    }

    public UsuarioDTO buscarPorIdDTO(Long id, Long meuId) {
        return toDTO(usuarioRepository.findById(id).orElseThrow(), meuId);
    }

    public List<UsuarioDTO> buscarPorNome(String nome, Long meuId) {
        if (nome == null)
            nome = "";
        return usuarioRepository.findByNomeContainingIgnoreCase(nome).stream().map(u -> toDTO(u, meuId))
                .collect(Collectors.toList());
    }
}
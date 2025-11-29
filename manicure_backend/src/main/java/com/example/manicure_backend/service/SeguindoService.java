package com.example.manicure_backend.service;

import com.example.manicure_backend.model.Seguindo;
import com.example.manicure_backend.model.Usuario;
import com.example.manicure_backend.repository.SeguindoRepository;
import com.example.manicure_backend.repository.UsuarioRepository;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeguindoService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private SeguindoRepository seguindoRepository; 
    
    /**
     * Inicia o seguimento de um usuário por outro.
     * @param seguidorId ID do usuário logado (quem segue).
     * @param seguidoId ID do usuário alvo (quem é seguido).
     */
    @Transactional
    public void follow(Long seguidorId, Long seguidoId) {
        
        // 1. Regra de Negócio: Não pode seguir a si mesmo.
        if (seguidorId.equals(seguidoId)) {
            throw new IllegalStateException("Um usuário não pode seguir a si mesmo.");
        }

        // 2. Busca o usuário seguido (garante que o alvo existe).
        // O Usuario seguido é carregado.
        Usuario seguido = usuarioRepository.findById(seguidoId)
            .orElseThrow(() -> new NoSuchElementException("Usuário seguido não encontrado."));
        
        // 3. OTIMIZAÇÃO: Verifica se o relacionamento já existe usando apenas os IDs.
        // Evita carregar os objetos completos do Seguidor e Seguido novamente.
        if (seguindoRepository.existsBySeguidor_IdUsuarioAndSeguido_IdUsuario(seguidorId, seguidoId)) {
              throw new IllegalStateException("Você já está seguindo este usuário.");
        }
        
        // 4. Cria uma Referência ao Seguidor.
        // Isso evita uma consulta SELECT desnecessária na tabela 'usuario' para o seguidor,
        // pois o ID já é conhecido e garantido pela autenticação.
        Usuario seguidorReference = usuarioRepository.getReferenceById(seguidorId);
        
        // 5. Cria e salva o novo relacionamento
        Seguindo seguindo = Seguindo.builder()
            .seguidor(seguidorReference)
            .seguido(seguido)
            .build();
            
        seguindoRepository.save(seguindo);
    }
    
    /**
     * Desfaz o seguimento de um usuário por outro (unfollow).
     * @param seguidorId ID do usuário logado (quem segue).
     * @param seguidoId ID do usuário alvo (quem é seguido).
     */
    @Transactional
    public void unfollow(Long seguidorId, Long seguidoId) {
        
        // 1. Busca o relacionamento usando os IDs do seguidor e do seguido.
        Seguindo seguimento = seguindoRepository.findBySeguidor_IdUsuarioAndSeguido_IdUsuario(seguidorId, seguidoId)
            .orElseThrow(() -> new NoSuchElementException("Você não está seguindo este usuário."));
        
        // 2. Deleta o relacionamento
        seguindoRepository.delete(seguimento);
    }
    
    // --- Novos Métodos Adicionais (Opcional, mas útil) ---
    
    /**
     * Verifica se o usuário seguidor está seguindo o usuário seguido.
     * @param seguidorId ID do seguidor.
     * @param seguidoId ID do seguido.
     * @return true se estiver seguindo, false caso contrário.
     */
    @Transactional(readOnly = true)
    public boolean isFollowing(Long seguidorId, Long seguidoId) {
        // Reusa o método de verificação de existência otimizado
        return seguindoRepository.existsBySeguidor_IdUsuarioAndSeguido_IdUsuario(seguidorId, seguidoId);
    }
    
    /**
     * Retorna o número de seguidores de um usuário.
     * @param usuarioId ID do usuário alvo.
     * @return O número de seguidores.
     */
    @Transactional(readOnly = true)
    public long getFollowersCount(Long usuarioId) {
        // Assume-se que o SeguindoRepository implementa um método de contagem por ID do Seguido.
        return seguindoRepository.countBySeguido_IdUsuario(usuarioId);
    }
    
    /**
     * Retorna o número de usuários que um usuário está seguindo.
     * @param usuarioId ID do usuário alvo.
     * @return O número de seguidos.
     */
    @Transactional(readOnly = true)
    public long getFollowingCount(Long usuarioId) {
        // Assume-se que o SeguindoRepository implementa um método de contagem por ID do Seguidor.
        return seguindoRepository.countBySeguidor_IdUsuario(usuarioId);
    }
}
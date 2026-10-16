package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.UsuarioSistema;
import com.marceloaleixo.centrocirurgico.repository.UsuarioSistemaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlteracaoSenhaService {
    private final UsuarioSistemaRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AlteracaoSenhaService(UsuarioSistemaRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void alterar(String username, String senhaAtual, String novaSenha, String confirmarSenha) {
        UsuarioSistema usuario = repository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário autenticado não foi encontrado."));

        if (senhaAtual == null || senhaAtual.isBlank() || !passwordEncoder.matches(senhaAtual, usuario.getSenhaHash())) {
            throw new IllegalArgumentException("A senha atual está incorreta.");
        }
        if (novaSenha == null || novaSenha.length() < 12) {
            throw new IllegalArgumentException("A nova senha deve ter pelo menos 12 caracteres.");
        }
        if (novaSenha.length() > 100) {
            throw new IllegalArgumentException("A nova senha não pode exceder 100 caracteres.");
        }
        if (confirmarSenha == null || !novaSenha.equals(confirmarSenha)) {
            throw new IllegalArgumentException("A confirmação não corresponde à nova senha.");
        }
        if (passwordEncoder.matches(novaSenha, usuario.getSenhaHash())) {
            throw new IllegalArgumentException("A nova senha deve ser diferente da senha atual.");
        }
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        repository.save(usuario);
    }
}

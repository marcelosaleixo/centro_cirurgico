package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.UsuarioSistema;
import com.marceloaleixo.centrocirurgico.repository.UsuarioSistemaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class GestaoUsuarioSistemaService {
    private static final Set<String> PERFIS = Set.of("ADMIN_RH", "RH", "CONSULTA");
    private final UsuarioSistemaRepository repository;
    private final PasswordEncoder passwordEncoder;

    public GestaoUsuarioSistemaService(UsuarioSistemaRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioSistema> listar() {
        return repository.findAll().stream()
                .sorted((a, b) -> a.getUsername().compareToIgnoreCase(b.getUsername()))
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioSistema buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }

    @Transactional
    public UsuarioSistema salvar(Long id, String username, String perfil, String senha, boolean ativo) {
        String login = username == null ? "" : username.trim();
        String role = perfil == null ? "" : perfil.trim().toUpperCase(Locale.ROOT);
        if (login.length() < 3 || login.length() > 100)
            throw new IllegalArgumentException("O usuário deve ter entre 3 e 100 caracteres.");
        if (!PERFIS.contains(role))
            throw new IllegalArgumentException("Perfil inválido.");
        UsuarioSistema usuario = id == null ? new UsuarioSistema(login, "TEMPORARIO", role, ativo) : buscar(id);
        boolean eraAdminAtivo = id != null && usuario.isAtivo() && "ADMIN_RH".equals(usuario.getPerfil());
        if (eraAdminAtivo && (!ativo || !"ADMIN_RH".equals(role)) && contarOutrosAdminsAtivos(usuario.getId()) == 0)
            throw new IllegalArgumentException("Não é possível desativar ou rebaixar o último administrador ativo.");
        if (repository.findByUsernameIgnoreCase(login).filter(u -> !u.getId().equals(usuario.getId())).isPresent())
            throw new IllegalArgumentException("Já existe uma conta com esse nome de usuário.");
        if (id == null && (senha == null || senha.length() < 12))
            throw new IllegalArgumentException("A senha inicial deve ter pelo menos 12 caracteres.");
        if (senha != null && !senha.isBlank() && senha.length() < 12)
            throw new IllegalArgumentException("A senha deve ter pelo menos 12 caracteres.");
        if (senha != null && !senha.isBlank()) usuario.setSenhaHash(passwordEncoder.encode(senha));
        usuario.setUsername(login);
        usuario.setPerfil(role);
        usuario.setAtivo(ativo);
        return repository.save(usuario);
    }

    @Transactional
    public void alternarAtivo(Long id) {
        UsuarioSistema usuario = buscar(id);
        if (usuario.isAtivo() && "ADMIN_RH".equals(usuario.getPerfil()) && contarOutrosAdminsAtivos(id) == 0)
            throw new IllegalArgumentException("Não é possível desativar o último administrador ativo.");
        usuario.setAtivo(!usuario.isAtivo());
        repository.save(usuario);
    }

    private long contarOutrosAdminsAtivos(Long idIgnorado) {
        return repository.findAll().stream()
                .filter(UsuarioSistema::isAtivo)
                .filter(u -> "ADMIN_RH".equals(u.getPerfil()))
                .filter(u -> !u.getId().equals(idIgnorado))
                .count();
    }
}

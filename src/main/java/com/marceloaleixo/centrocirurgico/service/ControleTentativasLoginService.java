package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.UsuarioSistema;
import com.marceloaleixo.centrocirurgico.repository.UsuarioSistemaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class ControleTentativasLoginService {
    private static final int LIMITE_TENTATIVAS = 5;
    private static final int MINUTOS_BLOQUEIO = 15;
    private final UsuarioSistemaRepository usuarios;
    private final AuditoriaSegurancaService auditoria;

    public ControleTentativasLoginService(UsuarioSistemaRepository usuarios, AuditoriaSegurancaService auditoria) {
        this.usuarios = usuarios;
        this.auditoria = auditoria;
    }

    @Transactional
    public void registrarFalha(String username) {
        if (username == null || username.isBlank()) {
            auditoria.registrar("desconhecido", "LOGIN_FALHA", "Nome de usuário não informado", false);
            return;
        }
        var encontrado = usuarios.findByUsernameIgnoreCase(username.trim());
        if (encontrado.isEmpty()) {
            auditoria.registrar(username.trim(), "LOGIN_FALHA", "Tentativa para usuário inexistente", false);
            return;
        }
        UsuarioSistema usuario = encontrado.get();
        OffsetDateTime agora = OffsetDateTime.now();
        if (usuario.getBloqueadoAte() != null && usuario.getBloqueadoAte().isAfter(agora)) {
            auditoria.registrar(usuario.getUsername(), "LOGIN_BLOQUEADO", "Tentativa durante bloqueio temporário", false);
            return;
        }
        if (usuario.getBloqueadoAte() != null && !usuario.getBloqueadoAte().isAfter(agora)) {
            usuario.setBloqueadoAte(null);
            usuario.setTentativasFalhas(0);
        }
        int tentativas = usuario.getTentativasFalhas() + 1;
        usuario.setTentativasFalhas(tentativas);
        if (tentativas >= LIMITE_TENTATIVAS) {
            usuario.setBloqueadoAte(agora.plusMinutes(MINUTOS_BLOQUEIO));
            auditoria.registrar(usuario.getUsername(), "LOGIN_BLOQUEADO",
                    "Conta bloqueada por 15 minutos após " + tentativas + " tentativas inválidas", false);
        } else {
            auditoria.registrar(usuario.getUsername(), "LOGIN_FALHA",
                    "Tentativa inválida " + tentativas + " de " + LIMITE_TENTATIVAS, false);
        }
        usuarios.save(usuario);
    }

    @Transactional
    public void registrarSucesso(String username) {
        usuarios.findByUsernameIgnoreCase(username).ifPresent(usuario -> {
            usuario.setTentativasFalhas(0);
            usuario.setBloqueadoAte(null);
            usuarios.save(usuario);
        });
        auditoria.registrar(username, "LOGIN_SUCESSO", "Autenticação realizada", true);
    }

    @Transactional(readOnly = true)
    public boolean estaBloqueado(String username) {
        return usuarios.findByUsernameIgnoreCase(username)
                .map(UsuarioSistema::isBloqueadoAgora).orElse(false);
    }

    /**
     * Lista as contas que ainda estão dentro do período de bloqueio.
     */
    @Transactional(readOnly = true)
    public java.util.List<UsuarioSistema> listarBloqueados() {
        return usuarios.findAll().stream()
                .filter(UsuarioSistema::isAtivo)
                .filter(UsuarioSistema::isBloqueadoAgora)
                .sorted(java.util.Comparator.comparing(UsuarioSistema::getUsername,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /**
     * Libera uma conta bloqueada por tentativas inválidas e registra a justificativa.
     */
    @Transactional
    public void desbloquearManualmente(Long usuarioId, String administrador, String justificativa) {
        if (justificativa == null || justificativa.trim().length() < 10
                || justificativa.trim().length() > 500) {
            throw new IllegalArgumentException("A justificativa deve ter entre 10 e 500 caracteres.");
        }
        UsuarioSistema usuario = usuarios.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        if (!usuario.isBloqueadoAgora()) {
            throw new IllegalArgumentException("Esta conta não está bloqueada neste momento.");
        }
        usuario.setTentativasFalhas(0);
        usuario.setBloqueadoAte(null);
        usuarios.save(usuario);
        String autor = (administrador == null || administrador.isBlank())
                ? "desconhecido" : administrador.trim();
        auditoria.registrar(autor, "DESBLOQUEIO_MANUAL",
                "Conta desbloqueada: " + usuario.getUsername()
                        + ". Justificativa: " + justificativa.trim(), true);
    }
}

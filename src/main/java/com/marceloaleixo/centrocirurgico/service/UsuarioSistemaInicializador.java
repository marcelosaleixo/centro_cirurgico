package com.marceloaleixo.centrocirurgico.service;

import com.marceloaleixo.centrocirurgico.entity.UsuarioSistema;
import com.marceloaleixo.centrocirurgico.repository.UsuarioSistemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UsuarioSistemaInicializador implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(UsuarioSistemaInicializador.class);

    private final UsuarioSistemaRepository repository;
    private final PasswordEncoder encoder;

    @Value("${app.bootstrap.admin.username:admin.rh}")
    private String adminUsername;

    @Value("${app.bootstrap.admin.password:}")
    private String adminPassword;

    public UsuarioSistemaInicializador(UsuarioSistemaRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        String username = adminUsername == null ? "" : adminUsername.trim();
        if (username.isBlank()) {
            log.error("Administrador inicial NÃO criado: configure RH_ADMIN_USERNAME e RH_ADMIN_PASSWORD.");
            return;
        }
        if (adminPassword == null || adminPassword.length() < 12) {
            log.error("Administrador inicial NÃO criado: RH_ADMIN_PASSWORD precisa ter pelo menos 12 caracteres.");
            return;
        }

        var existente = repository.findByUsernameIgnoreCase(username);
        if (existente.isPresent()) {
            UsuarioSistema usuario = existente.get();
            if (!usuario.isAtivo() || !"ADMIN_RH".equals(usuario.getPerfil())) {
                usuario.setAtivo(true);
                usuario.setPerfil("ADMIN_RH");
                repository.save(usuario);
                log.warn("Conta '{}' reativada/promovida para ADMIN_RH. A senha existente foi mantida.", username);
            } else {
                log.info("Administrador inicial '{}' já existe; nenhuma alteração realizada.", username);
            }
            return;
        }

        repository.save(new UsuarioSistema(username, encoder.encode(adminPassword), "ADMIN_RH", true));
        log.info("Administrador inicial '{}' criado com sucesso.", username);
    }
}

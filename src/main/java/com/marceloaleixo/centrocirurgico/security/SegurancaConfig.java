package com.marceloaleixo.centrocirurgico.security;

import com.marceloaleixo.centrocirurgico.repository.UsuarioSistemaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableMethodSecurity
public class SegurancaConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuditoriaLoginSuccessHandler successHandler,
                                                   AuditoriaLoginFailureHandler failureHandler) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                // Recursos públicos necessários para autenticação e páginas de erro.
                .requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**", "/webjars/**", "/actuator/health", "/actuator/health/**").permitAll()

                // Administração e auditoria: somente ADMIN_RH.
                .requestMatchers("/admin/usuarios/**", "/admin/auditoria-seguranca/**", "/admin/auditoria-operacoes/**", "/admin/auditoria-alteracoes/**", "/admin/seguranca/**", "/admin/backups/**").hasRole("ADMIN_RH")
                .requestMatchers(HttpMethod.POST,
                        "/relatorios/fechamento-rh/fechar",
                        "/relatorios/fechamento-rh/reabrir").hasRole("ADMIN_RH")

                // CONSULTA pode visualizar módulos operacionais, mas não alterá-los.
                .requestMatchers(HttpMethod.GET,
                        "/", "/dashboard", "/colaboradores", "/colaboradores/**",
                        "/equipes", "/equipes/**", "/funcoes", "/funcoes/**",
                        "/feriados", "/feriados/**", "/ferias", "/ferias/**",
                        "/escalas", "/escalas/**", "/trocas", "/trocas/**",
                        "/estoque", "/estoque/**", "/banco-horas", "/banco-horas/**",
                        "/relatorios/**").hasAnyRole("ADMIN_RH", "RH", "CONSULTA")

                // Operações de escrita exigem perfil administrativo ou RH.
                .requestMatchers(HttpMethod.POST,
                        "/colaboradores/**", "/equipes/**", "/funcoes/**",
                        "/feriados/**", "/ferias/**", "/escalas/**", "/trocas/**",
                        "/estoque/**", "/banco-horas/**",
                        "/relatorios/fechamento-rh/conferencia/**").hasAnyRole("ADMIN_RH", "RH")

                // Conta pessoal e logout permanecem acessíveis ao usuário autenticado.
                .requestMatchers("/minha-conta/**", "/acesso-negado").authenticated()

                // Evita conceder acesso por acidente a perfis desconhecidos.
                .anyRequest().hasAnyRole("ADMIN_RH", "RH", "CONSULTA")
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        http.sessionManagement(session -> session
                .invalidSessionUrl("/login?expired")
                .sessionFixation(fixation -> fixation.migrateSession())
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/login?concurrent")
                .sessionRegistry(sessionRegistry())
        );

        http.exceptionHandling(ex -> ex.accessDeniedPage("/acesso-negado"));

        // Etapa 29: manter CSRF habilitado e reforçar cabeçalhos HTTP.
        // Não desabilite CSRF globalmente: formulários Thymeleaf com th:action
        // incluem o token automaticamente.
        http.csrf(csrf -> { });
        http.headers(headers -> headers
                .contentTypeOptions(contentType -> { })
                .frameOptions(frame -> frame.deny())
                .referrerPolicy(referrer -> referrer
                        .policy(org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                        "default-src 'self'; " +
                        "img-src 'self' data:; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "script-src 'self' 'unsafe-inline'; " +
                        "object-src 'none'; " +
                        "base-uri 'self'; " +
                        "frame-ancestors 'none'; " +
                        "form-action 'self'"))
        );

        return http.build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioSistemaRepository repository) {
        return username -> repository.findByUsernameIgnoreCase(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getSenhaHash())
                        .roles(u.getPerfil())
                        .disabled(!u.isAtivo() || u.isBloqueadoAgora())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }
}

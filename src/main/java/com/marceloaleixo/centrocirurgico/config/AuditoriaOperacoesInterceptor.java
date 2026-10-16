package com.marceloaleixo.centrocirurgico.config;

import com.marceloaleixo.centrocirurgico.service.AuditoriaOperacaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuditoriaOperacoesInterceptor implements HandlerInterceptor {
    private final AuditoriaOperacaoService service;
    public AuditoriaOperacoesInterceptor(AuditoriaOperacaoService service) { this.service = service; }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception exception) {
        String metodo = request.getMethod();
        if (!("POST".equals(metodo) || "PUT".equals(metodo) || "PATCH".equals(metodo) || "DELETE".equals(metodo))) return;
        String caminho = request.getRequestURI();
        // Endpoints de autenticação e troca de senha já têm auditoria própria; nunca registrar parâmetros/corpo.
        if (caminho == null || caminho.equals("/login") || caminho.equals("/logout")
                || caminho.startsWith("/minha-conta/senha") || caminho.startsWith("/admin/auditoria-operacoes")) return;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuario = auth != null && auth.isAuthenticated() && auth.getName() != null
                ? auth.getName() : "anônimo";
        try {
            service.registrar(usuario, metodo, caminho, response.getStatus());
        } catch (RuntimeException ignored) {
            // A auditoria não deve mascarar a resposta original; falhas de persistência devem ser monitoradas nos logs.
        }
    }
}

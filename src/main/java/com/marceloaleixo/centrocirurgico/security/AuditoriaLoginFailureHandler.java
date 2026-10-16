package com.marceloaleixo.centrocirurgico.security;

import com.marceloaleixo.centrocirurgico.service.ControleTentativasLoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class AuditoriaLoginFailureHandler implements AuthenticationFailureHandler {
    private final ControleTentativasLoginService controle;
    public AuditoriaLoginFailureHandler(ControleTentativasLoginService controle) { this.controle = controle; }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        controle.registrarFalha(request.getParameter("username"));
        response.sendRedirect(request.getContextPath() + "/login?error");
    }
}

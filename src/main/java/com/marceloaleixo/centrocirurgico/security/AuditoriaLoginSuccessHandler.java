package com.marceloaleixo.centrocirurgico.security;

import com.marceloaleixo.centrocirurgico.service.ControleTentativasLoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class AuditoriaLoginSuccessHandler implements AuthenticationSuccessHandler {
    private final ControleTentativasLoginService controle;
    private final SavedRequestAwareAuthenticationSuccessHandler delegate = new SavedRequestAwareAuthenticationSuccessHandler();

    public AuditoriaLoginSuccessHandler(ControleTentativasLoginService controle) {
        this.controle = controle;
        this.delegate.setDefaultTargetUrl("/");
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        controle.registrarSucesso(authentication.getName());
        delegate.onAuthenticationSuccess(request, response, authentication);
    }
}

package com.example.school_mangement_system.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException, ServletException {
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();

            if (role.equals("ROLE_SUPER_ADMIN") || role.equals("ROLE_ADMIN")) {
                setDefaultTargetUrl("/dashboard");
                break;
            } else if (role.equals("ROLE_TEACHER")) {
                setDefaultTargetUrl("/dashboard");
                break;
            } else if (role.equals("ROLE_STUDENT")) {
                setDefaultTargetUrl("/student/dashboard");
                break;
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}

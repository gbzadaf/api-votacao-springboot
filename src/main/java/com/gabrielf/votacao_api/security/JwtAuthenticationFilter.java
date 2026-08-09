package com.gabrielf.votacao_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);  // sem token, deixa passar
            return;

        }

        String token = authHeader.substring(7); // remove o prefixo "Bearer "
        String email = jwtService.extrairEmail(token);

        if (email != null & SecurityContextHolder.getContext().getAuthentication() == null) {
            UsuarioDetails usuarioDetails = (UsuarioDetails) userDetailsService.loadUserByUsername(email);


        if (jwtService.tokenValido(token, usuarioDetails)) {
            UsernamePasswordAuthenticationToken authToken =  new UsernamePasswordAuthenticationToken(
                    usuarioDetails, null, usuarioDetails.getAuthorities());
            authToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

        }

        }

        filterChain.doFilter(request, response);

    }
}

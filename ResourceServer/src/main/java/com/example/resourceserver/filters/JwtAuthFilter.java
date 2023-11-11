package com.example.resourceserver.filters;

import com.example.resourceserver.dto.TokenBody;
import com.example.resourceserver.entities.User;
import com.example.resourceserver.exceptions.UnfinishedUserException;
import com.example.resourceserver.services.impl.JwtServiceImpl;
import com.example.resourceserver.services.impl.UserServiceImpl;
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
public class JwtAuthFilter extends OncePerRequestFilter {
    private final UserServiceImpl userService;
    private final JwtServiceImpl jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (!(authHeader != null && authHeader.startsWith("Bearer "))) {
            filterChain.doFilter(request, response);

            return;
        }

        TokenBody tokenBody = jwtService.getTokenBody(authHeader.substring(7));

        if (!tokenBody.getTokenType().equals("access")) {
            filterChain.doFilter(request, response);

            return;
        }

        User user = userService.getByAuthIdOrCreateFromTokenBody(tokenBody);

        if (user.getFirstName().isEmpty() || user.getLastName().isEmpty()) {
            filterChain.doFilter(request, response);

            throw new UnfinishedUserException();
        }

        if (!(user != null && SecurityContextHolder.getContext().getAuthentication() == null)) {
            filterChain.doFilter(request, response);

            return;
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

package io.realworld.backend.infrastructure.security;

import io.realworld.backend.domain.aggregate.user.User;
import io.realworld.backend.domain.service.JwtService;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
  private static final String AUTH_HEADER = "Authorization";

  private final JwtService jwtService;

  @Autowired
  public JwtTokenFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      FilterChain filterChain)
      throws ServletException, IOException {
    getTokenString(httpServletRequest.getHeader(AUTH_HEADER))
        .ifPresent(
            (String token) -> {
              jwtService
                  .getUser(token)
                  .ifPresent(
                      (User user) -> {
                        UserDetails ud =
                            org.springframework.security.core.userdetails.User.withUsername(
                                    user.getEmail())
                                .password(user.getPasswordHash())
                                .authorities(Collections.emptyList())
                                .accountExpired(false)
                                .accountLocked(false)
                                .credentialsExpired(false)
                                .disabled(false)
                                .build();
                        UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(ud, token, ud.getAuthorities());
                        authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                      });
            });
    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }

  private Optional<String> getTokenString(String header) {
    if (header == null) {
      return Optional.empty();
    } else {
      final var split = header.split(" ");
      if (split.length < 2) {
        return Optional.empty();
      } else {
        return Optional.ofNullable(split[1]);
      }
    }
  }
}

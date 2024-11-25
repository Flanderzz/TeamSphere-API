package com.YipYapTimeAPI.YipYapTimeAPI.config;
import java.security.PublicKey;
import java.util.List;
import java.io.IOException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
public class JWTTokenValidator extends OncePerRequestFilter {
    private final PublicKey publicKey;

    private final JwtProperties jwtProperties;

    public JWTTokenValidator(PublicKey publicKey, JwtProperties jwtProperties) {
        this.publicKey = publicKey;
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String jwt =request.getHeader(JWTTokenConst.HEADER);

        if (jwt != null && jwt.startsWith("Bearer ")) {
            try {
                log.info("Validating the jwt");

                jwt = jwt.substring(7);

                Claims claim = Jwts.parserBuilder()
                        .setSigningKey(publicKey)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String audience = claim.getAudience();
                if (!jwtProperties.getAudience().equals(audience)) {
                    throw new JwtException("Invalid audience: " + audience);
                }

                String username = claim.getSubject();
                String authorities = claim.get("authorities", String.class);

                List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                Authentication auth = new UsernamePasswordAuthenticationToken(username, null ,auths);

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (ExpiredJwtException e) {
                log.warn("JWT token is expired: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is expired");
                return;
            } catch (JwtException e) {
                log.warn("Invalid JWT token: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                return;
            } catch (Exception e) {
                log.error("Unexpected error during JWT validation: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error during JWT validation");
                return;
            }
        } else {
            log.info("JWT token is missing or does not start with 'Bearer '");
        }
        // Continue with the filter chain if the JWT is valid or not present
        filterChain.doFilter(request, response);
    }
}

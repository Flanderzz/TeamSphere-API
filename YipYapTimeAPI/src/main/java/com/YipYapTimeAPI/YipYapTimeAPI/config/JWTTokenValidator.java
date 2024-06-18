package com.YipYapTimeAPI.YipYapTimeAPI.config;
import java.util.List;
import java.io.IOException;

import javax.crypto.SecretKey;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.GrantedAuthority;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
public class JWTTokenValidator extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String jwt =request.getHeader(JWTTokenConst.HEADER);

        log.info("Validating the jwt");

        if(jwt != null) {
            try {

                jwt=jwt.substring(7);

                SecretKey key=Keys.hmacShaKeyFor(JWTTokenConst.JWT_KEY.getBytes());

                Claims claim=Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

                String username=String.valueOf(claim.get("username"));
                String authorities=String.valueOf(claim.get("authorities"));

                List<GrantedAuthority> auths=AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                Authentication auth=new UsernamePasswordAuthenticationToken(username, null,auths);

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                log.info("invalid token recived...................");
                throw new BadCredentialsException("invalid token");
            }
        }
        filterChain.doFilter(request, response);
    }
}

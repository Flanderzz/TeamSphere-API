package com.YipYapTimeAPI.YipYapTimeAPI.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class JWTTokenProviderTest {

    private JWTTokenProvider jwtTokenProvider;

    private PrivateKey privateKey;

    private JwtProperties jwtProperties;

    @BeforeEach
    public void setUp() {
        jwtTokenProvider = new JWTTokenProvider(privateKey, jwtProperties);
    }

    @Test
    public void testGenerateJwtToken() {
        // Mock Authentication object
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");

        String token = jwtTokenProvider.generateJwtToken(authentication);

        // Validate token
        Claims claims= Jwts.parserBuilder()
                .setSigningKey(privateKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        assertEquals("test@example.com", claims.get("email"));
    }

    @Test
    public void testGetEmailFromToken() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");
        String token = "Bearer " + jwtTokenProvider.generateJwtToken(authentication);
        String email = jwtTokenProvider.getEmailFromToken(token);
        assertEquals("test@example.com", email);
    }

    @Test
    public void testPopulateAuthorities() {
        // Mock a collection of authorities
        Collection<GrantedAuthority> authorities = Arrays.asList(
                (GrantedAuthority) () -> "ROLE_USER",
                (GrantedAuthority) () -> "ROLE_ADMIN"
        );

        String populatedAuthorities = jwtTokenProvider.populateAuthorities(authorities);

        // Validate populated authorities
        assertEquals("ROLE_USER,ROLE_ADMIN", populatedAuthorities);
    }
}


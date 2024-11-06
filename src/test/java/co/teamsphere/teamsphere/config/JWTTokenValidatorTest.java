package co.teamsphere.teamsphere.config;

import co.teamsphere.teamsphere.config.JWTTokenConst;
import co.teamsphere.teamsphere.config.JWTTokenProvider;
import co.teamsphere.teamsphere.config.JWTTokenValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JWTTokenValidatorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JWTTokenValidator jwtTokenValidator;
    private JWTTokenProvider jwtTokenProvider;

    @Test
    public void testValidToken() throws IOException, ServletException {

        SecretKey key = Keys.hmacShaKeyFor(JWTTokenConst.JWT_KEY.getBytes());

        String token = Jwts.builder().setIssuer("YipYapTime")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+86400000))
                .claim("username", "testuser")
                .claim("authorities", "ROLE_USER")
                .signWith(key)
                .compact();

        when(request.getHeader(JWTTokenConst.HEADER)).thenReturn("Bearer "+token);


        jwtTokenValidator.doFilterInternal(request, response, filterChain);

        // Assert authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(authentication.getName(), "testuser");
        assertTrue(AuthorityUtils.authorityListToSet(authentication.getAuthorities()).contains("ROLE_USER"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void testInvalidToken() {
        when(request.getHeader(JWTTokenConst.HEADER)).thenReturn("Bearer invalidToken");
        assertThrows(BadCredentialsException.class, () -> jwtTokenValidator.doFilterInternal(request, response, filterChain));
    }
}
